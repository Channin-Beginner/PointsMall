package com.hemisphericconflict.pointsmall.service;

import com.hemisphericconflict.pointsmall.dao.CategoryDao;
import com.hemisphericconflict.pointsmall.entity.CategoryDB;

import java.util.List;


public class CategoryService {
    private final CategoryDao categoryDao = new CategoryDao();

    public List<CategoryDB> getAllCategories() {
        return categoryDao.getAllCategories();
    }
}