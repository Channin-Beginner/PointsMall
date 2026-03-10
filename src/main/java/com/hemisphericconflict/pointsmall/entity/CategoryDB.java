package com.hemisphericconflict.pointsmall.entity;

/** 商品分类表实体类 */
public class CategoryDB {
    private Integer categoryId;
    private String categoryName;
    private String iconClass; // 新增字段

    // Getters and Setters
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getIconClass() { return iconClass; } // 新增getter
    public void setIconClass(String iconClass) { this.iconClass = iconClass; } // 新增setter
}
