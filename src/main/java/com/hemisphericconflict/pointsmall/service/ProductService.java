package com.hemisphericconflict.pointsmall.service;

import com.hemisphericconflict.pointsmall.dao.ProductDao;
import com.hemisphericconflict.pointsmall.entity.ProductDB;

import java.util.List;

public class ProductService {
    private final ProductDao productDao = new ProductDao();
    public List<ProductDB> getProductsByCategory(Integer categoryId) {
        return productDao.getProductsByCategory(categoryId);
    }

    // 新增：搜索商品服务
    public List<ProductDB> searchProducts(String keyword) {
        return productDao.searchProductsByKeyword(keyword);
    }
}