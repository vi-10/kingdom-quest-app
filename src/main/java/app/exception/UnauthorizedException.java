package app.exception;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(
                message,
                "403",
                "Unauthorized"
        );
    }
}
