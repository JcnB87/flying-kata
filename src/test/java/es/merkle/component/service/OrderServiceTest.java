package es.merkle.component.service;

import es.merkle.component.application.OrderService;
import es.merkle.component.mapper.OrderMapper;
import es.merkle.component.model.*;
import es.merkle.component.model.api.ModifyOrderRequest;
import es.merkle.component.processing.OrderProcessorRunner;
import es.merkle.component.repository.OrderRepository;
import es.merkle.component.repository.adapter.OrderAdapter;
import es.merkle.component.repository.adapter.ProductAdapter;
import es.merkle.component.repository.entity.DbOrder;
import es.merkle.component.validating.OrderValidatorRunner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private ProductAdapter productAdapter;

    @MockBean
    private OrderAdapter orderAdapter;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private OrderValidatorRunner orderValidatorRunner;

    @MockBean
    private OrderProcessorRunner orderProcessorRunner;

    @MockBean
    private OrderMapper orderMapper;


    @ParameterizedTest
    @MethodSource("provideOrderValidationAndWithOrderTypeScenarios")
    void shouldCorrectlyModifyOrderBasedOnOrderType(OrderType orderType, ProductStatus productStatus, LocalDate expiryDate, LocalDate releaseDate, OrderStatus expectedOrderStatus) {
        String orderId = "orderId123";
        String customerId = "customerId123";
        String productId = "productId123";

        ModifyOrderRequest modifyOrderRequest = new ModifyOrderRequest(orderId, orderType, productId);
        DbOrder dbOrder = new DbOrder(orderId, customerId, OrderStatus.NEW, new ArrayList<>(), new ArrayList<>(), BigDecimal.ZERO, "Customer Name");
        Product product = new Product(productId, "Product Name", productStatus, ProductCategory.TV, BigDecimal.TEN, releaseDate, expiryDate);
        Order updatedOrder = new Order(orderId, customerId, null, OrderType.ADD, expectedOrderStatus, List.of(product), new ArrayList<>(), BigDecimal.TEN, null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(dbOrder));
        when(productAdapter.getProduct(productId)).thenReturn(product);
        when(orderMapper.mapModifyOrderRequestToOrder(modifyOrderRequest)).thenReturn(updatedOrder);
        when(orderAdapter.retrieveOrder(orderId)).thenReturn(updatedOrder);

        Order resultOrder = orderService.modifyOrder(modifyOrderRequest);

        verify(orderMapper, times(1)).updateOrder(any(Order.class), any(Order.class));
        verify(orderMapper, times(1)).mapModifyOrderRequestToOrder(any(ModifyOrderRequest.class));
        verify(orderAdapter, times(1)).retrieveOrder(any(String.class));
        verify(orderAdapter, times(1)).saveOrder(any(Order.class));
        verify(orderProcessorRunner, times(1)).run(any(Order.class));
        verify(orderValidatorRunner, times(1)).run(any(Order.class));

        assertNotNull(resultOrder);
        assertEquals(orderId, resultOrder.getId());
        assertEquals(expectedOrderStatus, resultOrder.getStatus());
    }

    private static Stream<Arguments> provideOrderValidationAndWithOrderTypeScenarios() {
        return Stream.of(
                Arguments.of(OrderType.ADD, ProductStatus.AVAILABLE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), OrderStatus.VALID),
                Arguments.of(OrderType.REMOVE, ProductStatus.AVAILABLE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), OrderStatus.VALID),
                Arguments.of(OrderType.ADD, ProductStatus.NOT_AVAILABLE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), OrderStatus.INVALID),
                Arguments.of(OrderType.ADD, ProductStatus.AVAILABLE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), OrderStatus.INVALID),
                Arguments.of(OrderType.REMOVE, ProductStatus.VIP, LocalDate.now().plusDays(1), LocalDate.now().plusDays(30), OrderStatus.INVALID),
                Arguments.of(OrderType.REMOVE, ProductStatus.AVAILABLE, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1), OrderStatus.INVALID)
        );
    }
}
