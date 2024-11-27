package es.merkle.component.validators.modify;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.OrderType;
import es.merkle.component.modify.validating.OrderValidatorRunnerImpl;
import es.merkle.component.modify.validating.validator.CustomOrderValidator;
import es.merkle.component.modify.validating.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomOrderValidatorTest {

    @MockBean
    private CustomOrderValidator customOrderValidator;

    @Autowired
    private OrderValidatorRunnerImpl orderValidatorRunnerImpl;

    private Order order;

    @BeforeEach
    void setup() {
        order = new Order("orderId", "customerId", "productId", OrderType.ADD, OrderStatus.NEW, null, null, BigDecimal.TEN, null);
    }

    @Test
    void shouldRunValidatorsAndSetStatusToInvalid() {
        when(customOrderValidator.validate(order)).thenReturn(true); // Order is invalid

        orderValidatorRunnerImpl.run(order);

        verify(customOrderValidator).setStatus(order, OrderStatus.INVALID);
    }

    @Test
    void shouldRunValidatorsAndSetStatusToValid() {
        when(customOrderValidator.validate(order)).thenReturn(false); // Order is valid

        orderValidatorRunnerImpl.run(order);

        verify(customOrderValidator).setStatus(order, OrderStatus.VALID);
    }

    @Test
    void shouldRunMultipleValidatorsInOrder() {
        List<OrderValidator> validators = List.of(customOrderValidator);
        validators.forEach(validator -> when(validator.validate(order)).thenReturn(false)); // All validators return valid

        orderValidatorRunnerImpl.run(order);

        for (OrderValidator validator : validators) {
            verify(validator).setStatus(order, OrderStatus.VALID);
        }
    }
}