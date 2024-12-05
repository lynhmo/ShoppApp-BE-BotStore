package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.OrderRes;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderRes createOrder(OrderDTO orderDTO) throws DataNotFoundException;

    OrderRes getOrderByOrderId(long orderId) throws DataNotFoundException;

    ApiPageResponse<OrderRes> getAllOrdersByUserId(long user, Pageable pageable) throws DataNotFoundException;

    void deleteOrder(long orderId) throws DataNotFoundException;

    OrderRes updateOrder(long orderId, OrderDTO orderDTO) throws DataNotFoundException;

}
