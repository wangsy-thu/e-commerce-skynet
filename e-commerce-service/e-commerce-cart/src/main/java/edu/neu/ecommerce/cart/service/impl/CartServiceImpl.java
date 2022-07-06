package edu.neu.ecommerce.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.cart.feign.ProductFeignService;
import edu.neu.ecommerce.cart.interceptor.CartInterceptor;
import edu.neu.ecommerce.cart.service.CartService;
import edu.neu.ecommerce.cart.vo.Cart;
import edu.neu.ecommerce.cart.vo.CartItem;
import edu.neu.ecommerce.cart.vo.SkuInfoVo;
import edu.neu.ecommerce.cart.vo.UserInfoTo;
import edu.neu.ecommerce.constant.CartConstant;
import edu.neu.ecommerce.constant.ObjectConstant;
import edu.neu.ecommerce.utils.DateUtils;
import edu.neu.ecommerce.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final StringRedisTemplate redisTemplate;

    private final ProductFeignService productFeignService;

    private final ThreadPoolExecutor executor;

    public CartServiceImpl(StringRedisTemplate redisTemplate,
                           ProductFeignService productFeignService,
                           ThreadPoolExecutor executor) {
        this.redisTemplate = redisTemplate;
        this.productFeignService = productFeignService;
        this.executor = executor;
    }

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 获取购物车redis操作对象
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        // 获取商品
        // 多线程的方式获取，速度更快
        String cartItemJSONString = (String) operations.get(skuId.toString());
        CartItem cartItem;
        if (StringUtils.isEmpty(cartItemJSONString)) {
            // 购物车不存在此商品，需要将当前商品添加到购物车中
            cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                // 远程查询当前商品信息
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setSkuId(skuInfo.getSkuId());// 商品ID
                cartItem.setTitle(skuInfo.getSkuTitle());// 商品标题
                cartItem.setImage(skuInfo.getSkuDefaultImg());// 商品默认图片
                cartItem.setPrice(skuInfo.getPrice());// 商品单价
                cartItem.setCount(num);// 商品件数
                cartItem.setCheck(true);// 是否选中
            }, executor);

            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                // 远程查询attrName:attrValue信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
        } else {
            // 当前购物车已存在此商品，修改当前商品数量
            cartItem = JSON.parseObject(cartItemJSONString, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
        }
        operations.put(skuId.toString(), JSON.toJSONString(cartItem));
        return cartItem;
    }

    /**
     * 根据用户信息获取购物车redis操作对象
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 获取用户登录信息
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        String cartKey;
        if (userInfo.getUserId() != null) {
            // 登录态，使用用户购物车
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
        } else {
            // 非登录态，使用游客购物车
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
        }
        // 绑定购物车的key操作Redis
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 根据skuId获取购物车商品信息
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        // 获取购物车redis操作对象
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJSONString = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(cartItemJSONString, CartItem.class);
    }

    /**
     * 获取购物车列表
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        // 获取用户登录信息
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        // 获取游客购物车
        List<CartItem> touristItems = getCartItems(CartConstant.CART_PREFIX + userInfo.getUserKey());
        if (userInfo.getUserId() != null) {
            // 1.登录状态
            if (!CollectionUtils.isEmpty(touristItems)) {
                // 2.游客购物车非空，需要整合到用户购物车
                for (CartItem item : touristItems) {
                    // 将商品逐个放到用户购物车
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 清楚游客购物车
                clearCart(CartConstant.CART_PREFIX + userInfo.getUserKey());
            }
            // 3.获取用户购物车（已经合并后的购物车）
            List<CartItem> items = getCartItems(CartConstant.CART_PREFIX + userInfo.getUserId());
            cart.setItems(items);
        } else {
            // 未登录状态，返回游客购物车
            cart.setItems(touristItems);
        }
        return cart;
    }

    /**
     * 根据购物车的key获取
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (!CollectionUtils.isEmpty(values)) {
            // 购物车非空，反序列化成商品并封装成集合返回
            return values.stream()
                    .map(jsonString -> JSONObject.parseObject((String) jsonString, CartItem.class))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 更改购物车商品选中状态
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        // 查询购物车商品信息
        CartItem cartItem = getCartItem(skuId);
        // 修改商品选中状态
        cartItem.setCheck(ObjectConstant.BooleanIntEnum.YES.getCode().equals(check));
//        System.out.println(cartItem.getCheck());
        // 更新到redis中
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.put(skuId.toString(), JSONObject.toJSONStringWithDateFormat(cartItem, DateUtils.DATATIMEF_TIME_STR));
    }

    /**
     * 改变商品数量
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {
        // 查询购物车商品信息
        CartItem cartItem = getCartItem(skuId);
        // 修改商品数量
        cartItem.setCount(num);
        // 更新到redis中
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.put(skuId.toString(), JSONObject.toJSONStringWithDateFormat(cartItem, DateUtils.DATATIMEF_TIME_STR));
    }

    /**
     * 删除购物项
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        List<CartItem> cartItemList;
        //获取当前用户登录的信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //如果用户未登录直接返回null
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            //获取所有的购物车项
            List<CartItem> cartItems = getCartItems(cartKey);
            //筛选出选中的购物车项
            assert cartItems != null;
            cartItemList = cartItems.stream()
                    .filter(CartItem::getCheck)
                    .peek(item -> {
                        //更新为最新的价格（查询数据库）
                        String price = (String)productFeignService.getPrice(item.getSkuId()).get("data");
                        item.setPrice(new BigDecimal(price));
                    })
                    .collect(Collectors.toList());
        }
        return cartItemList;
    }
}
