package org.ckn.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 网关路由
 * </p>
 *
 * @author ckn
 * @since 2023-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GatewayRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 路由id
     */
    private String routeId;

    /**
     * 请求地址
     */
    private String uri;

    /**
     * 断言
     */
    private String predicates;

    /**
     * 拦截器
     */
    private String filters;

    /**
     * 附加参数
     */
    private String metadata;

    /**
     * 执行顺序,数值越小优先级越高
     */
    private Integer filterOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除控制
     */
    @TableLogic
    private Boolean deleted;
}
