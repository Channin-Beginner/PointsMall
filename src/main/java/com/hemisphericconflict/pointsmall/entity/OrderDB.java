package com.hemisphericconflict.pointsmall.entity;

import java.util.Date;

/** 订单表实体类 */
public class OrderDB {
    private Integer orderId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String address;
    private Integer status;
    private Integer totalPoints;
    private Date orderTime;
    private ProductDB product;

    // Getters a    nd Setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public ProductDB getProduct() { return product; }
    public void setProduct(ProductDB product) { this.product = product; }
    // 辅助方法：从 address 中提取手机号
    public String getPhoneFromAddress() {
        if (address != null && address.contains("#")) {
            return address.split("#")[1];
        }
        return "";
    }
    // 辅助方法：从 address 中提取纯地址
    public String getPureAddress() {
        if (address != null && address.contains("#")) {
            return address.split("#")[0];
        }
        return address;
    }
}

