package com.dryht.mobile.Bean;

public class Category {

    /**
     * categoryid : 1
     * name : 交友
     */

    private int categoryid;
    private String name;

    public Category(int categoryid,String name){
        this.categoryid = categoryid;
        this.name = name;
    }

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
