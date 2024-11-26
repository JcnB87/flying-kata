package es.merkle.component.modify.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;

public interface OrderValidator {
    boolean validate(Order order);

    void setStatus(Order order, OrderStatus orderStatus);

}
