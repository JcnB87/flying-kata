package es.merkle.component.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.merkle.component.application.OrderService;
import es.merkle.component.model.*;
import es.merkle.component.model.api.ModifyOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ModifyOrderControllerTest {

    @Mock
    private OrderService orderService;  // Mock the OrderService

    @InjectMocks
    private OrderController orderController;  // Inject the controller with mocked services

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();  // Initialize MockMvc
        objectMapper = new ObjectMapper();  // Initialize ObjectMapper for JSON conversion
    }

    @Test
    void modifyOrder_WithAddOperation_ReturnsOrderWithAddedProducts() throws Exception {
        // Prepare the ModifyOrderRequest
        ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
        modifyOrderRequest.setOrderId("order123");
        modifyOrderRequest.setProductId("product123");
        modifyOrderRequest.setOrderType(OrderType.ADD);

        // Mock the behavior of orderService.modifyOrder
        Order expectedOrder = Order.builder()
                .id("order123")
                .customerId("customer123")
                .orderType(OrderType.ADD)
                .status(OrderStatus.NEW)
                .addingProducts(List.of(
                        Product.builder().id("prod1").build(),
                        Product.builder().id("prod2").build()
                ))
                .removeProducts(List.of(
                        Product.builder().id("prod3").build()
                ))
                .finalPrice(new BigDecimal("99.99"))
                .customer(Customer.builder().id("customer123").name("John Doe").build())
                .build();

        when(orderService.modifyOrder(any(ModifyOrderRequest.class))).thenReturn(expectedOrder);

        // Act & Assert
        mockMvc.perform(post("/order-service/modify")
                        .contentType("application/json")
                        .content("""
                        {
                          "orderId": "order123",
                          "orderType": "ADD",
                          "productId": "product123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order123"))
                .andExpect(jsonPath("$.customerId").value("customer123"))
                .andExpect(jsonPath("$.orderType").value("ADD"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.addingProducts[0].id").value("prod1"))
                .andExpect(jsonPath("$.addingProducts[1].id").value("prod2"))
                .andExpect(jsonPath("$.removeProducts[0].id").value("prod3"))
                .andExpect(jsonPath("$.finalPrice").value(99.99))
                .andExpect(jsonPath("$.customer.id").value("customer123"))
                .andExpect(jsonPath("$.customer.name").value("John Doe"));
    }
}