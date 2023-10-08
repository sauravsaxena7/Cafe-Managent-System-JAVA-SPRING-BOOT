package com.inn.cafe.POJO;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name="User.findByEmailId",query = "select u from User u where u.email=:email")
@NamedQuery(name="User.getAllUser",query="select new com.inn.cafe.wrapper.UserWrapper(u.id,u.name,u.ContactNumber,u.status,u.email) from User u where u.role='user'")
@NamedQuery(name="User.getAllAdmin",query="select u.email from User u where u.role='admin'")
@NamedQuery(name="User.updateStatus",query = "update User u set u.status=:status where u.id=:id")

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user")
@Data //for constructor and getter  & setter
public class User implements Serializable {
    private static final long serialVersionID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="contactNumber")
    private String ContactNumber;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String Password;

    @Column(name="status")
    private String status;

    @Column(name="role")
    private String role;
}
