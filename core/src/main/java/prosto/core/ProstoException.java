package prosto.core;

public class ProstoException extends RuntimeException {
    public ProstoErrorCode code;
    public String message;
    public String description;

    public Exception e;

    @Override
    public String toString() {
        return "[" + this.code + "]: " + this.message;
    }

    public ProstoException(ProstoErrorCode code, String message, String description, Exception e) {
        this(code, message, description);
        this.e = e;
    }
    public ProstoException(ProstoErrorCode code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
