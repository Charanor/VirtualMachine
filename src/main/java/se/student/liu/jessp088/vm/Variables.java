package se.student.liu.jessp088.vm;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This class holds key-value pairs of variables where both the keys and the values are integers.
 */
public class Variables implements Iterable<Integer>
{
	private final int[] variables;
	private int numVariables;

	public Variables(final int maxSize) {
		variables = new int[maxSize];
	}

	/**
	 * Creates a new Variables class that is an exact copy of the supplied Variables.
	 * @param other the Variables to copy.
	 */
	public Variables(final Variables other) {
		this.variables = new int[other.variables.length];
		System.arraycopy(other.variables, 0, variables, 0, variables.length);
		this.numVariables = other.numVariables;
	}

	/**
	 * Stores a variable in this Variables object.
	 * @param idx the variable's index
	 * @param val the value of the variable
	 */
	public void store(final int idx, final int val) {
		if (idx >= variables.length) throw new IndexOutOfBoundsException(
			"Trying to access variable at index " + idx + " but max index is " + variables.length);
		if (idx < 0) throw new IndexOutOfBoundsException("Index cannot be negative.");
		numVariables = Math.max(idx + 1, numVariables);
		variables[idx] = val;
	}

	/**
	 * Loads (gets) a variable from this Variables object.
	 * @param idx the variable's index
	 * @return the value of the variable
	 */
	public int load(final int idx) {
		if (idx >= variables.length) throw new IndexOutOfBoundsException(
			"Trying to access variable at index " + idx + " but max index is " + variables.length);
		if (idx < 0) throw new IndexOutOfBoundsException("Index cannot be negative.");
		return variables[idx];
	}

	/**
	 * Clears and removes all variables.
	 */
	public void clear() {
		numVariables = 0;
		Arrays.fill(variables, 0);
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOf(variables, numVariables));
	}

	@Override
	public Iterator<Integer> iterator() {
		return Arrays.stream(variables).iterator();
	}
}
