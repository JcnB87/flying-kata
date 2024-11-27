package es.merkle.component.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.merkle.component.model.api.SubmitOrderRequest;
import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.service.OrderService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();  // Initialize MockMvc
        objectMapper = new ObjectMapper();  // Initialize ObjectMapper for JSON conversion
    }

    @Test
    void modifyOrder_WithAddOperation_ReturnsOrderWithAddedProducts() throws Exception {
        ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest();
        modifyOrderRequest.setOrderId("order123");
        modifyOrderRequest.setProductId("product123");
        modifyOrderRequest.setOrderType(OrderType.ADD);

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

        mockMvc.perform(post("/order-service/modify")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(modifyOrderRequest)))
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

    @Test
    void shouldSubmitOrderSuccessfully() throws Exception {
        // Initialize the request and response objects
        SubmitOrderRequest submitOrderRequest = SubmitOrderRequest.builder()
                .orderId("orderId")
                .build();

        // Assume that an order object is returned by the service
        Product product = new Product("productId", "Product Name", ProductStatus.AVAILABLE, ProductCategory.TV, BigDecimal.TEN, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        Order order = new Order("orderId", "customerId", product.getId(), OrderType.ADD, OrderStatus.NEW, null, null, BigDecimal.TEN, null);
        SubmitOrderResponse submitOrderResponse = new SubmitOrderResponse(order, "Order submitted successfully");

        when(orderService.submitOrder(submitOrderRequest)).thenReturn(submitOrderResponse);

        mockMvc.perform(post("/order-service/submit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(submitOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.id").value("orderId"))
                .andExpect(jsonPath("$.message").value("Order submitted successfully"));
    }

}