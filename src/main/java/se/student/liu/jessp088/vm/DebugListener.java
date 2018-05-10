package se.student.liu.jessp088.vm;

/**
 * A listener that listens for events in a {@link VirtualMachine}.
 */
public interface DebugListener
{
	/**
	 * Called before {@link VirtualMachine#execute(Bytecode)} or
	 * {@link VirtualMachine#debug(Bytecode)} is called but after state has been set to
	 * {@link VMState#EXECUTING} or {@link VMState#DEBUGGING}.
	 *
	 * @param vm the virtual machine
	 */
	// Unused. Here for the sake of completeness.
	void beforeExecution(VirtualMachine vm);

	/**
	 * Called after {@link VirtualMachine#execute(Bytecode)} or
	 * {@link VirtualMachine#debug(Bytecode)} is called and after state has been set to
	 * {@link VMState#END_SUCCESS}, {@link VMState#END_ERROR}, or {@link VMState#END_USER}.
	 *
	 * @param vm the virtual machine
	 */
	// Unused. Here for the sake of completeness.
	void afterExecution(VirtualMachine vm);

	/**
	 * Called before the virtual machine handles an instruction. Calling
	 * {@link VirtualMachine#stopExecution()} in this method is guaranteed to stop the execution of
	 * the virtual machine before the instruction is handled.
	 *
	 * @param vm the virtual machine
	 */
	void beforeInstruction(VirtualMachine vm);

	/**
	 * Called after the virtual machine handles an instruction. If execution of the virtual machine
	 * is paused or stopped this method is <b>not</b> guaranteed to be called before the next
	 * {@link DebugListener#beforeExecution(VirtualMachine)} is called. This method is guaranteed to
	 * be called even if the virtual machine gets a <b>non-critical</b> error during execution.
	 *
	 * @param vm the virtual machine
	 */
	void afterInstruction(VirtualMachine vm);

	/**
	 * Called after the state of the virtual machine changes.
	 *
	 * @param vm the virtual machine
	 *
	 * @see VirtualMachine#getState()
	 * @see VirtualMachine#getPreviousState()
	 */
	void onStateChanged(VirtualMachine vm);
}
