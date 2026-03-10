package com.hemisphericconflict.pointsmall.entity;

import java.util.Date;

public class CartItemDB {
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Date addedTime;

    // 关联的商品信息（非数据库字段）
    private ProductDB product;

    // Getters and Setters
    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getAddedTime() { return addedTime; }
    public void setAddedTime(Date addedTime) { this.addedTime = addedTime; }

    public ProductDB getProduct() { return product; }
    public void setProduct(ProductDB product) { this.product = product; }
}