package regalloc;

public class RegisterAllocationFailedException extends RuntimeException {
    public RegisterAllocationFailedException() {
        super("Register Allocation Failed");
    }
}
