package com.gmallinterface.bean;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class User implements Serializable {
    private String name;
    private String city;
    private Integer age;
    private String sex;
    private Double salary;
}
