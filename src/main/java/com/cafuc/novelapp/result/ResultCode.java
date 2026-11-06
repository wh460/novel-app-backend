package com.cafuc.novelapp.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAIL(500, "服务器异常"),
    TOKEN_INVALID(401, "未登录或Token失效"),
    PARAM_ERROR(400, "请求参数错误");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
