package com.inn.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills",query = "select b from Bill b order by b.id desc")
@NamedQuery(name = "Bill.getAllBillsByUserName", query = "select b from Bill b where b.createdBy=:username order by b.id desc")


@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "bill")
@Data //for constructor and getter  & setter
public class Bill implements Serializable {
    private static final long serialVersionID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="uuid")
    private String uUid;

    @Column(name="email")
    private String email;

    @Column(name="contactNumber")
    private String ContactNumber;

    @Column(name="paymentmethod")
    private String paymentMethod;

    @Column(name="productdetails")
    private String productDetails;

    @Column(name="total")
    private Integer total;

    @Column(name="createdby")
    private String createdBy;
}
