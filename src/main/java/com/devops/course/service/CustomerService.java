package com.devops.course.service;

import com.devops.course.entity.Customer;
import com.devops.course.repository.CustomerRepository;
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

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 查询所有客户
     */
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * 根据ID查询客户
     */
    public Optional<Customer> findCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    /**
     * 根据客户类型查询
     */
    public List<Customer> findByCustomerType(String customerType) {
        return customerRepository.findByCustomerType(customerType);
    }

    /**
     * 根据状态查询
     */
    public List<Customer> findActiveCustomers() {
        return customerRepository.findByStatus("ACTIVE");
    }

    /**
     * 保存客户
     */
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * 删除客户
     */
    @Transactional
    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }
}