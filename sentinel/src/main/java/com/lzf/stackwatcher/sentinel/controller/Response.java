package com.lzf.stackwatcher.sentinel.controller;

class Response {

    private int code;

    private String msg;

    public Response() { }

    public Response(int code, String msg) {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
