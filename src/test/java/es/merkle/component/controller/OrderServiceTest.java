package es.merkle.component.controller;

import es.merkle.component.application.OrderService;
import es.merkle.component.mapper.ProductMapper;
import es.merkle.component.model.*;
import es.merkle.component.model.api.ModifyOrderRequest;
import es.merkle.component.model.api.ModifyOrderResponse;
import es.merkle.component.repository.CustomerRepository;
import es.merkle.component.repository.OrderRepository;
import es.merkle.component.repository.ProductRepository;
import es.merkle.component.repository.entity.DbCustomer;
import es.merkle.component.repository.entity.DbOrder;
import es.merkle.component.repository.entity.DbProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void modifyOrder_addProduct_success() {
        // Arrange
        ModifyOrderRequest request = ModifyOrderRequest.builder()
                .orderId("order123")
                .productId("product123")
                .orderType(OrderType.ADD)
                .build();

        DbOrder dbOrder = DbOrder.builder()
                .id("order123")
                .status(OrderStatus.NEW)
                .finalPrice(BigDecimal.ZERO)
                .addingProducts(new ArrayList<>())
                .build();

        Order order = Order.builder()
                .id("order123")
                .addingProducts(new ArrayList<>())
                .removeProducts(new ArrayList<>())
                .status(OrderStatus.NEW)
                .build();

        DbProduct dbProduct = DbProduct.builder()
                .id("product123")
                .price(BigDecimal.TEN)
                .productStatus(String.valueOf(ProductStatus.AVAILABLE))
                .releasedDate(LocalDate.now().minusDays(1))
                .expiringDate(LocalDate.now().plusDays(10))
                .build();

        Product product = Product.builder()
                .id("product123")
                .price(BigDecimal.TEN)
                .productStatus(ProductStatus.AVAILABLE)
                .releasedDate(LocalDate.now().minusDays(1))
                .expiringDate(LocalDate.now().plusDays(10))
                .build();

        DbCustomer dbCustomer = DbCustomer.builder()
                .id("customer123")
                .build();

        when(orderRepository.findById("order123")).thenReturn(Optional.of(dbOrder));
        when(productRepository.findById("product123")).thenReturn(Optional.of(dbProduct));
        when(productMapper.mapToProduct(dbProduct)).thenReturn(product);
        when(customerRepository.findById(any())).thenReturn(Optional.of(dbCustomer));

        // Act
        ModifyOrderResponse modifiedOrder = orderService.modifyOrder(request);

        // Assert
        assertNotNull(modifiedOrder);
        assertEquals(1, modifiedOrder.getOrder().getAddingProducts().size());
        assertEquals(product, modifiedOrder.getOrder().getAddingProducts().get(0));
        assertEquals(BigDecimal.TEN, modifiedOrder.getOrder().getFinalPrice());
        assertEquals(OrderStatus.VALID, modifiedOrder.getOrder().getStatus());

        verify(orderRepository).findById("order123");
        verify(productRepository).findById("product123");
        verify(orderRepository).save(any());
    }
}
