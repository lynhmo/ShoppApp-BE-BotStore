package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Page<OrderDetail> findByOrderId(Long orderId, Pageable pageable);


    boolean existsByProductIdAndOrderId(Long productId, Long orderId);

    Optional<OrderDetail> findByProductIdAndOrderId(Long productId, Long orderId);
}