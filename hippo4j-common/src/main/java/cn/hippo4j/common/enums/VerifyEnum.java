package cn.hippo4j.common.enums;

import java.util.Objects;

public enum VerifyEnum {
    /**
     * unVerify
     */
    TO_VERIFY(0,"待审核"),

    /**
     * accept
     */
    VERIFY_ACCEPT(1,"审核通过"),

    /**
     * reject
     */
    VERIFY_REJECT(2,"审核拒绝");

    private final Integer verifyStatus;

    private final String desc;

    VerifyEnum(Integer verifyStatus,String desc) {
        this.verifyStatus = verifyStatus;
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public Integer getVerifyStatus() {
        return this.verifyStatus;
    }

}
