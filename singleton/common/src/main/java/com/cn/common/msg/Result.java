package com.cn.common.msg;


import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 后端返回体信息封装
 *
 * @author bdth @github dulaiduwang003
 * @version 1.0
 */
@SuppressWarnings("all")
public class Result extends LinkedHashMap<String, Object> implements Serializable {


    private static final long serialVersionUID = 1L;

    public static final int CODE_SUCCESS = 200;

    public static final int CODE_ERROR = 500;


    public Result() {
    }

    public Result(int code, String msg, Object data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }


    public Result(Map<String, ?> map) {
        this.setMap(map);
    }

    public Integer getCode() {
        return (Integer) this.get("code");
    }

    public String getMsg() {
        return (String) this.get("msg");
    }


    public Object getData() {
        return this.get("data");
    }


    public Result setCode(int code) {
        this.put("code", code);
        return this;
    }


    public Result setMsg(String msg) {
        this.put("msg", msg);
        return this;
    }


    public Result setData(Object data) {
        this.put("data", data);
        return this;
    }

    public Result set(String key, Object data) {
        this.put(key, data);
        return this;
    }


    public Result setMap(Map<String, ?> map) {
        Iterator var2 = map.keySet().iterator();

        while (var2.hasNext()) {
            String key = (String) var2.next();
            this.put(key, map.get(key));
        }

        return this;
    }


    public static Result ok() {
        return new Result(CODE_SUCCESS, "操作成功", (Object) null);
    }

    public static Result ok(String msg) {
        return new Result(CODE_SUCCESS, msg, (Object) null);
    }

    public static Result code(int code) {
        return new Result(code, (String) null, (Object) null);
    }

    public static Result data(Object data) {
        return new Result(CODE_SUCCESS, "ok", data);
    }

    public static Result error() {
        return new Result(CODE_ERROR, "操作失败", (Object) null);
    }

    public static Result error(final String msg, final Integer code) {
        return new Result(code, msg, (Object) null);
    }

    public static Result error(String msg) {
        return new Result(CODE_ERROR, msg, (Object) null);
    }

    public static Result error(String msg, int code) {
        return new Result(code, msg, (Object) null);
    }

    public static Result build(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }


    public String toString() {
        return "{\"code\": " + this.getCode() + ", \"msg\": " + this.transValue(this.getMsg()) + ", \"data\": " + this.transValue(this.getData()) + "}";
    }


    private String transValue(Object value) {
        return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
    }
}
