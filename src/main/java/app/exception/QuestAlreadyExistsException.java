package app.exception;

public class QuestAlreadyExistsException extends ApplicationException{

    public QuestAlreadyExistsException(String title) {
        super(
                "A quest with title " + title + " already exists.",
                "409",
                "Quest Already Exists"
        );
    }
}
