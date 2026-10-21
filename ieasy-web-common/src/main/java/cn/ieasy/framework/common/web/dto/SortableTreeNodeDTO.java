package cn.ieasy.framework.common.web.dto;

import io.swagger.annotations.ApiModelProperty;

public class SortableTreeNodeDTO extends JsTreeNodeDTO {

    private static final long serialVersionUID = 8804189780556697676L;

    @ApiModelProperty(value = "序号", position = 3)
    private int sortNum;

    public int getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(final int sortNum) {
        this.sortNum = sortNum;
    }
}
