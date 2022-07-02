package edu.neu.ecommerce.cart.vo;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 *  需要计算的属性，必须重写他的get方法，保证每次获取属性都会进行计算
 */
public class Cart {

    private List<CartItem> items; // 购物购项集合
    private Integer countNum;       // 商品件数（汇总购物车内商品总件数）
    private Integer countType;      // 商品数量（汇总物车内商品总个数）
    private BigDecimal totalAmount; // 商品总价（商品总价-优惠总价）
    private BigDecimal reduce = new BigDecimal("0.00");// 减免价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        return CollectionUtils.isEmpty(items) ? 0 : items.size();
    }


    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        // 1、计算购物项总价
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem cartItem : items) {
                if (cartItem.getCheck()) {
                    amount = amount.add(cartItem.getTotalPrice());
                }
            }
        }
        // 2、计算优惠后的价格
        return amount.subtract(getReduce());
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
    
}
