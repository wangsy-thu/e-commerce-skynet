package edu.neu.ecommerce.search.listener;

import edu.neu.ecommerce.search.service.ProductSaveService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;

/**
 * <h1>商品下架监听器</h1>
 */
@Service
public class SkuDownListener {

    private final ProductSaveService productSaveService;

    public SkuDownListener(ProductSaveService productSaveService) {
        this.productSaveService = productSaveService;
    }

    @RabbitListener(queues = "sku.down.ids")
    public void listenerHandler(Long skuId){
        productSaveService.productStatusDown(skuId);
    }
}
