package com.devops.course.service;

import com.devops.course.entity.Customer;
import com.devops.course.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 客户业务逻辑层
 */
@Service
@Transactional(readOnly = true)
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 查询所有客户
     */
    public List<Customer> findAllCustomers() {
        log.debug("查询所有客户");
        List<Customer> customers = customerRepository.findAll();
        log.info("查询到 {} 个客户", customers.size());
        return customers;
    }

    /**
     * 根据ID查询客户
     */
    public Optional<Customer> findCustomerById(String customerId) {
        log.debug("根据ID查询客户: {}", customerId);
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            log.info("找到客户: {}", customerId);
        } else {
            log.warn("未找到客户: {}", customerId);
        }
        return customer;
    }

    /**
     * 根据客户类型查询
     */
    public List<Customer> findByCustomerType(String customerType) {
        log.debug("根据客户类型查询: {}", customerType);
        List<Customer> customers = customerRepository.findByCustomerType(customerType);
        log.info("查询到 {} 个 {} 类型的客户", customers.size(), customerType);
        return customers;
    }

    /**
     * 根据状态查询
     */
    public List<Customer> findActiveCustomers() {
        log.debug("查询活跃客户");
        List<Customer> customers = customerRepository.findByStatus("ACTIVE");
        log.info("查询到 {} 个活跃客户", customers.size());
        return customers;
    }

    /**
     * 保存客户
     */
    @Transactional
    public Customer saveCustomer(Customer customer) {
        log.debug("保存客户: {}", customer.getCustomerId());
        try {
            Customer savedCustomer = customerRepository.save(customer);
            log.info("客户保存成功: {}", savedCustomer.getCustomerId());
            return savedCustomer;
        } catch (Exception e) {
            log.error("客户保存失败: {}, 错误: {}", customer.getCustomerId(), e.getMessage());
            throw e;
        }
    }

    /**
     * 删除客户
     */
    @Transactional
    public void deleteCustomer(String customerId) {
        log.debug("删除客户: {}", customerId);
        try {
            customerRepository.deleteById(customerId);
            log.info("客户删除成功: {}", customerId);
        } catch (Exception e) {
            log.error("客户删除失败: {}, 错误: {}", customerId, e.getMessage());
            throw e;
        }
    }
}