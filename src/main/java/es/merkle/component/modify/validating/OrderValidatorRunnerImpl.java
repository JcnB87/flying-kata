package es.merkle.component.modify.validating;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.modify.validating.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderValidatorRunnerImpl implements OrderValidatorRunner {

    private final List<OrderValidator> orderValidators;

    @Override
    public void run(Order order) {
        for (int i = orderValidators.size() -1; i >= 0; i--) {
            if (orderValidators.get(i).validate(order)) { //if true = Invalid
                orderValidators.get(i).setStatus(order, OrderStatus.INVALID);
            } else {
                orderValidators.get(i).setStatus(order, OrderStatus.VALID);
            }
        }
    }


}
