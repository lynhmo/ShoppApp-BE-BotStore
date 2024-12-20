package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    Page<OrderDetail> findByOrderId(Long orderId, Pageable pageable);

    @Query("UPDATE OrderDetail od SET od.isDeleted = true WHERE od.order.id= :orderId")
    @Modifying
    void deleteByOrderId(Long orderId);

    Page<OrderDetail> findByOrderIdAndIsDeleted(Long orderId, Boolean isDeleted,Pageable pageable);

    boolean existsByProductIdAndOrderId(Long productId, Long orderId);

    Optional<OrderDetail> findByProductIdAndOrderId(Long productId, Long orderId);

    @Query("SELECT SUM(od.totalMoney) FROM OrderDetail od WHERE od.order.id = :orderId and od.isDeleted=:isDelete")
    Float getTotalMoneyOfAllOrderDetailByOrderIdAndIsDelete(Long orderId, Boolean isDelete);
}