package edu.neu.ecommerce.recommend.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.recommend.neo4j.NeoClient;
import edu.neu.ecommerce.vo.graph.BuyRelationVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RelationAddListener {

    @RabbitListener(queues = "buy.relation.add")
    public void listenerHandler(String buyRelationVoStr){
        BuyRelationVo buyRelationVo = JSON.parseObject(buyRelationVoStr, new TypeReference<BuyRelationVo>() {
        });
        try (NeoClient client = new NeoClient("bolt://localhost:7687", "neo4j", "123456")) {
            client.insertBuyRelation(buyRelationVo);
        }
    }
}
