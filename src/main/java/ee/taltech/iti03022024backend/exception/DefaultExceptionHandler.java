package ee.taltech.iti03022024backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {
    @ExceptionHandler(CampingRouteNotFoundException.class)
    public ResponseEntity<ExceptionResponse> exception(CampingRouteNotFoundException e) {
        log.error("Error occurred", e);

        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
