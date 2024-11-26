package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.api.SubmitOrderResponse;

public interface OrderValidator2 {
    boolean validate(Order order);

    void updateResponse(SubmitOrderResponse submitOrderResponse);

}
