package com.devops.course.repository;

import com.devops.course.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户数据访问层
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * 根据客户类型查询客户
     */
    List<Customer> findByCustomerType(String customerType);

    /**
     * 根据状态查询客户
     */
    List<Customer> findByStatus(String status);

    /**
     * 根据客户名称模糊查询
     */
    List<Customer> findByCustomerNameContaining(String name);

    /**
     * 自定义查询：查询指定信用等级的客户数量
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.creditLevel = :creditLevel")
    Long countByCreditLevel(String creditLevel);
}
