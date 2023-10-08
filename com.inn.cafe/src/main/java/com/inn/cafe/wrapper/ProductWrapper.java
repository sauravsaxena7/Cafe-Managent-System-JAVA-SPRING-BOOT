package com.inn.cafe.wrapper;


import lombok.Data;

@Data
public class ProductWrapper {

    Integer id;
    String name;
    String description;
    String status;
    Integer categoryId;
    Integer price;
    String categoryName;

    public ProductWrapper(){

    }

    public ProductWrapper(Integer id,String name,String description,String status,Integer categoryId,Integer price,String categoryName){
        this.id=id;
        this.name=name;
        this.description=description;
        this.price=price;
        this.status=status;
        this.categoryName=categoryName;
        this.categoryId=categoryId;

    }

    public ProductWrapper(Integer id,String name){
        this.id=id;
        this.name=name;
    }

}
