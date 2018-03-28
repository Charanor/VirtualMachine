package se.student.liu.jessp088.vm.exceptions;

public class VirtualMachineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public VirtualMachineException(final String message) {
		super(message);
	}

	public VirtualMachineException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
