package com.inn.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name="Product.getAllProduct",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.status,p.category.id,p.price,p.category.name) from Product p")
@NamedQuery(name = "Product.updateProductStatus",query = "update Product p set p.status=:status where p.id=:id")
@NamedQuery(name="Product.getAllProductByCategory",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.status,p.category.id,p.price,p.category.name) from Product p where p.category.id=:id and p.status='true'")
@NamedQuery(name="Product.getProductById",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.status,p.category.id,p.price,p.category.name) from Product p where p.id=:id")

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
@Data //for constructor and getter  & setter
public class Product implements Serializable {
    private static final long serialVersionID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk",nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;



}
