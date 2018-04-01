package se.student.liu.jessp088.vm;

import static se.student.liu.jessp088.vm.VirtualMachine.VMState.DEBUGGING;
import static se.student.liu.jessp088.vm.VirtualMachine.VMState.END_ERROR;
import static se.student.liu.jessp088.vm.VirtualMachine.VMState.END_SUCCESS;
import static se.student.liu.jessp088.vm.VirtualMachine.VMState.END_USER;
import static se.student.liu.jessp088.vm.VirtualMachine.VMState.EXECUTING;
import static se.student.liu.jessp088.vm.VirtualMachine.VMState.PAUSE_USER;

import java.util.ArrayList;
import java.util.List;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/** <b>NOT</b> thread-safe, use {@link AtomicVirtualMachine} or {@link ThreadedVirtualMachine}
 * instead.
 *
 * @author Charanor */
public class DefaultVirtualMachine implements VirtualMachine {
	private final Stack stack;
	private final Variables variables;

	private Bytecode currentCode;
	private Instruction currentInstruction;
	private final List<Integer> breakpoints;

	private VMState state;
	private VMState previousState;

	private final List<DebugListener> listeners;

	private String error;
	private boolean wasAtBreakpoint = false;

	public DefaultVirtualMachine(final int maxStackSize, final int maxVariableSize) {
		stack = new Stack(maxStackSize);
		variables = new Variables(maxVariableSize);
		listeners = new ArrayList<>();
		breakpoints = new ArrayList<>();
		previousState = state = VMState.END_SUCCESS;
	}

	/** {@inheritDoc}
	 * <p>
	 * The {@link DefaultVirtualMachine} implementation is an <b>infinite loop</b> that can only be
	 * broken by calling {@link DefaultVirtualMachine#stopExecution()} or if the code execution is
	 * stopped either by error or by finishing successfully.
	 * </p>
	*/
	@Override
	public void execute(final Bytecode code) throws IllegalStateException {
		if (!isStopped())
			throw new IllegalStateException("Cannot execute a running or paused VirtualMachine!");
		runInternal(code, false);
	}

	@Override
	public void debug(final Bytecode code) throws IllegalStateException {
		if (!isStopped())
			throw new IllegalStateException("Cannot debug a running or paused VirtualMachine!");
		runInternal(code, true);
	}

	private void runInternal(final Bytecode code, final boolean debug) {
		currentCode = code;

		setState(debug ? DEBUGGING : EXECUTING);

		if (wasStopped()) beforeExecution();
		if (!isRunning()) return; // catch listener pause or stop

		while (currentCode.hasNext()) {
			if (wasAtBreakpoint) {
				// We don't want to break on the same line twice
				wasAtBreakpoint = false;
			} else if (debug && isBreakpointAt(currentCode.getPtr())) {
				wasAtBreakpoint = true;
				setState(VMState.PAUSE_BREAKPOINT);
				return;
			}

			currentInstruction = currentCode.next();

			beforeInstruction();
			if (!isRunning()) {
				// Revert to previous instruction or we will skip an instruction
				code.setPtr(code.getPtr() - 1);
				return;
			}

			try {
				currentInstruction.process(stack, code, variables);
				afterInstruction();
			} catch (final InstructionException e) {
				this.error = String.format("Error executing instruction %s on line %s: %s",
						currentInstruction, getCurrentLineNumber(), e);

				currentCode.setPtr(0);
				setState(END_ERROR);
				afterInstruction();
				afterExecution();
				return;
			}

		}

		setState(END_SUCCESS);
		afterExecution();
	}

	@Override
	public void pauseExecution() {
		if (!isRunning()) return;
		setState(PAUSE_USER);
	}

	@Override
	public void resumeExecution() {
		if (!isPaused()) return;
		if (!wasRunning()) {
			final String format = "Cannot resume from state %s when previous state was %s!";
			throw new IllegalStateException(
					String.format(format, getCurrentState(), getPreviousState()));
		}

		runInternal(currentCode, previousState == DEBUGGING);
	}

	@Override
	public void stopExecution() {
		if (isStopped()) return;
		currentCode.setPtr(0);
		setState(END_USER);
	}

	@Override
	public VMState getCurrentState() {
		return state;
	}

	@Override
	public VMState getPreviousState() {
		return previousState;
	}

	@Override
	public void addDebugListener(final DebugListener listener) {
		if (!this.listeners.contains(listener)) this.listeners.add(listener);
	}

	@Override
	public void removeDebugListener(final DebugListener listener) {
		if (this.listeners.contains(listener)) this.listeners.remove(listener);
	}

	@Override
	public void addBreakpoint(final int instructionPtr) {
		if (!isBreakpointAt(instructionPtr)) breakpoints.add(instructionPtr);
	}

	@Override
	public void removeBreakpoint(final int instructionPtr) throws IllegalArgumentException {
		// Must cast otherwise it uses the wrong remove() method
		if (isBreakpointAt(instructionPtr)) breakpoints.remove((Integer) instructionPtr);
	}

	@Override
	public boolean isBreakpointAt(final int instructionPtr) {
		return breakpoints.contains(instructionPtr);
	}

	@Override
	public Instruction getCurrentInstruction() {
		return currentInstruction;
	}

	@Override
	public int getCurrentInstructionPtr() {
		return currentCode.getPtr() - 1;
	}

	@Override
	public int getCurrentLineNumber() {
		return currentCode.getLineNumber();
	}

	@Override
	public int convertPtrToLine(final int ptr) throws IllegalArgumentException {
		return currentCode.toLineNumber(ptr);
	}

	@Override
	public int convertLineToPtr(final int line) throws IllegalArgumentException {
		return currentCode.toPointer(line);
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public boolean hasErrors() {
		return error != null && !error.isEmpty();
	}

	@Override
	public Stack getStack() {
		return new Stack(stack);
	}

	@Override
	public Variables getVariables() {
		return new Variables(variables);
	}

	private void beforeExecution() {
		stack.clear();
		variables.clear();
		for (final DebugListener l : listeners) {
			l.beforeExecution(this);
		}
	}

	private void afterExecution() {
		for (final DebugListener l : listeners) {
			l.afterExecution(this);
		}
	}

	private void beforeInstruction() {
		for (final DebugListener l : listeners) {
			l.beforeInstruction(this);
		}
	}

	private void afterInstruction() {
		for (final DebugListener l : listeners) {
			l.afterInstruction(this);
		}
	}

	private void onStateChanged(final VMState state) {
		for (final DebugListener l : listeners) {
			l.onStateChanged(this);
		}
	}

	private void setState(final VMState state) {
		if (this.state == state) return;
		this.previousState = this.state;
		this.state = state;
		onStateChanged(state);
	}

}
