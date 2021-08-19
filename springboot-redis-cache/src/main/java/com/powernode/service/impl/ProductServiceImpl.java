package com.powernode.service.impl;

import com.powernode.mapper.ProductMapper;
import com.powernode.model.Product;
import com.powernode.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Product queryById(int id) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Product product = (Product) redisTemplate.opsForValue().get("product");
        if (product == null){
            synchronized (this){
                product = (Product) redisTemplate.opsForValue().get("product");
                if (product == null){
                    product = productMapper.selectById(id);
                    System.out.println("从数据库获得数据");
                    redisTemplate.opsForValue().set("product",product);
                }else {
                    System.out.println("从缓存中获取..." + System.currentTimeMillis());
                }
            }
        }else {
            System.out.println("从缓存中获取...");
        }
        return product;
    }
}
