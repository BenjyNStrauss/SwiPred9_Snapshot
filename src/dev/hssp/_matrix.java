package dev.hssp;

import assist.Deconstructable;
import assist.translation.cplusplus.std;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class _matrix<T> extends matrix_base<T> implements Deconstructable {

	private Object[] m_data;
	private int m_m, m_n;
	
	public _matrix(matrix_base<T> m) {
		m_m = (m.dim_m());
		m_n = (m.dim_n());
		m_data = new Object[m_m * m_n];
		for (int i = 0; i < m_m; ++i) {
			for (int j = 0; j < m_n; ++j) {
				operator_parens(i, j) = m(i, j);
			}
		}
	}

	public _matrix() {
		m_data = (null);
		m_m = (0);
		m_n =(0);
	}

	public _matrix(_matrix<T> m) {
		m_m = (m.m_m);
		m_n = (m.m_n);
            	
		m_data = new Object[m_m * m_n];
		std.copy(m.m_data, m.m_data + (m_m * m_n), m_data);
	}
	
	public _matrix<T> operator_eq(_matrix<T> m) {
		Object[] t = new Object[m.m_m * m.m_n];
		std.copy(m.m_data, m.m_data + (m.m_m * m.m_n), t);

		m_data = null;
		m_data = t;
		m_m = m.m_m;
		m_n = m.m_n;

		return this;
	}
	
	public _matrix(int m, int n) {
		this(m, n, T());
	}

	public _matrix(int m, int n, T v) {
		m_m = (m);
		m_n = (n);
		m_data = new Object[m_m * m_n];
		std.fill(m_data, m_data + (m_m * m_n), v);
	}

	public void deconstruct() {
		m_data = null;
	}

	public int dim_m() { return m_m; }
	public int dim_n() { return m_n; }

  	public T operator_parens(int i, int j) {
  		assert(i < m_m); assert(j < m_n);
  		return m_data[i * m_n + j];
  	}

  	//template<typename Func>
  	public void each(Func f) {
  		for (int i = 0; i < m_m * m_n; ++i) {
  			f(m_data[i]);
  		}
  	}

  	//template<typename U>
  	public _matrix<T> operator_div_eq(U v) {
  		for (int i = 0; i < m_m * m_n; ++i) {
  			m_data[i] /= v;
  		}

  		return this;
  	}
};