package com.llu1ts.shopapp.service.impl;

import com.llu1ts.shopapp.common.ApiPageResponse;
import com.llu1ts.shopapp.dto.OrderDTO;
import com.llu1ts.shopapp.entity.Order;
import com.llu1ts.shopapp.entity.OrderStatus;
import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.DataNotFoundException;
import com.llu1ts.shopapp.repo.OrderRepository;
import com.llu1ts.shopapp.repo.UserRepository;
import com.llu1ts.shopapp.response.OrderRes;
import com.llu1ts.shopapp.service.svc.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public OrderRes createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        // check user co tontai hay khong
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() ->
                new DataNotFoundException("User not found"));

        // set order vao db
        Order order = new Order();
        order.setUserId(user);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Shipping date must be at least today!");
        }
        BeanUtils.copyProperties(orderDTO, order);
        orderRepository.save(order);

        return modelMapper.map(order, OrderRes.class);
    }

    @Override
    public OrderRes getOrderByOrderId(long orderId) throws DataNotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Order not found"));
        return modelMapper.map(order, OrderRes.class);
    }

    @Override
    public ApiPageResponse<OrderRes> getAllOrdersByUserId(long userId, Pageable pageable) throws DataNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataNotFoundException("User not exist"));
        Page<Order> orders = orderRepository.findByUserId(user, pageable);
        ApiPageResponse<OrderRes> apiPageResponse = new ApiPageResponse<>();
        List<OrderRes> listOrder = orders.getContent()
                .stream()
                .map(order -> modelMapper.map(order, OrderRes.class))
                .toList();
        BeanUtils.copyProperties(orders, apiPageResponse);
        apiPageResponse.setContent(listOrder);
        return apiPageResponse;
    }

    @Override
    public void deleteOrder(long orderId) throws DataNotFoundException {
        Order existOrder = orderRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Order not found"));
        existOrder.setActive(false);
        existOrder.setIsDeleted(true);
        orderRepository.save(existOrder);
    }

    @Override
    @Transactional
    public OrderRes updateOrder(long orderId, OrderDTO orderDTO) throws DataNotFoundException {
        Order existOrder = orderRepository.findById(orderId).orElseThrow(() ->
                new DataNotFoundException("Order not found"));
        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow(() ->
                new DataNotFoundException("User not exist"));
        BeanUtils.copyProperties(orderDTO, existOrder);
        existOrder.setUserId(user);
        return modelMapper.map(orderRepository.save(existOrder), OrderRes.class);
    }
}
