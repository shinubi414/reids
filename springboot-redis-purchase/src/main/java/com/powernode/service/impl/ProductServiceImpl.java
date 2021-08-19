package com.powernode.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.powernode.mapper.ProductMapper;
import com.powernode.mapper.PurchaseRecordMapper;
import com.powernode.model.Product;
import com.powernode.model.PurchaseRecord;
import com.powernode.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    PurchaseRecordMapper purchaseRecordMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public boolean purchase(int id, int quantity, int userId) {

        //根据Id查询产品库存信息
        Product product = productMapper.selectById(id);
        //判断该产品是否存在
        if (product != null) {
            //判断库存是否充足
            if (product.getStock() >= quantity) {
                //更新库存数量
                int result = productMapper.updateStock(quantity, id);
                if (result > 0) {
                    //初始化购买记录
                    PurchaseRecord purchaseRecord = initPurchaseRecord(id, quantity, userId, product.getPrice());
                    //添加购买记录
                    result += purchaseRecordMapper.insertRecord(purchaseRecord);
                    if (result > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void addRedis(int id) {
        Product product = productMapper.selectById(id);

        //记录产品库存
        redisTemplate.opsForHash().put("product-stock", id + "", product.getStock() + "");
        //记录产品信息
        redisTemplate.opsForHash().put("product", id + "", product);

    }

    @Override
    public boolean purchaseRedis(int id, int quantity, int userId) {
        synchronized (Product.class) {
            //从redis中获取产品库存
            int stock = Integer.parseInt(redisTemplate.opsForHash().get("product-stock", id + "").toString());
            //从redis中获取产品销售数量
            Object o = redisTemplate.opsForHash().get("product-sales", id + "");
            int sales = 0;
            if (o != null) {
                sales = Integer.parseInt(o.toString());
            } else {
                redisTemplate.opsForHash().put("product-sales", id + "", "0");
            }
            if (stock >= sales + quantity) {
                //修改产品销售数量
                redisTemplate.opsForHash().put("product-sales", id + "", (sales + quantity));
                //从redis中获取产品信息
                Object object = redisTemplate.opsForHash().get("product", id + "");
                Product product = JSON.parseObject(JSON.toJSONString(object), Product.class);
                //初始化购买记录
                PurchaseRecord purchaseRecord = initPurchaseRecord(id, quantity, userId, product.getPrice());
                //添加购买记录
                redisTemplate.opsForList().leftPush("product-record", purchaseRecord);
                return true;
            }
            return false;
        }
    }

    @Override
    public void adddb() {
        //获取购买记录
        List<Object> purchaseRecords = redisTemplate.opsForList().range("product-record",0,-1);
        //将购买记录添加进数据库
        for (Object object : purchaseRecords) {
            PurchaseRecord purchaseRecord = JSONObject.parseObject(JSON.toJSONString(object),PurchaseRecord.class);
            productMapper.updateStock(purchaseRecord.getQuantity(),purchaseRecord.getProductId());
            purchaseRecordMapper.insertRecord(purchaseRecord);
            redisTemplate.opsForList().remove("product-record",0,purchaseRecord);
        }
        redisTemplate.delete("product-stock");
        redisTemplate.delete("product-sales");
        redisTemplate.delete("product");
        redisTemplate.delete("product-record");

    }


    private PurchaseRecord initPurchaseRecord(int id, int quantity, int userId, double price) {
        PurchaseRecord purchaseRecord = new PurchaseRecord();
        purchaseRecord.setUserId(userId);
        purchaseRecord.setProductId(id);
        purchaseRecord.setPrice(price);
        purchaseRecord.setQuantity(quantity);
        purchaseRecord.setTotalPrice(price * quantity);
        purchaseRecord.setPurchaseTime(new Timestamp(System.currentTimeMillis()));
        purchaseRecord.setNote("购买时间" + new Date());
        return purchaseRecord;
    }
}
