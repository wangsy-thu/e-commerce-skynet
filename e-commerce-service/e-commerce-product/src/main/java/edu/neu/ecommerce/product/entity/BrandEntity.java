package edu.neu.ecommerce.product.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.neu.ecommerce.validator.group.AddGroup;
import edu.neu.ecommerce.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "新增时必须为空", groups = {AddGroup.class})
	@NotNull(message = "修改必须指定品牌ID", groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名称必须提交", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "/^[a-zA-Z]$/")
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0, message = "排序信息必须为正整数")
	private Integer sort;

}
