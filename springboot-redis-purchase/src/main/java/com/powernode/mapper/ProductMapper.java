package com.powernode.mapper;

import com.powernode.model.Product;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {

    Product selectById(int id);

    int updateStock(@Param("quantity")int quantity,@Param("id")int id);
}
