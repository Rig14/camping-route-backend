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
        log.error("Camping route not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(CampingRouteImageStorageException.class)
    public ResponseEntity<ExceptionResponse> exception(CampingRouteImageStorageException e) {
        log.error("Camping route image storage exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(CampingRouteImageNotFound.class)
    public ResponseEntity<ExceptionResponse> exception(CampingRouteImageNotFound e) {
        log.error("Camping route image not found", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage()));
    }


    @ExceptionHandler(RuntimeException.class)
    public void exception(RuntimeException e) {
        log.error("Camping route image storage exception occurred.", e);
    }

}
