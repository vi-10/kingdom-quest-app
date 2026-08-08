package app.exception;

public class UserAlreadyExistsException extends ApplicationException {


    public UserAlreadyExistsException(String username) {
        super(
                "A user with username " + username + " already exists.",
                "409",
                "User Already Exists"
        );
    }
}
