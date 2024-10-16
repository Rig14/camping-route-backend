package ee.taltech.iti03022024backend.exception;

import lombok.Getter;

@Getter
public class CampingRouteNotFoundException extends RuntimeException {
    private final long id;
    public CampingRouteNotFoundException(String string, long id) {
        super(string);
        this.id = id;
    }
}
