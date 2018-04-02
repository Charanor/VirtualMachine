package se.student.liu.jessp088.vm;

import se.student.liu.jessp088.vm.instructions.Instruction;

/**
 * An object representing a virtual machine that can run and debug bytecode given to it. It is also
 * responsible for containing the stack and any variables used by the code.
 *
 * @author Charanor
 */
public interface VirtualMachine
{
	/**
	 * Executes the code in the {@link Bytecode} object. Calling this method when the virtual
	 * machine is running or paused will cause an {@link IllegalStateException} to be thrown.
	 *
	 * @param code the code to execute.
	 *
	 * @throws IllegalStateException if called when {@link VirtualMachine} is running or paused.
	 */
	void execute(Bytecode code) throws IllegalStateException;

	/**
	 * Debugs the code in the {@link Bytecode} object. Calling this method when the virtual machine
	 * is running or paused will cause an {@link IllegalStateException} to be thrown.
	 *
	 * @param code the code to debug.
	 *
	 * @throws IllegalStateException if called when {@link VirtualMachine} is running or paused.
	 */
	void debug(Bytecode code) throws IllegalStateException;

	/**
	 * Pauses the execution of the virtual machine. The exact effects of pausing is
	 * <b>implementation specific</b> and is allowed to depend on the current and previous
	 * {@link VMState}. The only guarantee made is that pausing prohibits any more
	 * {@link Instruction}s from being processed until {@link VirtualMachine#resumeExecution()} is
	 * called. If the virtual machine is not running this method does nothing.
	 */
	void pauseExecution();

	/**
	 * Resumes the execution of the virtual machine. If the virtual machine is not paused this
	 * method does nothing.
	 */
	void resumeExecution();

	/**
	 * Stops the execution of the virtual machine. The exact effects of stopping is
	 * <b>implementation specific</b> and is allowed to depend on the current and previous
	 * {@link VMState}. The only guarantee made is that stopping prohibits any more
	 * {@link Instruction}s from being processed until {@link VirtualMachine#execute(Bytecode)} or
	 * {@link VirtualMachine#debug(Bytecode)} is called again and that the program counter is reset.
	 * If the virtual machine is not running this method does nothing.
	 */
	void stopExecution();

	/**
	 * Gets the current {@link VMState} of the virtual machine. The default state is
	 * {@link VMState#END_SUCCESS}.
	 *
	 * @return the current state.
	 */
	VMState getCurrentState();

	/**
	 * Gets the previous {@link VMState} of the virtual machine. The default state is
	 * {@link VMState#END_SUCCESS}.
	 *
	 * @return the previous state.
	 */
	VMState getPreviousState();

	/** @return true if the virtual machine is currently running; false otherwise. */
	default boolean isRunning() {
		return getCurrentState().running;
	}

	/** @return true if the virtual machine is currently paused; false otherwise. */
	default boolean isPaused() {
		return getCurrentState().paused;
	}

	/** @return true if the virtual machine is currently stopped; false otherwise. */
	default boolean isStopped() {
		return getCurrentState().stopped;
	}

	/** @return true if the virtual machine previous state was running; false otherwise. */
	default boolean wasRunning() {
		return getPreviousState().running;
	}

	/** @return true if the virtual machine previous state was paused; false otherwise. */
	default boolean wasPaused() {
		return getPreviousState().paused;
	}

	/** @return true if the virtual machine previous state was stopped; false otherwise. */
	default boolean wasStopped() {
		return getPreviousState().stopped;
	}

	/**
	 * Adds a {@link DebugListener} to listen for debug events from this virtual machine.
	 *
	 * @param listener the listener
	 */
	void addDebugListener(DebugListener listener);

	/**
	 * Removes a previously registered {@link DebugListener} from this virtual machine. If the
	 * listener is not registered this method does nothing.
	 *
	 * @param listener the listener
	 */
	void removeDebugListener(DebugListener listener);

	/**
	 * Adds a breakpoint at the specified instruction pointer. A breakpoint is automatically
	 * triggered and does not require external interference. The exact effects of a breakpoint is
	 * <b>implementation specific</b>. The only guarantees made are:
	 * <ul>
	 * <li>A breakpoint prohibits any more {@link Instruction}s from being processed until
	 * {@link VirtualMachine#resumeExecution()} is called.</li>
	 * <li>A breakpoint happens before the instruction at pointer <code>instructionPtr</code> is
	 * called.</li>
	 * <li>The first instruction executed after resuming after a breakpoint will be the instruction
	 * at <code>instructionPtr</code>.</li>
	 * <li>A breakpoint happens before {@link DebugListener#beforeExecution(VirtualMachine)} is
	 * called on any listeners.</li>
	 * </ul>
	 * <p>
	 * <b><u>Please note that</u></b> Any calls to {@link VirtualMachine#getCurrentInstruction()}
	 * when the virtual machine is paused because of a breakpoint <b>is not guaranteed</b> to return
	 * the instruction that the breakpoint stopped on and is thus <b>undefined behavior</b>.
	 * </p>
	 *
	 * @param instructionPtr the pointer to break at
	 */
	void addBreakpoint(int instructionPtr);

	/**
	 * Removes a breakpoint at the specified instruction pointer. If there is no breakpoint at the
	 * specified instruction pointer this method does nothing.
	 *
	 * @param instructionPtr the pointer to remove the break from
	 */
	void removeBreakpoint(int instructionPtr);

