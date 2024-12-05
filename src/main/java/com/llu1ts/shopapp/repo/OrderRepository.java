package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(User userId, Pageable pageable);


}