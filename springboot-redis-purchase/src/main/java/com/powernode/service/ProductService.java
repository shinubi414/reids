package com.powernode.service;

public interface ProductService {

    boolean purchase(int id,int quantity,int userId);

    //把产品信息添加到redis缓存
    void addRedis(int id);

    //在Redis中处理购买请求
    boolean purchaseRedis(int id,int quantity,int userId);

    //将redis缓存添加到数据库
    void adddb();
}
