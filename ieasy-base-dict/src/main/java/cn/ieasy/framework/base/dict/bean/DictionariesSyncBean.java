package cn.ieasy.framework.base.dict.bean;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionariesSyncBean {

    private Map<String, DictionaryCategoryBean> dictionaries;

    private Long version;
}
