package org.ckn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SecurityUtils {

    private ThreadLocal<Map<String, Object>> threadLocal;

    private SecurityUtils() {
        this.threadLocal = new ThreadLocal<>();
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static SecurityUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类单例模式
     * 单例初使化
     */
    private static class SingletonHolder {
        private static final SecurityUtils sInstance = new SecurityUtils();
    }

    /**
     * 用户上下文中放入信息
     *
     * @param map
     */
    public void setContext(Map<String, Object> map) {
        threadLocal.set(map);
    }

    /**
     * 获取上下文中的信息
     *
     * @return
     */
    public Map<String, Object> getContext() {
        return threadLocal.get();
    }

    /**
     * 获取上下文中的用户名
     *
     * @return
     */
    public String getUsername() {
        return String.valueOf(Optional.ofNullable(threadLocal.get()).orElse(new HashMap<>()).get("username"));
    }

    public String getUserId() {
        return String.valueOf(Optional.ofNullable(threadLocal.get()).orElse(new HashMap<>()).get("id"));
    }
    /**
     * 清空上下文
     */
    public void clear() {
        threadLocal.remove();
    }

}
