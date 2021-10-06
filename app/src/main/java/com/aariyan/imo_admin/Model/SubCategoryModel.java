package com.aariyan.imo_admin.Model;

public class SubCategoryModel {
    private String id;
    private String subCategoryName;
    private String parentId;

    public SubCategoryModel(){}

    public SubCategoryModel(String id, String subCategoryName, String parentId) {
        this.id = id;
        this.subCategoryName = subCategoryName;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
