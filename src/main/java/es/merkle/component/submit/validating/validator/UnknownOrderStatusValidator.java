package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class UnknownOrderStatusValidator implements OrderValidator2 {
    @Override
    public boolean validate(Order order) {
        return false;
    }

    @Override
    public void updateResponse(SubmitOrderResponse submitOrderResponse) {
        throw new IllegalStateException("Unknown order status: "+ submitOrderResponse.getOrder().getStatus());
    }
}
