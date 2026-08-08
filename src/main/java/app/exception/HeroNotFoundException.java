package app.exception;

public class HeroNotFoundException extends ApplicationException{

    public HeroNotFoundException() {
        super(
                "The requested hero could not be found.",
                "404",
                "Hero Not Found"
        );
    }
}
