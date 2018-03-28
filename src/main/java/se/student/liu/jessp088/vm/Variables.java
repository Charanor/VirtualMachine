package se.student.liu.jessp088.vm;

import java.util.Arrays;

public class Variables {
	private final int[] variables;
	private int numVariables;

	public Variables(final int maxSize) {
		variables = new int[maxSize];
	}

	public void store(final int idx, final int val) {
		if (idx >= variables.length)
			throw new IndexOutOfBoundsException("Trying to access variable at index " + idx
					+ " but max index is " + variables.length);
		if (idx < 0) throw new IndexOutOfBoundsException("Index cannot be negative.");
		numVariables = Math.max(idx + 1, numVariables);
		variables[idx] = val;
	}

	public int load(final int idx) {
		if (idx >= variables.length)
			throw new IndexOutOfBoundsException("Trying to access variable at index " + idx
					+ " but max index is " + variables.length);
		if (idx < 0) throw new IndexOutOfBoundsException("Index cannot be negative.");
		return variables[idx];
	}

	public void clear() {
		numVariables = 0;
		Arrays.fill(variables, 0);
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.copyOf(variables, numVariables));
	}
}
