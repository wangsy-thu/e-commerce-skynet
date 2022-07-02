package edu.neu.ecommerce.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车里面的购物项的详细信息
 */
public class CartItem {
    private Long skuId;                     // skuId
    private Boolean check = true;           // 是否选中
    private String title;                   // 标题
    private String image;                   // 图片
    private List<String> skuAttrValues;     // 销售属性
    private BigDecimal price;               // 单价
    private Integer count;                  // 商品件数
    private BigDecimal totalPrice;          // 总价

    // 访问器
    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttrValues() {
        return skuAttrValues;
    }

    public void setSkuAttrValues(List<String> skuAttrValues) {
        this.skuAttrValues = skuAttrValues;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算当前购物项总价
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal("" + this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
