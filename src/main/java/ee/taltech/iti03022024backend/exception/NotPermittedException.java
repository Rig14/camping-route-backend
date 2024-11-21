package ee.taltech.iti03022024backend.exception;

public class NotPermittedException extends RuntimeException {
    public NotPermittedException(String message) {
        super(message);
    }
}
