package com.miaoshaproject.error;

public enum EmBussinessError implements CommonError {
    //通用错误类型20001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),
    //2000开头为用户信息香港错误定义。
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAILURE(20002,"用户登录失败，用户名或密码错误"),
    USER_NOT_LOGIN(20003,"用户未登录"),
    // 30000 开头的为交易信息错误定义。
    STOCK_NOT_ENOUGH(300001,"库存不足");
    ;

    private EmBussinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    private int errCode;
    private String errMsg;
    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
