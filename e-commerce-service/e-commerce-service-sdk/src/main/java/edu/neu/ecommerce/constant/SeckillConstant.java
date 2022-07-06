package edu.neu.ecommerce.constant;

public class SeckillConstant {

    // 秒杀商品上架功能的锁
    public static final String UPLOAD_LOCK = "seckill:upload:lock";
    // 秒杀场次key
    public static final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    // 秒杀商品key
    public static final String SECKILL_CHARE_KEY = "seckill:skus";
    // 商品随机码
    public static final String SKU_STOCK_SEMAPHORE = "seckill:stock:";
    // 用户占位key
    public static final String SECKILL_USER_PREFIX = "seckill:user:";

}
