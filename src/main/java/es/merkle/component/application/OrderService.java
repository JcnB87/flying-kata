package es.merkle.component.application;

import es.merkle.component.model.*;
import es.merkle.component.model.api.*;
import es.merkle.component.repository.CustomerRepository;
import es.merkle.component.repository.OrderRepository;
import es.merkle.component.repository.ProductRepository;
import es.merkle.component.repository.entity.DbOrder;
import es.merkle.component.repository.entity.DbProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import es.merkle.component.mapper.OrderMapper;
import es.merkle.component.populating.PopulatorRunner;
import es.merkle.component.repository.adapter.OrderAdapter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final PopulatorRunner populatorRunner;
    private final OrderMapper orderMapper;
    private final OrderAdapter orderAdapter;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public Order createOrder(CreateOrderRequest orderRequest) {
        Order order = mapCreateOrderRequest(orderRequest);

        try {
            populateOrder(order);
            saveOrder(order);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return order;
    }

    public SubmitOrderResponse submitOrder(SubmitOrderRequest submitOrderRequest) {
        Order order = new Order(); // TODO: Idk how to do this
        SubmitOrderResponse submitOrderResponse = new SubmitOrderResponse();
        submitOrderResponse.setOrder(order);

        if (order.getStatus() == OrderStatus.INVALID) {
            submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
            submitOrderResponse.setMessage("The order was not submitted because it's INVALID");
            return submitOrderResponse;
        } else if (order.getStatus() == OrderStatus.VALID) {
            submitOrderResponse.setMessage("The order was submitted successfully");
            submitOrderResponse.getOrder().setStatus(OrderStatus.SUBMITTED);
        } else if (order.getStatus() == OrderStatus.NEW) {
            submitOrderResponse.setMessage("The was not submitted because it's not in a final status");
            submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
        } else {
            return null;
        }

        return submitOrderResponse;
    }

    public Order modifyOrder(ModifyOrderRequest modifyOrderRequest) {

        DbOrder dbOrder = orderRepository.findById(modifyOrderRequest.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DbProduct dbProduct = productRepository.findById(modifyOrderRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        dbOrder.getAddingProducts().add(dbProduct.getId());

        BigDecimal totalPrice = dbOrder.getFinalPrice() != null ? dbOrder.getFinalPrice() : BigDecimal.ZERO;
        totalPrice = totalPrice.add(dbProduct.getPrice());
        dbOrder.setFinalPrice(totalPrice);

        if (dbProduct.getProductStatus().equals("NOT_AVAILABLE")
           || !dbProduct.getExpiringDate().isAfter(LocalDate.now())
           || !dbProduct.getReleasedDate().isBefore(LocalDate.now())) {
            dbOrder.setStatus(OrderStatus.INVALID);
        } else {
            dbOrder.setStatus(OrderStatus.VALID);
        }

        orderRepository.save(dbOrder);

        Order orderResponse = convertDbOrderToOrder(dbOrder);

        return orderResponse;
    }

    private void saveOrder(Order order) {
        orderAdapter.saveOrder(order);
    }

    private void populateOrder(Order order) {
        populatorRunner.run(order);
    }

    private Order mapCreateOrderRequest(CreateOrderRequest orderRequest) {
        return orderMapper.mapCreateOrderRequestToOrder(orderRequest);
    }

    private Order convertDbOrderToOrder(DbOrder dbOrder) {
        // Assuming you have a method to fetch customer and products
        Customer customer = getCustomerById(dbOrder.getCustomerId());
        List<Product> addingProducts = getProductsByIds(dbOrder.getAddingProducts());
        List<Product> removeProducts = getProductsByIds(dbOrder.getRemoveProducts());

        return Order.builder()
                .id(dbOrder.getId())
                .customerId(dbOrder.getCustomerId())
                .orderType(OrderType.ADD)
                .status(dbOrder.getStatus())
                .addingProducts(addingProducts)
                .removeProducts(removeProducts)
                .finalPrice(dbOrder.getFinalPrice())
                .customer(customer)
                .build();
    }

    // Helper method to get Products by IDs
    private List<Product> getProductsByIds(List<String> productIds) {
//        return StreamSupport.stream(productRepository.findAllById(productIds).spliterator(), false)
//                .map(product -> new Product(product.getId(), product.getName(), ProductStatus.fromValue(product.getProductStatus()), ProductCategory.valueOf(product.getProductCategory()), product.getPrice(), product.getExpiringDate(), product.getReleasedDate()))
//                .collect(Collectors.toList());
        List<Product> productList = new ArrayList<>();
        productRepository.findAllById(productIds).forEach(product -> {
            productList.add(
                    new Product(product.getId(), product.getName(), ProductStatus.fromValue(product.getProductStatus()), ProductCategory.valueOf(product.getProductCategory()), product.getPrice(), product.getExpiringDate(), product.getReleasedDate())
            );

        });
        return productList;
    }

    // Helper method to get Customer by ID
    private Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId)
                .map(c -> mapCustomer(c.getId(), c.getName(), c.getAddress(), c.getPhoneNumber()))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    private Customer mapCustomer(String id, String name, String address, String phoneNumber) {
        return Customer.builder()
                .id(id)
                .name(name)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }
}
