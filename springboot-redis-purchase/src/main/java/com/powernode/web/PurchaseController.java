package com.powernode.web;

import com.powernode.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/purchase")
    public String purchase(int id,int quantity){
        boolean flag = productService.purchase(id, quantity, 1);
        return flag?"抢购成功":"抢购失败";
    }

    @RequestMapping("/addRedis")
    public String addRedis(int id){
        productService.addRedis(id);
        return "success";
    }

    @RequestMapping("/purchaseRedis")
    public String purchaseRedis(int id,int quantity){
        boolean flag = productService.purchaseRedis(id, quantity, 1);
        return flag?"抢购成功":"抢购失败";
    }

    @RequestMapping("/addDb")
    public String addDb(){
        productService.adddb();
        return "success";
    }
}
