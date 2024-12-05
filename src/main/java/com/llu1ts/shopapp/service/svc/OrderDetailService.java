package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDetailDTO;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.OrderDetailRes;
import org.springframework.data.domain.Pageable;

public interface OrderDetailService {
    OrderDetailRes createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException;

    OrderDetailRes getOrderDetail(long orderId) throws DataNotFoundException;

    ApiPageResponse<OrderDetailRes> getAllOrderDetailByOrderId(long orderId, Pageable pageable) throws DataNotFoundException;

    void deleteOrderDetail(long orderId) throws DataNotFoundException;

    OrderDetailRes updateOrderDetail(long orderId, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;


}
