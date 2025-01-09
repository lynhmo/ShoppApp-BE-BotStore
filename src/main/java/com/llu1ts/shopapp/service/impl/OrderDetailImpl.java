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
import com.llu1ts.shopapp.response.OrderDetailWithProductImage;
import com.llu1ts.shopapp.response.ProductResponseImage;
import com.llu1ts.shopapp.service.svc.OrderDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDetailImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Override
    public List<OrderDetailWithProductImage> getAllOrderDetailWithProductAdmin(long orderDetailId) throws DataNotFoundException {
        if (!orderRepository.existsById(orderDetailId)) {
            throw new DataNotFoundException("Order not found");
        }

        Pageable pageable = PageRequest.of(0, 9999);
        Page<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndIsDeleted(orderDetailId, false, pageable);
        List<OrderDetail> orderDetailList = orderDetails.getContent();
        List<OrderDetailWithProductImage> orderDetailWithProductImages = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetailWithProductImage orderDetailWithProductImage = new OrderDetailWithProductImage();
            BeanUtils.copyProperties(orderDetail, orderDetailWithProductImage);

            //Map product
            ProductResponseImage productResponseImage = new ProductResponseImage();
            Product product = orderDetail.getProduct();
            productResponseImage.setCategoryId(product.getCategory().getId());
            String image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(orderDetail.getProduct().getThumbnail());
            productResponseImage.setThumbnail(image);
            BeanUtils.copyProperties(product, productResponseImage);
            orderDetailWithProductImage.setProduct(productResponseImage);

            orderDetailWithProductImages.add(orderDetailWithProductImage);
        }
        return orderDetailWithProductImages;
    }

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


            //revoke the order detail if it deleted
            if (Boolean.TRUE.equals(tempOrderDetail.getIsDeleted())) {
                tempOrderDetail.setIsDeleted(false);
                tempOrderDetail.setNumberOfProducts(newQuantities);
            } else {
                tempOrderDetail.setNumberOfProducts(currentQuantities + newQuantities);
            }

            // set the number of prod


            // update total money
            tempOrderDetail.setTotalMoney(tempOrderDetail.getProduct().getPrice() * tempOrderDetail.getNumberOfProducts());

            //save
            orderDetailRepository.save(tempOrderDetail);


            orderRepository.updateTotalMoneyById(orderDetailRepository.getTotalMoneyOfAllOrderDetailByOrderIdAndIsDelete(order.getId(), false), order.getId());


            // map the response and return
            OrderDetailRes orderDetailRes = new OrderDetailRes();
            BeanUtils.copyProperties(tempOrderDetail, orderDetailRes);
            orderDetailRes.setOrder(order.getId());
            orderDetailRes.setProduct(product.getId());
            orderDetailRes.setTotalMoney(tempOrderDetail.getTotalMoney().longValue());
            return orderDetailRes;
        } else {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(orderDetailDTO, orderDetail);
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(product.getPrice() * orderDetailDTO.getNumberOfProducts());
            orderDetailRepository.save(orderDetail);

            Float currentOrderTotalMoney = orderDetailRepository.getTotalMoneyOfAllOrderDetailByOrderIdAndIsDelete(order.getId(), false);
            if (currentOrderTotalMoney == null) {
                currentOrderTotalMoney = (float) 0;
            }
            orderRepository.updateTotalMoneyById(currentOrderTotalMoney, order.getId());


            OrderDetailRes orderDetailRes = new OrderDetailRes();
            BeanUtils.copyProperties(orderDetail, orderDetailRes);
            orderDetailRes.setOrder(order.getId());
            orderDetailRes.setProduct(product.getId());
            orderDetailRes.setTotalMoney(orderDetail.getTotalMoney().longValue());
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
        orderDetailRes.setTotalMoney(orderDetail.getTotalMoney().longValue());
        return orderDetailRes;
    }

    @Override
    public ApiPageResponse<OrderDetailRes> getAllOrderDetailByOrderId(long orderId, Pageable pageable) throws DataNotFoundException {
        if (!orderRepository.existsById(orderId)) {
            throw new DataNotFoundException("Order not found");
        }
        Page<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndIsDeleted(orderId, false, pageable);
        ApiPageResponse<OrderDetailRes> apiPageResponse = new ApiPageResponse<>();
        List<OrderDetailRes> orderDetailResList = orderDetails.getContent().stream()
                .map(orderDetail -> {
                    OrderDetailRes orderDetailRes = new OrderDetailRes();
                    BeanUtils.copyProperties(orderDetail, orderDetailRes);
                    orderDetailRes.setProduct(orderDetail.getProduct().getId());
                    orderDetailRes.setOrder(orderDetail.getOrder().getId());
                    orderDetailRes.setTotalMoney(orderDetail.getTotalMoney().longValue());
                    return orderDetailRes;
                }).toList();
        BeanUtils.copyProperties(orderDetails, apiPageResponse);
        apiPageResponse.setContent(orderDetailResList);
        return apiPageResponse;
    }

    @Override
    public void deleteOrderDetail(long orderDetailId) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow(() -> new DataNotFoundException("Order not found"));
        orderDetail.setIsDeleted(true);

        orderRepository.updateTotalMoneyById(orderDetail.getOrder().getTotalMoney() - orderDetail.getTotalMoney(), orderDetail.getOrder().getId());
        orderDetailRepository.save(orderDetail);
    }


    @Override
    public void deleteAllOrderDetail(long orderId) throws DataNotFoundException {
        orderDetailRepository.deleteByOrderId(orderId);
        orderRepository.updateTotalMoneyById((float) 0, orderId);
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

        // Copy prop
        BeanUtils.copyProperties(orderDetailDTO, orderDetail);
        // Set các thông tin thiếu
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        // Sửa lại thông tin tổng tiền trên sản phẩm
        orderDetail.setTotalMoney(orderDetailDTO.getNumberOfProducts() * product.getPrice());

        // sửa lại thông tin sản phẩm
        orderDetail.setPrice(product.getPrice());
        // save
        orderDetailRepository.save(orderDetail);

        orderRepository.updateTotalMoneyById(orderDetailRepository.getTotalMoneyOfAllOrderDetailByOrderIdAndIsDelete(order.getId(), false), order.getId());

        // Response
        OrderDetailRes orderDetailRes = new OrderDetailRes();
        // từ E -> Response
        BeanUtils.copyProperties(orderDetail, orderDetailRes);
        // Thông tin thiếu
        orderDetailRes.setOrder(order.getId());
        orderDetailRes.setProduct(product.getId());
        orderDetailRes.setTotalMoney(orderDetail.getTotalMoney().longValue());

        return orderDetailRes;
    }
}
