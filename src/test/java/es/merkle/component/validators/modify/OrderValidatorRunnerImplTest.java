package es.merkle.component.validators.modify;

import static org.mockito.Mockito.*;

import es.merkle.component.model.*;
import es.merkle.component.modify.validating.OrderValidatorRunnerImpl;
import es.merkle.component.modify.validating.validator.OrderValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class OrderValidatorRunnerImplTest {

    @MockBean
    private OrderValidator orderValidator;

    @Test
    void shouldSetOrderStatusToInvalidWhenProductStatusIsNotAvailableThenValidatorReturnsTrue() {
        List<OrderValidator> validators = List.of(orderValidator);

        Product product = new Product("productId", "Product Name", ProductStatus.NOT_AVAILABLE, ProductCategory.TV, BigDecimal.TEN, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        Order order = new Order("orderId", "customerId", product.getId(), OrderType.ADD, OrderStatus.NEW, null, null, BigDecimal.TEN, null);

        OrderValidatorRunnerImpl runner = new OrderValidatorRunnerImpl(validators);

        when(orderValidator.validate(order)).thenReturn(true);
        runner.run(order);

        verify(orderValidator).setStatus(order, OrderStatus.INVALID);
        verify(orderValidator, never()).setStatus(order, OrderStatus.VALID);
    }

    @Test
    void shouldSetOrderStatusToValidWhenProductStatusIsAvailableThenValidatorReturnsFalse() {
        List<OrderValidator> validators = List.of(orderValidator);

        Product product = new Product("productId", "Product Name", ProductStatus.AVAILABLE, ProductCategory.TV, BigDecimal.TEN, LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        Order order = new Order("orderId", "customerId", product.getId(), OrderType.ADD, OrderStatus.NEW, null, null, BigDecimal.TEN, null);

        OrderValidatorRunnerImpl runner = new OrderValidatorRunnerImpl(validators);

        when(orderValidator.validate(order)).thenReturn(false);
        runner.run(order);

        verify(orderValidator).setStatus(order, OrderStatus.VALID);
        verify(orderValidator, never()).setStatus(order, OrderStatus.INVALID);
    }
}
