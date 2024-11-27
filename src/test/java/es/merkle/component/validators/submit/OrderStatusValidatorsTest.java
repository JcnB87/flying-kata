package es.merkle.component.validators.submit;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.OrderType;
import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.submit.validating.validator.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderStatusValidatorsTest {

    @Autowired
    private FailedOrderStatusValidator failedOrderStatusValidator;

    @Autowired
    private InvalidOrderStatusValidator invalidOrderStatusValidator;

    @Autowired
    private NewOrderStatusValidator newOrderStatusValidator;

    @Autowired
    private SubmittedOrderStatusValidator submittedOrderStatusValidator;

    @Autowired
    private UnknownOrderStatusValidator unknownOrderStatusValidator;

    @Autowired
    private ValidOrderStatusValidator validOrderStatusValidator;

    @Test
    void testFailedOrderStatusValidator() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.FAILED, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, "The order was failed");

        assertTrue(failedOrderStatusValidator.validate(order));
        failedOrderStatusValidator.updateResponse(response);
        assertEquals("The order was failed", response.getMessage());
    }

    @Test
    void testInvalidOrderStatusValidator() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.INVALID, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, "The order was not submitted because it's INVALID");

        assertTrue(invalidOrderStatusValidator.validate(order));
        invalidOrderStatusValidator.updateResponse(response);
        assertEquals("The order was not submitted because it's INVALID", response.getMessage());
    }

    @Test
    void testSubmittedOrderStatusValidator() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.SUBMITTED, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, "The order was already submitted");

        assertTrue(submittedOrderStatusValidator.validate(order));
        submittedOrderStatusValidator.updateResponse(response);
        assertEquals("The order was already submitted", response.getMessage());
    }

    @Test
    void testUnknownOrderStatusValidator() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, null, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, "Unknown order status: " + order.getStatus());

        assertFalse(unknownOrderStatusValidator.validate(order));
        assertThrows(IllegalStateException.class, () -> unknownOrderStatusValidator.updateResponse(response));
    }

    @Test
    void testValidOrderStatusValidator() {
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.VALID, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, "The order was submitted successfully");

        assertTrue(validOrderStatusValidator.validate(order));
        validOrderStatusValidator.updateResponse(response);
        assertEquals(OrderStatus.SUBMITTED, response.getOrder().getStatus());
        assertEquals("The order was submitted successfully", response.getMessage());
    }
}
