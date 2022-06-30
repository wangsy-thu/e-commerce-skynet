package edu.neu.ecommerce.constant;

public class ObjectConstant {

    /**
     * 整形布尔值
     */
    public enum BooleanIntEnum {
        YES(1, "是"),
        NO(0, "否");
        private Integer code;
        private String desc;

        BooleanIntEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static boolean isValid(Integer code) {
            for (BooleanIntEnum obj : values()) {
                if (obj.code.equals(code))
                    return true;
            }
            return false;
        }

        public Integer getCode() {
            return this.code;
        }

        public String getDesc() {
            return this.desc;
        }
    }
}
