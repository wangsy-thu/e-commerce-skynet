package edu.neu.ecommerce.product.service.impl;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.product.dao.CategoryDao;
import edu.neu.ecommerce.product.entity.CategoryEntity;
import edu.neu.ecommerce.product.service.CategoryBrandRelationService;
import edu.neu.ecommerce.product.service.CategoryService;
import edu.neu.ecommerce.product.vo.Catelog2Vo;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService relationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2.组装成父子的树形结构
        //2.1.找到所有的一级分类
        return entities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).peek(menu -> menu.setChildren(getChildren(menu, entities)))
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        return all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, all)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO: 检查当前删除的菜单，是否被别的地方引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long attrGroupId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(attrGroupId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    @Override
    @CacheEvict(value = "category", allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        relationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    @Cacheable(value = {"category"}, key = "#root.method.name")
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("get level1");
        return this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0)
        );
    }

    //TODO:产生堆外内存溢出
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        //1，空结果缓存-缓存穿透
        //2，设置随机过期时间-缓存雪崩
        //3，加锁-缓存击穿问题
        //只要是同一把锁，就能锁住
        //1，加入缓存逻辑[序列化与反序列化]
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtils.isEmpty(catalogJSON)){
            //2，缓存中没有，查询数据库
            System.out.println("缓存未命中，查询数据库");
            //3，将查到的数据放入缓存，将对象转为JSON，缓存中存的都是JSON字符串
            //跨语言，跨平台的兼容性
            return getCatelogJsonFromDbWithRedissonLock();
        }
        //转为指定的对象
        System.out.println("缓存命中，直接返回");
        return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedissonLock() {

        //锁的名字，定义
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {

            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        log.debug("get data:" + dataFromDb);
        return dataFromDb;
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithRedisLock() {

        //1，占用分布式锁，去redis占坑，并设置过期时间
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue()
                .setIfAbsent("lock", uuid, 300, TimeUnit.MILLISECONDS);
        if(Boolean.TRUE.equals(lock)){
            //加锁成功，设置过期时间
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //使用LUA脚本解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                //原子性删除锁
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class)
                        , Collections.singletonList("lock"), uuid);
            }

            return dataFromDb;
        }else {
            //加锁失败，重试
            try{
                Thread.sleep(200);
            } catch (Exception e){
                e.printStackTrace();
            }
            return getCatelogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(!StringUtils.isEmpty(catalogJSON)){
            return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        //1，查出所有的分类
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1，每一个一级分类，查询到二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null && categoryEntities.size() > 0) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString()
                            , null
                            , l2.getCatId().toString()
                            , l2.getName());
                    //找当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //封装成指定格式
                            return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    /**
     * <h2>从数据库表查询并封装</h2>
     * @return 搜索结果
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDbWithLocalLock() {

        //加入缓存功能
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        synchronized (this){
            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
            if(StringUtils.isEmpty(catalogJSON)){
                return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            }
            //1，查出所有的分类
            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //1，每一个一级分类，查询到二级分类
                List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (categoryEntities != null && categoryEntities.size() > 0) {
                    catelog2Vos = categoryEntities.stream().map(l2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString()
                                , null
                                , l2.getCatId().toString()
                                , l2.getName());
                        //找当前二级分类的三级分类，封装成vo
                        List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                        if (level3Catelog != null) {
                            List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                //封装成指定格式
                                return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect);
                        }
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String s = JSON.toJSONString(parent_cid);
            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
            return parent_cid;
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,
                                               Long parent_cid) {
        return selectList.stream()
                .filter(item -> Objects.equals(item.getParentCid(), parent_cid))
                .collect(Collectors.toList());
    }
}