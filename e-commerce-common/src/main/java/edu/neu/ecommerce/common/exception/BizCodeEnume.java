package edu.neu.ecommerce.common.exception;

public enum BizCodeEnume {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    TOO_MANY_REQUEST(10002, "请求流量过大"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，稍后再试"),
    USER_EXIST_EXCEPTION(15001, "用户存在"),
    PHONE_EXIST_EXCEPTION(15001, "手机号存在"),
    LOGINACCT_PASSWORD_INVALID(15003, "账号或密码错误"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足");
    

    private int code;
    private String msg;
    BizCodeEnume(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }


}
