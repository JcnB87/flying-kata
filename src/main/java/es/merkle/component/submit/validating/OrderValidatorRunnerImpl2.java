package es.merkle.component.submit.validating;

import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.submit.validating.validator.SingleOrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderValidatorRunnerImpl2 implements OrderValidatorRunner2 {

    private final List<SingleOrderValidator> singleOrderValidators;

    public void run(SubmitOrderResponse submitOrderResponse) {
        for (int i = singleOrderValidators.size() -1; i >= 0; i--) {
            singleOrderValidators.get(i).validateAndUpdateResponse(submitOrderResponse);
            break;
        }
    }


}
