package com.powernode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private int id;
    private String name;
    private int stock;
    private double price;
    private int version;
    private String note;


}
