package Exceptions;

public class SemanticException extends RuntimeException {
    private final int line;

    public SemanticException(int line) {
        super();
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
