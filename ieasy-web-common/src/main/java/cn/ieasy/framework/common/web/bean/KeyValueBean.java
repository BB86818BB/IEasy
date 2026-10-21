package cn.ieasy.framework.common.web.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lbt on 16/4/13.
 */

@Setter
@Getter
public class KeyValueBean {

    public KeyValueBean() {
    }

    public KeyValueBean(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;
}
