package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(User userId, Pageable pageable);


    List<Order> findByStatusAndUserId(String status, User userId);

    @Query("update Order o set o.totalMoney = :totalMoney where o.id = :id")
    @Modifying
    void updateTotalMoneyById(Float totalMoney, Long id);
}