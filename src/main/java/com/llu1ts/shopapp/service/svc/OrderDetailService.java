package com.llu1ts.shopapp.service.svc;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDetailDTO;
import com.llu1ts.shopapp.entity.OrderDetail;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.response.OrderDetailRes;
import com.llu1ts.shopapp.response.OrderDetailWithProductImage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderDetailService {
    OrderDetailRes createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException;

    OrderDetailRes getOrderDetail(long orderDetailId) throws DataNotFoundException;

    ApiPageResponse<OrderDetailRes> getAllOrderDetailByOrderId(long orderDetailId, Pageable pageable) throws DataNotFoundException;

    List<OrderDetailWithProductImage> getAllOrderDetailWithProductAdmin(long orderDetailId) throws DataNotFoundException;

    void deleteOrderDetail(long orderDetailId) throws DataNotFoundException;

    void deleteAllOrderDetail(long orderId) throws DataNotFoundException;

    OrderDetailRes updateOrderDetail(long orderDetailsId, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;


}
