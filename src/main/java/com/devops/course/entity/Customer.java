package com.devops.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 客户实体类
 * 对应数据库表: TCBS.CUSTOMERS
 *
 * 使用 Lombok 注解简化代码：
 * - @Getter/@Setter: 自动生成所有字段的 getter 和 setter 方法
 * - @NoArgsConstructor: 生成无参构造器（JPA 需要）
 * - @AllArgsConstructor: 生成全参构造器
 * - @ToString: 生成 toString 方法
 */
@Entity
@Table(name = "CUSTOMERS", schema = "TCBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {

    @Id
    @Column(name = "CUSTOMER_ID", nullable = false, length = 20)
    private String customerId;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customerName;

    @Column(name = "ID_CARD_NO", length = 18)
    private String idCardNo;

    @Column(name = "CUSTOMER_TYPE", length = 10)
    private String customerType;

    @Column(name = "CONTACT_PHONE", length = 20)
    private String contactPhone;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "CREDIT_LEVEL", length = 10)
    private String creditLevel;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
