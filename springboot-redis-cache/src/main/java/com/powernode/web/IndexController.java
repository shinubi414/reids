package com.powernode.web;

import com.powernode.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IndexController {

    @Autowired
    ProductService productService;

    @RequestMapping("/home")
    @ResponseBody
    public String index(){
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0;i<1000;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    productService.queryById(1);
                }
            });
        }
        executorService.shutdown();
        return "success";
    }
}
