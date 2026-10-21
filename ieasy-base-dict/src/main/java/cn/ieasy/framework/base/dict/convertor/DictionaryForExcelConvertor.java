package cn.ieasy.framework.base.dict.convertor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import cn.ieasy.framework.base.dict.model.Dictionary;
import cn.ieasy.framework.base.dict.model.DictionaryCategory;
import cn.ieasy.framework.common.web.utils.CalendarUtil;

@Component
public final class DictionaryForExcelConvertor {
    protected static final Logger logger = LoggerFactory.getLogger(DictionaryForExcelConvertor.class);

    public final List<DictionaryCategory> parse(final InputStream inputStream) {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);

            final List<DictionaryCategory> dictionaryCategories = new ArrayList<DictionaryCategory>();
            final Sheet dcsSheet = workbook.getSheetAt(0);

            int rowNumber = 0;
            Row row = null;

            final Map<String, Dictionary> dicts = new HashMap<String, Dictionary>();
            row = dcsSheet.getRow(rowNumber++);
            while (rowNumber <= dcsSheet.getLastRowNum()) {
                if (this.isDictionaryCategory(row)) {
                    final DictionaryCategory dictionaryCategory = this.parseToDictionaryCategory(row);
                    dictionaryCategories.add(dictionaryCategory);

                    rowNumber++; // Skip Dict Header
                    while (rowNumber <= dcsSheet.getLastRowNum()) {
                        row = dcsSheet.getRow(rowNumber++);
                        if (this.isDictionaryCategory(row)) {
                            break;
                        } else {
                            final Dictionary dictionary = this.parseToDictionary(dictionaryCategory, dicts, row);
                            if (dictionary == null) {
                                break;
                            }
                        }
                    }
                } else {
                    throw new RuntimeException(
                            "[" + row.getSheet().getSheetName() + "]第" + (row.getRowNum() + 1) + "行内容错误： 需要码表类型数据。");
                }
            }
            return dictionaryCategories;
        } catch (final Exception e) {
            DictionaryForExcelConvertor.logger.error("文件数据导入失败，", e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (final IOException e) {
            }
        }
        return new ArrayList<DictionaryCategory>();
    }

    private boolean isDictionaryCategory(final Row row) {
        final int columnNumber = 0;

        final String value = this.getCellStringValue(row, columnNumber, true);

        return "-".equals(value);
    }

    private DictionaryCategory parseToDictionaryCategory(final Row row) {
        int columnNumber = 1;

        final String key = this.getCellStringValue(row, columnNumber, true);
        columnNumber++;
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        final DictionaryCategory dictionaryCategory = new DictionaryCategory();

        final String description = this.getCellStringValue(row, columnNumber + 1, false);

        dictionaryCategory.setKey(key);
        dictionaryCategory.setDescription(description);

        return dictionaryCategory;
    }

    private Dictionary parseToDictionary(final DictionaryCategory dictionaryCategory,
            final Map<String, Dictionary> dicts, final Row row) {
        int columnNumber = 1;

        final String dictText = this.getCellStringValue(row, columnNumber, false);
        columnNumber++;
        final String dictKey = this.getCellStringValue(row, columnNumber, false);
        columnNumber++;
        final String dictValue = this.getCellStringValue(row, columnNumber, true);
        columnNumber++;
        final String dictDescription = this.getCellStringValue(row, columnNumber, true);
        columnNumber++;
        final boolean dictEditable = this.getCellBooleanValue(row, columnNumber, false);
        columnNumber++;
        this.getCellStringValue(row, columnNumber, true);
        columnNumber++;
        final String dictParentKey = this.getCellStringValue(row, columnNumber, true);
        columnNumber++;

        final Dictionary dict = new Dictionary();
        dict.setKey(dictKey);
        dict.setValue(dictValue);
        dict.setText(dictText);
        dict.setDescription(dictDescription);
        dict.setEditable(dictEditable);
        dict.setCategory(dictionaryCategory);

        dictionaryCategory.getDictionaries().add(dict);
        dicts.put(dictKey, dict);

        Dictionary parent = null;
        if (StringUtils.hasText(dictParentKey)) {
            parent = dicts.get(dictParentKey);
            if (parent == null) {
                throw new RuntimeException(
                        "[" + row.getSheet().getSheetName() + "]第" + (row.getRowNum() + 1) + "行内容错误： 码表父节点不存在，顺序可能错误。");
            }
            dict.setParent(parent);
            parent.getChildren().add(dict);
        }

        return dict;
    }

    protected boolean getCellBooleanValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        final String value = this.getCellStringValue(row, columnNumber, emptyEnabled);
        if (value == null) {
            return false;
        }

        return value.equalsIgnoreCase("true");
    }

    protected String getCellStringValue(final Row row, final int columnNumber) {
        return this.getCellStringValue(row, columnNumber, false);
    }

    protected String getCellStringValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        Cell cell = row.getCell(columnNumber);
        if (!emptyEnabled && ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK))) {
            cell = row.createCell(columnNumber);
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 内容为空。");
        }
        if ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK)) {
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            final Double numericCellValue = cell.getNumericCellValue();
            final String numStr = String.valueOf(numericCellValue.longValue());
            return numStr;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            final boolean booleanCellValue = cell.getBooleanCellValue();
            final String str = String.valueOf(booleanCellValue);
            return str;
        }

        if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 内容格式不是文本内容。");
        }
        final String value = cell.getStringCellValue();
        if (!emptyEnabled && StringUtils.isEmpty(value)) {
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 内容为空。");
        }

        return value.trim();
    }

    protected Integer getCellIntegerValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        final Double value = this.getCellNumbericValue(row, columnNumber, emptyEnabled);
        if (value == null) {
            return null;
        }

        return value.intValue();
    }

    protected Double getCellDoubleValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        return this.getCellNumbericValue(row, columnNumber, emptyEnabled);
    }

    protected Double getCellNumbericValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        Cell cell = row.getCell(columnNumber);
        if (!emptyEnabled && ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK))) {
            cell = row.createCell(columnNumber);
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 内容为空。");
        }
        if ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK)) {
            return null;
        }

        try {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                final String cellStringValue = this.getCellStringValue(row, columnNumber, true);
                if (StringUtils.isEmpty(cellStringValue)) {
                    return null;
                }
                final Double result = Double.valueOf(cellStringValue.trim());
                return result;
            }
        } catch (final Exception e) {
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 数据类型不正确，需要数值。");
        }
    }

    protected Date getCellDateValue(final Row row, final int columnNumber, final boolean emptyEnabled) {
        Cell cell = row.getCell(columnNumber);
        if (!emptyEnabled && ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK))) {
            cell = row.createCell(columnNumber);
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 内容为空。");
        }
        if ((cell == null) || (cell.getCellType() == Cell.CELL_TYPE_BLANK)) {
            return null;
        }

        try {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return DateUtil.getJavaDate(cell.getNumericCellValue());
                }
            } else {
                final String cellStringValue = this.getCellStringValue(row, columnNumber, true);
                if (StringUtils.isEmpty(cellStringValue)) {
                    return null;
                }
                final Date result = CalendarUtil.simpleParse(cellStringValue.trim());
                return result;
            }
        } catch (final Exception e) {
            throw new RuntimeException("[" + cell.getSheet().getSheetName() + "]第" + (cell.getRow().getRowNum() + 1)
                    + "行第" + Character.toString((char) ('A' + cell.getColumnIndex())) + "列内容错误： 日期内容不正确。");
        }
    }
}
