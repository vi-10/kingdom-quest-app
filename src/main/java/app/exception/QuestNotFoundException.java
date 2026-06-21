package app.exception;

public class QuestNotFoundException extends RuntimeException{
    public QuestNotFoundException(String message) {
        super(message);
    }
}
