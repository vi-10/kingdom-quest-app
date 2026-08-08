package app.exception;

public class QuestNotFoundException extends ApplicationException{

    public QuestNotFoundException() {
        super(
                "The requested quest could not be found.",
                "404",
                "Quest Not Found"
        );
    }
}
