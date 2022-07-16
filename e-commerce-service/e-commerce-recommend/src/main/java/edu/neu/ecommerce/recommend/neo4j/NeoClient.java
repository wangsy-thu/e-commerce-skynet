package edu.neu.ecommerce.recommend.neo4j;

import edu.neu.ecommerce.vo.graph.BuyRelationVo;
import edu.neu.ecommerce.vo.graph.ProductNodeVo;
import edu.neu.ecommerce.vo.graph.UserNodeVo;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class NeoClient implements AutoCloseable {

    private final Driver driver;

    public NeoClient(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }

    /**
     * <h1>判断User结点是否存在</h1>
     * @param id 用户ID
     * @return 是否存在
     */
    public boolean existUser(Long id) {
        try (Session session = driver.session()) {
            String cypher = "MATCH (a:USER) WHERE a.userId = $uId RETURN a";
            return session.readTransaction(tx -> {
                Result result = tx.run(cypher,
                        parameters("uId", id));
                return result.hasNext();
            });
        }
    }

    /**
     * <h2>判断商品节点是否存在</h2>
     * @param id 节点ID
     * @return 是否存在
     */
    public boolean existProduct(Long id){
        try (Session session = driver.session()){
            String cypher = "MATCH (p:PRODUCT) where p.skuId = $skuId return p";
            return session.readTransaction(tx -> tx.run(cypher,
                    parameters("skuId", id)).hasNext());
        }
    }

    /**
     * <h2>插入用户节点到图数据库</h2>
     *
     * @param userNodeVo 用户节点VO
     */
    public void insertUser(UserNodeVo userNodeVo){
        try (Session session = driver.session()){
            String cypher = "CREATE (u:USER {userId: $userId, username: $username}) return u";
            session.writeTransaction(tx -> tx.run(cypher,
                    parameters("userId", userNodeVo.getUserId(),
                            "username", userNodeVo.getUsername())).hasNext());
        }
    }

    /**
     * <h2>判断是否存在购买关系</h2>
     * @param userId 用户ID
     * @param skuId 产品ID
     * @return 是否存在关联
     */
    public boolean existBuyRelation(Long userId, Long skuId){
        try(Session session = driver.session()){
            String cypher = "MATCH (a:USER) -[r:BUY]-> (b:PRODUCT) WHERE a.userId = $userId AND b.skuId = $skuId RETURN r";
            return session.readTransaction(tx -> tx.run(cypher,
                    parameters("userId", userId, "skuId", skuId)).hasNext());
        }
    }

    /**
     * <h2>插入产品节点信息</h2>
     *
     * @param productNodeVo 产品节点值对象
     */
    public void insertProduct(ProductNodeVo productNodeVo){
        try (Session session = driver.session()){
            String cypher = "CREATE (p:PRODUCT {skuId: $skuId, skuName:$skuName, catalogId:$catalogId, catalogName: $catalogName}) RETURN p";
            session.writeTransaction(tx -> tx.run(cypher,
                    parameters(
                            "skuId", productNodeVo.getSkuId(),
                            "skuName", productNodeVo.getSkuName(),
                            "catalogId", productNodeVo.getCatalogId(),
                            "catalogName", productNodeVo.getCatalogName()
                    )).hasNext());
        }
    }

    /**
     * <h1>插入购买关系</h1>
     *
     * @param buyRelationVo 购买关系
     */
    public void insertBuyRelation(BuyRelationVo buyRelationVo){
        try (Session session = driver.session()){
            String cypher = "MATCH (u:USER), (p:PRODUCT) WHERE u.userId = $userId and p.skuId = $skuId CREATE (u) -[r:BUY {buyCount:1}]-> (p) return r";
            session.writeTransaction(tx -> tx.run(cypher, parameters(
                    "userId", buyRelationVo.getUserId(),
                    "skuId", buyRelationVo.getSkuId()
            )).hasNext());
        }
    }

    /**
     * <h2>更新购买数量</h2>
     *
     * @param userId 用户ID
     * @param skuId  商品ID
     */
    public void updateBuyCount(Long userId, Long skuId){
        try (Session session = driver.session()){
            String cypher = "MATCH (u:USER) -[r:BUY]-> (p:PRODUCT) WHERE u.userId = $userId AND p.skuId = $skuId SET r.buyCount = r.buyCount + 1 return r";
            session.writeTransaction(tx -> tx.run(cypher, parameters(
                    "userId", userId,
                    "skuId", skuId
            )).hasNext());
        }
    }

    /**
     * <h2>更新节点信息</h2>
     * @param buyRelationVo 购买关系
     */
    public void updateGraph(BuyRelationVo buyRelationVo){
        Long userId = buyRelationVo.getUserId();
        Long skuId = buyRelationVo.getSkuId();
        if(existBuyRelation(userId, skuId)){
            updateBuyCount(userId, skuId);
        }else{
            if(!existProduct(skuId)){
                insertProduct(buyRelationVo.getProductNodeVo());
            }
            if(!existUser(userId)){
                insertUser(buyRelationVo.getUserNodeVo());
            }
            insertBuyRelation(buyRelationVo);
        }
    }

    /**
     * <h2>推荐</h2>
     * 购买过该商品的人还买过啥
     * @param skuId 商品ID
     * @return 推荐结果
     */
    public List<ProductNodeVo> getAlsoBuy(Long skuId){
        List<ProductNodeVo> resultList = new ArrayList<>();
        try (Session session = driver.session()){
            String cypher = "MATCH (p1:PRODUCT) <-- (u1:USER) --> (p2:PRODUCT) WHERE p1.skuId = $skuId RETURN p2";
            session.readTransaction(tx -> {
                Result result = tx.run(cypher, parameters("skuId", skuId));
                while(result.hasNext()){
                    Node node = result.next().get(0).asNode();
                    Long proId = Long.parseLong(node.get("skuId").toString());
                    String skuName = node.get("skuName").toString();
                    Long catalogId = Long.parseLong(node.get("catalogId").toString());
                    String catalogName = node.get("catalogName").toString();
                    resultList.add(new ProductNodeVo(
                            proId,skuName, catalogId, catalogName
                    ));
                }
                return "ok";
            });
        }
        return resultList;
    }
}
