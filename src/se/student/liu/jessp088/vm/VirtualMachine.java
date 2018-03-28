package se.student.liu.jessp088.vm;

import java.util.Optional;
import java.util.stream.Stream;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/** This class is used to run instances of {@link Bytecode}. It also
 * handles the Stack and Variable instances.
 *
 * @author Charanor */
public class VirtualMachine {
	public static boolean DEBUG = false;

	private final Stack stack;
	private final Variables variables;

	private String error;

	/** Create a new VirtualMachine instance.
	 *
	 * @param maxStackSize
	 *            the absolute maximum stack size. The stack cannot
	 *            grow past this size.
	 * @param maxVariableSize
	 *            the absolute maximum number of variables. */
	public VirtualMachine(final int maxStackSize, final int maxVariableSize) {
		this.stack = new Stack(maxStackSize);
		this.variables = new Variables(maxVariableSize);
		this.error = "";
	}

	public boolean execute(final Bytecode code) {
		stack.clear();
		variables.clear();

		while (code.hasNext()) {
			final Instruction ins = code.next();
			if (DEBUG) System.out.printf("Handing instruction %-15s", ins);
			try {
				ins.process(stack, code, variables);
			} catch (final InstructionException e) {
				final Optional<Throwable> rootCause = Stream.iterate(e, Throwable::getCause)
						.filter(element -> element.getCause() == null).findFirst();
				if (DEBUG) System.err.println("ERROR");
				this.error = String.format("Error executing instruction at ptr %s of type %s. %s\n",
						code.getPtr(), ins.getClass().getSimpleName(),
						rootCause.get().getMessage());
				return false;
			}
			if (DEBUG) System.out.println(stack);
		}

		if (stack.getStackPtr() > 0 && DEBUG) System.err.println(
				"Warning: VM ended with items still on the stack. Code could probably be optimized.");
		return true;
	}

	public Stack getStack() {
		return stack;
	}

	public Variables getVariables() {
		return variables;
	}

	public boolean hasErrors() {
		return !error.isEmpty();
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return "Stack: " + stack + " Variables: " + variables;
	}
}
