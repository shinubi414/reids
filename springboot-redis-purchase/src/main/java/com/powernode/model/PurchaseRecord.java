package com.powernode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRecord implements Serializable {

    private int id;
    private int userId;
    private int productId;
    private double price;
    private int quantity;
    private double totalPrice;
    private Timestamp purchaseTime;
    private String note;


}
