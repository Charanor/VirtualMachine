package se.student.liu.jessp088.vm;

import static se.student.liu.jessp088.vm.VMState.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * The default implementation of a {@link VirtualMachine}.
 */
public class DefaultVirtualMachine implements VirtualMachine
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultVirtualMachine.class);

	private final Stack stack;
	private final Variables variables;

	private Bytecode currentCode = null;
	private Instruction currentInstruction = null;
	private final List<Integer> breakpoints;

	private VMState state;
	private VMState previousState;

	private final List<DebugListener> listeners;

	private String error = null;
	private boolean wasAtBreakpoint = false;

	public DefaultVirtualMachine(final int maxStackSize, final int maxVariableSize) {
		stack = new Stack(maxStackSize);
		variables = new Variables(maxVariableSize);
		listeners = new ArrayList<>();
		breakpoints = new ArrayList<>();
		state = END_SUCCESS;
		previousState = END_SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The {@link DefaultVirtualMachine} implementation is an <b>infinite loop</b> that can only be
	 * broken by calling {@link DefaultVirtualMachine#stopExecution()} or if the code execution is
	 * stopped either by error or by finishing successfully.
	 * </p>
	 */
	@Override
	public void execute(final Bytecode code) throws IllegalStateException {
		if (!isStopped()) {
			LOGGER.error("Trying to execute code on a virtual machine that is not stopped!");
			throw new IllegalStateException("Cannot execute a running or paused VirtualMachine!");
		}

		LOGGER.trace("Executing code {}", code);
		runInternal(code, false);
	}

	@Override
	public void debug(final Bytecode code) throws IllegalStateException {
		if (!isStopped()) {
			LOGGER.error("Trying to debug code on a virtual machine that is not stopped!");
			throw new IllegalStateException("Cannot debug a running or paused VirtualMachine!");
		}

		LOGGER.trace("Debugging code {}", code);
		runInternal(code, true);
	}

	private void runInternal(final Bytecode code, final boolean debug) {
		currentCode = code;

		setState(debug ? DEBUGGING : EXECUTING);

		if (wasStopped()) {
			LOGGER.info("Starting virtual machine.");
			beforeExecution();
		}
		// catch listener pause or stop
		if (!isRunning()) {
			LOGGER.debug("Virtual machine was stopped by a listener before processing could begin.");
			return;
		}

		while (currentCode.hasNext()) {
			if (wasAtBreakpoint) {
				// We don't want to break on the same line twice
				wasAtBreakpoint = false;
				LOGGER.trace("Skipping breakpoint on pointer {} (already executed).", Integer.valueOf(currentCode.getPtr()));
			} else if (debug && isBreakpointAt(currentCode.getPtr())) {
				wasAtBreakpoint = true;
				LOGGER.debug("Hit breakpoint at pointer {}.", Integer.valueOf(currentCode.getPtr()));
				setState(PAUSE_BREAKPOINT);
				return;
			}

			currentInstruction = currentCode.next();

			beforeInstruction();
			if (!isRunning()) {
				LOGGER.debug("Virtual machine stopped by listener before instruction could be processed.");
				// Revert to previous instruction or we will skip an instruction
				code.setPtr(code.getPtr() - 1);
				LOGGER.trace("Reverting instruction pointer.");
				return;
			}

			try {
				LOGGER.trace("Processing instruction {}: {}.", Integer.valueOf(currentCode.getPtr() - 1), currentInstruction);
				currentInstruction.process(stack, code, variables);
				afterInstruction();
			} catch (final InstructionException e) {
				this.error = String
					.format("Error executing instruction %s on line %s: %s", currentInstruction,
							Integer.valueOf(getCurrentLineNumber()), e);
				LOGGER.error(error);

				currentCode.setPtr(0);
				LOGGER.trace("Resetting instruction pointer to 0.");
				setState(END_ERROR);
				afterInstruction();
				afterExecution();
				return;
			}
		}

		afterExecution();
		// setState after so user cannot override state in listeners
		setState(END_SUCCESS);

		LOGGER.info("End of execution. Virtual machine finished successfully.");
		if (!stack.isEmpty()) {
			LOGGER.warn("Virtual machine finished with items still on the stack.");
			LOGGER.debug("Stack: {}", stack);
		}
	}

	@Override
	public void pauseExecution() {
		if (!isRunning()) {
			LOGGER.trace("Attempt to pause virtual machine in non-running state {}.", state);
			return;
		}

		LOGGER.info("Pausing virtual machine.");
		setState(PAUSE_USER);
	}

	@Override
	public void resumeExecution() {
		if (!isPaused()) {
			LOGGER.trace("Attempt to resume virtual machine in non-paused state {}.", state);
			return;
		}

		if (!wasRunning()) {
			LOGGER.error("Cannot resume from state {} when previous state was {}!", state, previousState);
			final String format = "Cannot resume from state %s when previous state was %s!";
			throw new IllegalStateException(String.format(format, state, previousState));
		}

		final boolean debug = previousState == DEBUGGING;
		LOGGER.info("Resuming virtual machine.");
		runInternal(currentCode, debug);
	}

	@Override
	public void stopExecution() {
		if (isStopped()) {
			LOGGER.trace("Attempt to stop already stopped virtual machine.");
			return;
		}

		LOGGER.info("Stopping virtual machine.");
		currentCode.setPtr(0);
		setState(END_USER);
	}

	@Override
	public VMState getState() {
		return state;
	}

	@Override
	public VMState getPreviousState() {
		return previousState;
	}

	@Override
	public void addDebugListener(final DebugListener listener) {
		if (!this.listeners.contains(listener)) {
			LOGGER.trace("Adding debug listener.");
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeDebugListener(final DebugListener listener) {
		if (this.listeners.contains(listener)) {
			LOGGER.trace("Removing debug listener.");
			this.listeners.remove(listener);
		}
	}

	@Override
	public void addBreakpoint(final int instructionPtr) {
		if (!isBreakpointAt(instructionPtr)) {
			LOGGER.trace("Adding breakpoint at {}", Integer.valueOf(instructionPtr));
			breakpoints.add(Integer.valueOf(instructionPtr));
		} else LOGGER.trace("Already a breakpoint at {}.", Integer.valueOf(instructionPtr));
	}

	@Override
	public void removeBreakpoint(final int instructionPtr) {
		if (isBreakpointAt(instructionPtr)) {
			LOGGER.trace("Removing breakpoint at {}", Integer.valueOf(instructionPtr));
			// Must cast otherwise it uses the wrong remove() method
			breakpoints.remove(Integer.valueOf(instructionPtr));
		} else LOGGER.trace("No breakpoint at {} to remove.", Integer.valueOf(instructionPtr));
	}

	@Override
	public void removeAllBreakpoints() {
		breakpoints.clear();
	}

	@Override
	public boolean isBreakpointAt(final int instructionPtr) {
		return breakpoints.contains(Integer.valueOf(instructionPtr));
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
		LOGGER.debug("First time running code, clearing state.");
		stack.clear();
		variables.clear();

		LOGGER.trace("Notifying listeners beforeExecution().");
		for (final DebugListener l : listeners) {
			l.beforeExecution(this);
		}
	}

	private void afterExecution() {
		LOGGER.trace("Notifying listeners afterExecution().");
		for (final DebugListener l : listeners) {
			l.afterExecution(this);
		}
	}

	private void beforeInstruction() {
		LOGGER.trace("Notifying listeners beforeInstruction().");
		for (final DebugListener l : listeners) {
			l.beforeInstruction(this);
		}
	}

	private void afterInstruction() {
		LOGGER.trace("Notifying listeners afterInstruction().");
		for (final DebugListener l : listeners) {
			l.afterInstruction(this);
		}
	}

	private void onStateChanged() {
		LOGGER.trace("Notifying listeners onStateChanged().");
		for (final DebugListener l : listeners) {
			l.onStateChanged(this);
		}
	}

	private void setState(final VMState state) {
		if (this.state == state) {
			LOGGER.trace("Attempt to set state to {} when virtual machine was already in that state!", state);
			return;
		}

		LOGGER.trace("Changing virtual machine state from {} to {}.", this.state, state);
		this.previousState = this.state;
		this.state = state;
		onStateChanged();
	}

}
