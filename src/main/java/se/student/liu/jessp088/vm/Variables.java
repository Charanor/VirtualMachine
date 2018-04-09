package se.student.liu.jessp088.vm;

import java.util.Arrays;
import java.util.Iterator;

public class Variables implements Iterable<Integer> {
	private final int[] variables;
	private int numVariables;

	public Variables(final int maxSize) {
		variables = new int[maxSize];
	}

	public Variables(final Variables other) {
		this.variables = new int[other.variables.length];
		System.arraycopy(other.variables, 0, variables, 0, variables.length);
		this.numVariables = other.numVariables;
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

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int idx = 0;

			@Override
			public boolean hasNext() {
				return idx < variables.length;
			}

			@Override
			public Integer next() {
				return variables[idx++];
			}
		};
	}
}
