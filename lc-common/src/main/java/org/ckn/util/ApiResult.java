package org.ckn.util;

import java.io.Serializable;

/**
 * 全局请求体
 * @author ckn
 * @date 2023/2/22
 */
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    private int code ;
    /**
     * 响应消息内容
     */
    private String msg;
    /**
     * 响应消息体
     */
    private T data;
    private ApiResult(){}

    /**
     * 成功响应
     * @param data 响应消息体
     * @param <T> 类型
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setCode(ResponseCodeConstants.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 成功响应
     * @param data 响应消息体
     * @param <T> 类型
     * @param msg 响应消息内容
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> success(T data, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setCode(ResponseCodeConstants.SUCCESS);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败响应
     * @return ApiResult对象
     */
    public static  ApiResult error() {
        ApiResult result = new ApiResult();
        result.setCode(ResponseCodeConstants.ERROR);
        return result;
    }

    /**
     * 失败响应
     * @param msg 响应消息内容
     * @return ApiResult对象
     */
    public static  ApiResult error(String msg) {
        ApiResult result = new ApiResult();
        result.setCode(ResponseCodeConstants.ERROR);
        result.setMsg(msg);
        return result;
    }

    /**
     * 自定义返回体
     * @param code 响应状态码
     * @param <T> 类型
     * @param data 响应消息体
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> customize(int code, T data) {
        ApiResult<T> result = new ApiResult<T>();
        result.setCode(code);
        result.setData(data);
        return result;
    }

    /**
     * 自定义返回体
     * @param code 响应状态码
     * @param <T> 类型
     * @param data 响应消息体
     * @param msg 响应消息内容
     * @return ApiResult对象
     */
    public static <T> ApiResult<T> customize(int code, T data, String msg) {
        ApiResult<T> result = new ApiResult<T>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }


    /**
     * 自定义返回体
     * @param code 响应状态码
     * @param msg 响应消息内容
     * @return ApiResult对象
     */
    public static  ApiResult customize(int code, String msg) {
        ApiResult result = new ApiResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
