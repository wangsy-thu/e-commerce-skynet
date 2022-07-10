package edu.neu.ecommerce.statistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.statistics.dao.SkuClickDao;
import edu.neu.ecommerce.statistics.entity.SkuClickEntity;
import edu.neu.ecommerce.statistics.service.SkuClickService;
import org.springframework.stereotype.Service;

@Service
public class SkuClickServiceImpl extends ServiceImpl<SkuClickDao, SkuClickEntity> implements SkuClickService {

}
