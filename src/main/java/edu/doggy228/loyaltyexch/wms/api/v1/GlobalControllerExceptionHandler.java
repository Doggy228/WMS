package edu.doggy228.loyaltyexch.wms.api.v1;

import edu.doggy228.loyaltyexch.wms.modeljson.ResponseError;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseError> handleRuntime(ApiException ex) {
        return new ResponseEntity<>(ex.createResponseError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
