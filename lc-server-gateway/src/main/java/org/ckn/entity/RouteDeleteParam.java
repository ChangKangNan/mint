package org.ckn.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author ckn
 * @date 2023/2/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteDeleteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "路由ID不能为空")
    private String routeId;
}
