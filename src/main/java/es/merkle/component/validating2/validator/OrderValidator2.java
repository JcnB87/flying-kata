package es.merkle.component.validating2.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.api.SubmitOrderResponse;

public interface OrderValidator2 {
    boolean validate(Order order);

    void updateResponse(SubmitOrderResponse submitOrderResponse);

}
