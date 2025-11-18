package com.devops.course.controller;

import com.devops.course.entity.Customer;
import com.devops.course.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有客户
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        log.info("收到请求: GET /api/customers");
        List<Customer> customers = customerService.findAllCustomers();
        log.info("返回 {} 个客户", customers.size());
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据ID获取客户
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        log.info("收到请求: GET /api/customers/{}", customerId);
        return customerService.findCustomerById(customerId)
                .map(customer -> {
                    log.info("返回客户: {}", customerId);
                    return ResponseEntity.ok(customer);
                })
                .orElseGet(() -> {
                    log.warn("客户不存在: {}", customerId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据客户类型查询
     */
    @GetMapping("/type/{customerType}")
    public ResponseEntity<List<Customer>> getCustomersByType(@PathVariable String customerType) {
        log.info("收到请求: GET /api/customers/type/{}", customerType);
        List<Customer> customers = customerService.findByCustomerType(customerType);
        log.info("返回 {} 个 {} 类型客户", customers.size(), customerType);
        return ResponseEntity.ok(customers);
    }

    /**
     * 获取活跃客户
     */
    @GetMapping("/active")
    public ResponseEntity<List<Customer>> getActiveCustomers() {
        log.info("收到请求: GET /api/customers/active");
        List<Customer> customers = customerService.findActiveCustomers();
        log.info("返回 {} 个活跃客户", customers.size());
        return ResponseEntity.ok(customers);
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("收到请求: POST /api/customers, 客户ID: {}", customer.getCustomerId());
        Customer savedCustomer = customerService.saveCustomer(customer);
        log.info("客户创建成功: {}", savedCustomer.getCustomerId());
        return ResponseEntity.ok(savedCustomer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("收到请求: DELETE /api/customers/{}", customerId);
        customerService.deleteCustomer(customerId);
        log.info("客户删除成功: {}", customerId);
        return ResponseEntity.noContent().build();
    }
}