package app.exception;

public class UserNotFoundException extends ApplicationException{


    public UserNotFoundException() {
        super(
                "The requested user could not be found.",
                "404",
                "User Not Found"
        );
    }
}
