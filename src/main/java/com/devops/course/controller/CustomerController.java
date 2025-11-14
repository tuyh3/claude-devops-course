package com.devops.course.controller;

import com.devops.course.entity.Customer;
import com.devops.course.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户管理REST API
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有客户
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据ID获取客户
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        return customerService.findCustomerById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据客户类型查询
     */
    @GetMapping("/type/{customerType}")
    public ResponseEntity<List<Customer>> getCustomersByType(@PathVariable String customerType) {
        List<Customer> customers = customerService.findByCustomerType(customerType);
        return ResponseEntity.ok(customers);
    }

    /**
     * 获取活跃客户
     */
    @GetMapping("/active")
    public ResponseEntity<List<Customer>> getActiveCustomers() {
        List<Customer> customers = customerService.findActiveCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}