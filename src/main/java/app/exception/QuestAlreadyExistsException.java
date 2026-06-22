package app.exception;

public class QuestAlreadyExistsException extends RuntimeException{
    public QuestAlreadyExistsException(String message) {
        super(message);
    }
}
