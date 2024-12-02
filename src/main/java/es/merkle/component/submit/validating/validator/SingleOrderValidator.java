package es.merkle.component.submit.validating.validator;


import es.merkle.component.model.api.SubmitOrderResponse;

public interface SingleOrderValidator {

    void validateAndUpdateResponse(SubmitOrderResponse submittedOrderResponse);

}