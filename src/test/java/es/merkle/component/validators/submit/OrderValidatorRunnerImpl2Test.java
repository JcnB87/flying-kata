package es.merkle.component.validators.submit;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.OrderType;
import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.submit.validating.OrderValidatorRunnerImpl2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderValidatorRunnerImpl2Test {

    @Autowired
    private OrderValidatorRunnerImpl2 singleOrderValidator;


    @Test
    void test_whenNewOrderStatus_shouldValidateOrderStatusAsFailed() {
        String expectedMessage = "The order was not submitted because it's not in a final status";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.NEW, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        singleOrderValidator.run(response);

        assertEquals(OrderStatus.FAILED, response.getOrder().getStatus());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void test_whenValidOrderStatus_shouldValidateOrderStatusAsSubmitted() {
        String expectedMessage = "The order was submitted successfully";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.VALID, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        singleOrderValidator.run(response);

        assertEquals(OrderStatus.SUBMITTED, response.getOrder().getStatus());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void test_whenFailedOrderStatus_shouldFailOrderStatus() {
        String expectedMessage = "The order was failed";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.FAILED, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        singleOrderValidator.run(response);

        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void test_whenInvalidOrderStatus_shouldInvalidateOrderStatusAsFailed() {
        String expectedMessage = "The order was not submitted because it's INVALID";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.INVALID, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        singleOrderValidator.run(response);

        assertEquals(OrderStatus.FAILED, response.getOrder().getStatus());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void test_whenSubmittedOrderStatus_shouldntValidateOrderStatus() {
        String expectedMessage = "The order was already submitted";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.SUBMITTED, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        singleOrderValidator.run(response);

        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void test_whenUnknownOrderStatus_shouldThrowIllegalStateException() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, null, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            singleOrderValidator.run(response);
        });

        assertTrue(exception.getMessage().contains("Unknown order status: null"));
    }

}
