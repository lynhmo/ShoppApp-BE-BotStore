package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.OrderRes;
import com.llu1ts.shopapp.response.OrderResDetail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderRes createOrder(OrderDTO orderDTO) throws DataNotFoundException;

    OrderRes getOrderByOrderId(long orderId) throws DataNotFoundException;

    List<OrderRes> allOrders() throws DataNotFoundException;

    ApiPageResponse<OrderRes> getAllOrdersByUserId(long user, Pageable pageable) throws DataNotFoundException;

    List<OrderResDetail> getAllOrderDetailByUserId(long orderId) throws DataNotFoundException;

    void deleteOrder(long orderId) throws DataNotFoundException;

    OrderRes updateOrder(long orderId, OrderDTO orderDTO) throws DataNotFoundException;

    void updateOrderStatus(long userid, long orderId, String orderStatus) throws DataNotFoundException;
}
