package cn.ieasy.framework.base.dict.dto;

public class SortableTreeNodeDTO extends JsTreeNodeDTO {

    private int sortNum;

    public int getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(final int sortNum) {
        this.sortNum = sortNum;
    }
}
