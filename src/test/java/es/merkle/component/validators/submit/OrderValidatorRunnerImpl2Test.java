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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderValidatorRunnerImpl2Test {

    @Autowired
    private OrderValidatorRunnerImpl2 orderValidatorRunner2;


    @Test
    void shouldRunAndUpdateResponseBasedOnOrderStatus() {
        String expectedMessage = "The order was not submitted because it's not in a final status";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.NEW, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        orderValidatorRunner2.run(response);

        // Check that the "NewOrderStatusValidator" was applied
        assertEquals(OrderStatus.FAILED, response.getOrder().getStatus());
        assertEquals(expectedMessage, response.getMessage());
    }

    @Test
    void shouldApplyCorrectValidatorForValidOrderStatus() {
        String expectedMessage = "The order was submitted successfully";
        Order order = new Order("orderId123", "customerId123", null, OrderType.ADD, OrderStatus.VALID, new ArrayList<>(), new ArrayList<>(), BigDecimal.TEN, null);
        SubmitOrderResponse response = new SubmitOrderResponse(order, expectedMessage);

        orderValidatorRunner2.run(response);

        // Check that the "ValidOrderStatusValidator" was applied
        assertEquals(OrderStatus.SUBMITTED, response.getOrder().getStatus());
        assertEquals(expectedMessage, response.getMessage());
    }
}
