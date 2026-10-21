package cn.ieasy.framework.common.web.view.example;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import cn.ieasy.framework.common.web.view.BaseExcelView;

// http://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-view-resolvers
// 使用BeanNameViewResolver
@Component
public class ExcelViewForPoc extends BaseExcelView {

    public ExcelViewForPoc() {
        //可以使用预做成的模板，这个例子没有使用
        //        super.setUrl("classpath:/excel/Excel文件模板");
    }

    @Override
    protected void buildExcelDocumentContents(final Map<String, Object> model, final Workbook workbook,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final CellStyle defaultCellStyle = this.buildDefaultCellStyle(workbook);

        @SuppressWarnings("unchecked")
        final Map<String, String> revenueData = (Map<String, String>) model.get("revenueData");

        final Sheet sheet = workbook.createSheet("Revenue Report");

        int rowNumber = 0;
        int colNumber = 0;

        Cell cell = this.buildMergedRowCell(sheet, rowNumber, colNumber++, defaultCellStyle);
        cell.setCellValue("Month");

        cell = this.buildMergedRowCell(sheet, rowNumber, colNumber++, defaultCellStyle);
        cell.setCellValue("Revenue");

        for (final Map.Entry<String, String> entry : revenueData.entrySet()) {
            rowNumber++;
            colNumber = 0;

            cell = this.buildMergedRowCell(sheet, rowNumber, colNumber++, defaultCellStyle);
            cell.setCellValue(entry.getKey());

            cell = this.buildMergedRowCell(sheet, rowNumber, colNumber++, defaultCellStyle);
            cell.setCellValue(entry.getValue());
        }

    }
}
