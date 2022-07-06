package edu.neu.ecommerce.ware.exception;

public class NoStockException extends RuntimeException{
    public NoStockException(Long skuId){
        super("商品id"+skuId+"没有足够库存了");
    }
}
