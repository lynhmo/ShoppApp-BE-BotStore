package com.llu1ts.shopapp.service.impl;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDetailDTO;
import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.OrderDetail;
import com.llu1ts.shopapp.entity.Product;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.repo.OrderDetailRepository;
import com.llu1ts.shopapp.repo.OrderRepository;
import com.llu1ts.shopapp.repo.ProductRepository;
import com.llu1ts.shopapp.response.OrderDetailRes;
import com.llu1ts.shopapp.service.svc.OrderDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDetailImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDetailRes createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                () -> new DataNotFoundException("Order not found")
        );
        if (Boolean.TRUE.equals(order.getIsDeleted())) {
            throw new DataNotFoundException("Order is deleted");
        }
        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product not found")
        );
        if (Boolean.TRUE.equals(product.getIsDeleted())) {
            throw new DataNotFoundException("Product is deleted");
        }
        Optional<OrderDetail> existOrderDetail = orderDetailRepository.findByProductIdAndOrderId(orderDetailDTO.getProductId(), orderDetailDTO.getOrderId());
        if (existOrderDetail.isPresent()) {

            // get the order detail out
            OrderDetail tempOrderDetail = existOrderDetail.get();

            // get the number of prod
            int currentQuantities = tempOrderDetail.getNumberOfProducts();
            int newQuantities = orderDetailDTO.getNumberOfProducts();

            // set the number of prod
            tempOrderDetail.setNumberOfProducts(currentQuantities + newQuantities);

            //save
            orderDetailRepository.save(tempOrderDetail);

            // map the response and return
            OrderDetailRes orderDetailRes = new OrderDetailRes();
            BeanUtils.copyProperties(tempOrderDetail, orderDetailRes);
            orderDetailRes.setOrder(order.getId());
            orderDetailRes.setProduct(product.getId());
            return orderDetailRes;
        } else {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(orderDetailDTO, orderDetail);
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetailRepository.save(orderDetail);
            OrderDetailRes orderDetailRes = new OrderDetailRes();
            BeanUtils.copyProperties(orderDetail, orderDetailRes);
            orderDetailRes.setOrder(order.getId());
            orderDetailRes.setProduct(product.getId());
            return orderDetailRes;
        }
    }

    @Override
    public OrderDetailRes getOrderDetail(long orderId) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Order not found"));
        OrderDetailRes orderDetailRes = new OrderDetailRes();
        BeanUtils.copyProperties(orderDetail, orderDetailRes);
        orderDetailRes.setOrder(orderDetail.getOrder().getId());
        orderDetailRes.setProduct(orderDetail.getProduct().getId());
        return orderDetailRes;
    }

    @Override
    public ApiPageResponse<OrderDetailRes> getAllOrderDetailByOrderId(long orderId, Pageable pageable) throws DataNotFoundException {
        if (!orderRepository.existsById(orderId)) {
            throw new DataNotFoundException("Order not found");
        }
        Page<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId, pageable);
        ApiPageResponse<OrderDetailRes> apiPageResponse = new ApiPageResponse<>();
        List<OrderDetailRes> orderDetailResList = orderDetails.getContent().stream()
                .map(orderDetail -> {
                    OrderDetailRes orderDetailRes = new OrderDetailRes();
                    BeanUtils.copyProperties(orderDetail, orderDetailRes);
                    return orderDetailRes;
                }).toList();
        BeanUtils.copyProperties(orderDetails, apiPageResponse);
        apiPageResponse.setContent(orderDetailResList);
        return apiPageResponse;
    }

    @Override
    public void deleteOrderDetail(long orderId) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailRepository.findById(orderId).orElseThrow(() -> new DataNotFoundException("Order not found"));
        orderDetail.setIsDeleted(true);
        orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetailRes updateOrderDetail(long orderId, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId()).orElseThrow(
                () -> new DataNotFoundException("Order not found")
        );
        Product product = productRepository.findById(orderDetailDTO.getProductId()).orElseThrow(
                () -> new DataNotFoundException("Product not found")
        );
        OrderDetail orderDetail = orderDetailRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Detail order not found"));
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetailRepository.save(orderDetail);
        BeanUtils.copyProperties(orderDetailDTO, orderDetail);
        OrderDetailRes orderDetailRes = new OrderDetailRes();
        BeanUtils.copyProperties(orderDetail, orderDetailRes);
        orderDetailRes.setOrder(order.getId());
        orderDetailRes.setProduct(product.getId());
        return orderDetailRes;
    }
}
