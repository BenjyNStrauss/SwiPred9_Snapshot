package dev.hssp;

import java.util.ArrayList;

/**
 * converted from "typedef std::basic_string<uint8> sequence;"
 * @translator Benjamin Strauss
 *
 */

public class Sequence extends ArrayList<Character> {
	private static final long serialVersionUID = 1L;

	public Sequence(int length, int initialValue) {
		super();
		for(int ii = 0; ii < length; ++ii) {
			add((char) initialValue);
		}
	}

	public int length() { return size(); }
}
