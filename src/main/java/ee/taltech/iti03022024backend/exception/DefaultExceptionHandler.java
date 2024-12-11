package ee.taltech.iti03022024backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    @ExceptionHandler({
            CampingRouteNotFoundException.class,
            UserNotFoundException.class,
            CampingRouteImageNotFound.class,
            CampingRouteGpxNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundException(RuntimeException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class})
    public ResponseEntity<ExceptionResponse> handleAlreadyExistsException(RuntimeException e) {
        log.warn("Resouor all new exceptions.rce already exists: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler({InvalidPasswordException.class,
            InvalidCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidInformationException(RuntimeException e) {
        log.warn("Given information is invalid: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler({
            CampingRouteImageStorageException.class,
            CampingRouteGpxStorageException.class})
    public ResponseEntity<ExceptionResponse> handleStorageException(CampingRouteImageStorageException e) {
        log.warn("Storage exception occurred: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(NotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(RuntimeException e) {
        log.warn("Forbidden action: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Some given information was not valid: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(HandlerMethodValidationException e) {
        String message = e.getAllValidationResults()
                .getFirst().getResolvableErrors()
                .getFirst().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message));
    }

    @ExceptionHandler(CommentNotExistsException.class)
    public ResponseEntity<ExceptionResponse> handleCommentNotExistsException(CommentNotExistsException e) {
        log.warn("Comment does not exist: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public void exception(Exception e) {
        log.error("An exception occurred.", e);
    }
}
