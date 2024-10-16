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
        log.warn("Camping route error occurred", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage()));
    }
}
