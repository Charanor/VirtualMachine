package se.student.liu.jessp088.vm;

/** The types of state a {@link VirtualMachine} can be in at one specific time.
 *
 * @author Charanor */
public enum VMState {
	/** The virtual machine is currently executing code normally. */
	EXECUTING(true, false, false),
	/** The virtual machine is currently debugging code. */
	DEBUGGING(true, false, false),
	/** The code executed and ended successfully. */
	END_SUCCESS(false, false, true),
	/** The code executed and ended in an error. */
	END_ERROR(false, false, true),
	/** The execution was stopped by a user. */
	END_USER(false, false, true),
	/** The virtual machine hit a breakpoint and paused execution. */
	PAUSE_BREAKPOINT(false, true, false),
	/** The execution was paused by a user. */
	PAUSE_USER(false, true, false);

	/**Is the VirtualMachine running while in this state?*/
	public final boolean running;
	/**Is the VirtualMachine paused while in this state?*/
	public final boolean paused;
	/**Is the VirtualMachine stopped while in this state?*/
	public final boolean stopped;

	private VMState(final boolean running, final boolean paused, final boolean stopped) {
		this.running = running;
		this.paused = paused;
		this.stopped = stopped;
	}
}
