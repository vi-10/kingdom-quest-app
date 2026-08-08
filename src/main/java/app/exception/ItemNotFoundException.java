package app.exception;

public class ItemNotFoundException extends ApplicationException{

    public ItemNotFoundException() {
        super(
                "The requested item could not be found.",
                "404",
                "Item Not Found"
        );
    }
}
