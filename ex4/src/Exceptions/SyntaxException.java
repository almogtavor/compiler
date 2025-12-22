package Exceptions;

public class SyntaxException extends RuntimeException {
    private final int line;

    public SyntaxException(int line) {
		super();
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
