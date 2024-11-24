package es.merkle.component.controller;

import es.merkle.component.application.OrderService;
import es.merkle.component.mapper.ProductMapper;
import es.merkle.component.model.*;
import es.merkle.component.model.api.ModifyOrderRequest;
import es.merkle.component.repository.CustomerRepository;
import es.merkle.component.repository.OrderRepository;
import es.merkle.component.repository.ProductRepository;
import es.merkle.component.repository.entity.DbCustomer;
import es.merkle.component.repository.entity.DbOrder;
import es.merkle.component.repository.entity.DbProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private ProductMapper productMapper;

    @Autowired
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
                .name("productName")
                .price(BigDecimal.TEN)
                .productStatus(String.valueOf(ProductStatus.AVAILABLE))
                .productCategory(String.valueOf(ProductCategory.TV))
                .releasedDate(LocalDate.now().minusDays(1))
                .expiringDate(LocalDate.now().plusDays(10))
                .build();

        Product product = Product.builder()
                .id("product123")
                .name("productName")
                .price(BigDecimal.TEN)
                .productStatus(ProductStatus.AVAILABLE)
                .productCategory(ProductCategory.TV)
                .releasedDate(LocalDate.now().minusDays(1))
                .expiringDate(LocalDate.now().plusDays(10))
                .build();

        DbCustomer dbCustomer = DbCustomer.builder()
                .id("customer123")
                .name("nameCustomer")
                .address("addressCustomer")
                .phoneNumber("phoneNumber")
                .build();

        when(orderRepository.findById("order123")).thenReturn(Optional.of(dbOrder));
        when(productRepository.findById("product123")).thenReturn(Optional.of(dbProduct));
        when(productRepository.findAllById(ArgumentMatchers.eq(List.of("product123")))).thenReturn(List.of(dbProduct));
        when(customerRepository.findById(any())).thenReturn(Optional.of(dbCustomer));

        // Act
//        Order modifiedOrder = orderService.modifyOrder(request);
//        Order modifiedOrder2 = orderService.modifyOrder2(request);

        // Assert
//        assertNotNull(modifiedOrder);
//        assertEquals(1, modifiedOrder.getOrder().getAddingProducts().size());
//        assertEquals(product, modifiedOrder.getOrder().getAddingProducts().get(0));
//        assertEquals(BigDecimal.TEN, modifiedOrder.getOrder().getFinalPrice());
//        assertEquals(OrderStatus.VALID, modifiedOrder.getOrder().getStatus());

        verify(orderRepository).findById("order123");
        verify(productRepository).findById("product123");
        verify(orderRepository).save(any());
    }
}
