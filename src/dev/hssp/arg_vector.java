package dev.hssp;

import assist.translation.cplusplus.Vector;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class arg_vector {
	
	Vector<String> m_args = new Vector<String>();
	private Vector<String> m_argv = new Vector<String>();
	
	public arg_vector(String program) {
		m_args.add(program);
	}

	public void push(String option){
		m_args.add(option);
	}

	//@Note: value was of template parameter type T
	void push(String option, Object value) {
		m_args.add(option);
		m_args.add(value.toString());
	}

	//operator char* const*();
	String operator_char() {
		m_argv.clear();
		for (String s: m_args) {
			m_argv.add(s);
			if (mas.VERBOSE > 1) {
				System.err.print(m_argv.get(m_argv.size()-1) + ' ');
			}
		}
		if (mas.VERBOSE > 1) {
			System.err.println();
		}

		m_argv.add(null);
		return m_argv.get(0);
	}

	//private friend std::ostream& operator<<(std::ostream& os, const arg_vector& argv);
}