	/**
	 * Returns if there is a breakpoint at the specified instruction pointer.
	 *
	 * @param instructionPtr the pointer
	 *
	 * @return true if there is a breakpoint; false otherwise or if instructionPtr is invalid.
	 */
	boolean isBreakpointAt(int instructionPtr);

	/**
	 * Gets the current {@link Instruction}. Whether or not the instruction has already been
	 * executed when this call is made is <b>implementation specific</b>.
	 *
	 * @return the current {@link Instruction}
	 */
	Instruction getCurrentInstruction();

	/**
	 * Gets the instruction pointer pointing to the index of the {@link Instruction} from
	 * {@link VirtualMachine#getCurrentInstruction()} in a {@link Bytecode} object.
	 *
	 * @return the pointer
	 */
	int getCurrentInstructionPtr();

	/**
	 * Gets the line number of the {@link Instruction} from
	 * {@link VirtualMachine#getCurrentInstruction()}.
	 *
	 * @return the line number
	 */
	int getCurrentLineNumber();

	/**
	 * Converts an instruction pointer to the line number where the instruction can be found.
	 *
	 * @param ptr the pointer
	 *
	 * @return the line number
	 * @throws IllegalArgumentException if there is no line corresponding to the pointer <b>or</b> if the pointer is
	 *                                  invalid.
	 */
	int convertPtrToLine(int ptr) throws IllegalArgumentException;

	/**
	 * Converts a line number to the instruction pointer found on the line number.
	 *
	 * @param line the line
	 *
	 * @return the instruction pointer
	 * @throws IllegalArgumentException if there is no pointer corresponding to the line given.
	 */
	int convertLineToPtr(int line) throws IllegalArgumentException;

	/**
	 * Gets the errors the virtual machine has experienced. This method is <b>only</b> guaranteed
	 * to return an accurate error message when the virtual machine is stopped. Otherwise the return
	 * value is <b>implementation specific</b> and is allowed to be <code>null</code>.
	 *
	 * @return the current error message(s). May be null.
	 */
	String getError();

	/**
	 * Checks if the virtual machine currently has any errors. This method is <b>only</b>
	 * guaranteed to return an accurate value when the virtual machine is stopped. Otherwise the
	 * return value is <b>implementation specific</b>.
	 *
	 * @return if the {@link VirtualMachine} currently has any errors.
	 */
	boolean hasErrors();

	/**
	 * Returns a copy of the current {@link Stack} instance contained in this
	 * {@link VirtualMachine}.
	 *
	 * @return the stack.
	 */
	Stack getStack();

	/**
	 * Returns a copy of the current {@link Variables} instance contained in this
	 * {@link VirtualMachine}.
	 *
	 * @return the variables.
	 */
	Variables getVariables();

	/**
	 * The types of state the {@link VirtualMachine} can be in at one specific time.
	 *
	 * @author Charanor
	 */
	public static enum VMState
	{
		/** The {@link VirtualMachine} is currently executing code normally. */
		EXECUTING(true, false, false), /** The {@link VirtualMachine} is currently debugging code. */
	DEBUGGING(true, false, false), /** The code executed and ended successfully. */
	END_SUCCESS(false, false, true), /**
	 * The code executed and ended in an error accessible by {@link VirtualMachine#getError()}
	 * .
	 */
	END_ERROR(false, false, true), /** The execution was stopped by a user. */
	END_USER(false, false, true), /** The execution hit a breakpoint and paused execution. */
	PAUSE_BREAKPOINT(false, true, false), /** The execution was paused by a user. */
	PAUSE_USER(false, true, false);

		public final boolean running, paused, stopped;

		private VMState(final boolean running, final boolean paused, final boolean stopped) {
			this.running = running;
			this.paused = paused;
			this.stopped = stopped;
		}
	}

	public static interface DebugListener
	{
		/**
		 * Called before {@link VirtualMachine#execute(Bytecode)} or
		 * {@link VirtualMachine#debug(Bytecode)} is called but after state has been set to
		 * {@link VMState#EXECUTING} or {@link VMState#DEBUGGING}.
		 *
		 * @param vm the virtual machine
		 */
		void beforeExecution(VirtualMachine vm);

		/**
		 * Called after {@link VirtualMachine#execute(Bytecode)} or
		 * {@link VirtualMachine#debug(Bytecode)} is called and after state has been set to
		 * {@link VMState#END_SUCCESS}, {@link VMState#END_ERROR}, or {@link VMState#END_USER}.
		 *
		 * @param vm the virtual machine
		 */
		void afterExecution(VirtualMachine vm);

		/**
		 * Called before the {@link VirtualMachine} handles an {@link Instruction}. Calling
		 * {@link VirtualMachine#stopExecution()} is guaranteed to stop the execution of the
		 * {@link VirtualMachine} before the Instruction is handled.
		 *
		 * @param vm the virtual machine
		 */
		void beforeInstruction(VirtualMachine vm);

		/**
		 * Called after the {@link VirtualMachine} handles an {@link Instruction}. If execution of
		 * the {@link VirtualMachine} is paused or stopped this method is <b>not</b> guaranteed to
		 * be called before the next {@link DebugListener#beforeExecution(VirtualMachine)} is
		 * called. This method is guaranteed to be called even if the {@link VirtualMachine} gets a
		 * <b>non-critical</b> error during execution.
		 *
		 * @param vm the virtual machine
		 */
		void afterInstruction(VirtualMachine vm);

		/**
		 * Called after the state of the {@link VirtualMachine} changes.
		 *
		 * @param vm the virtual machine
		 */
		void onStateChanged(VirtualMachine vm);
	}
}
