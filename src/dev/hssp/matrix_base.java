package dev.hssp;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class matrix_base<T> {
	public T value_type;

	public matrix_base() {}

	public int dim_m() { return 0; }
	public int dim_n() { return 0; }

	/*public T operator_parens(int i, int j){
		throw new RuntimeException("unimplemented method");
	}*/

	public T operator_parens(int i, int j) { return null; }

	public matrix_base<T> operator_times_eq(T rhs) {
		for (int i = 0; i < dim_m(); ++i) {
			for (int j = 0; j < dim_n(); ++j) {
				operator_parens(i, j) *= rhs;
			}
		}
		return this;
	}

	public matrix_base operator_minus_eq(T rhs) {
		for (int i = 0; i < dim_m(); ++i) {
			for (int j = 0; j < dim_n(); ++j) {
				operator_parens(i, j) -= rhs;
		    }
		}

		return this;
	}
};

