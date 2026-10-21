package cn.ieasy.framework.common.jpa.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class test implements Serializable {
    private String a;
    private String b;

    public String getA() {
        return this.a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public String getB() {
        return this.b;
    }

    public void setB(final String b) {
        this.b = b;
    }

}
