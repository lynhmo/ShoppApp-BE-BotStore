package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.response.OrderRes;

public interface OrderService {
    OrderRes createOrder(OrderDTO orderDTO);

    OrderRes getOrder(long orderId);

    ApiPageResponse<OrderRes> getAllOrders();

    void deleteOrder(long orderId);

    OrderRes updateOrder(OrderDTO orderDTO);

}
