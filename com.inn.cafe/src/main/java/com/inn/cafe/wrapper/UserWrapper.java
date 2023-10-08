package com.inn.cafe.wrapper;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {
    private Integer id;
    private String name;
    private String contactNumber;

    public UserWrapper(Integer id, String name, String contactNumber, String status, String email) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.status = status;
        this.email = email;
    }

    private String status;
    private String email;




}
