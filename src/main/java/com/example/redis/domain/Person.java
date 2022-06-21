package com.example.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person implements Serializable {

    //必须实现无参的构造函数
    //需要实现Serializable 接口和定义serialVersionUID（因为缓存需要使用JDK的方式序列化和反序列化）。

    private static final long serialVersionUID = -8985545025228238754L;

    public String id;
    public String firstname;
    public String lastname;
    public Address address;   //注意这里，不是基础数据类型

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
