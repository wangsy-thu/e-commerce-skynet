package edu.neu.ecommerce.vo.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>图数据库用户节点值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNodeVo {
    /* 用户ID */
    private Long userId;

    /* 用户名称 */
    private String username;
}
