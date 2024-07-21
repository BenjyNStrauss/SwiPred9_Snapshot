package dev.hssp;

import java.util.Deque;

/**
 * Code from buffer.h
 * @translator Benjamin Strauss
 *
 */

public class buffer<T, N> {

	public buffer() {}
	
	private buffer(buffer<T, N> buffer) { }
	
	private buffer<T, N> operator_eq(buffer<T, N> buffer) { 
		return buffer;
	}

	private Deque<T> m_queue;
	private boost.mutex m_mutex;
	private boost.condition m_empty, m_full;


	public void put(T inValue) {
		boost.mutex.scoped_lock lock(m_mutex);

		while (m_queue.size() >= N) {
			m_full.wait(lock);
		}

		m_queue.push_back(inValue);

		m_empty.notify_one();
	}
	
	public T get() {
		boost.mutex.scoped_lock lock(m_mutex);

		while (m_queue.empty()) {
			m_empty.wait(lock);
		}

		T result = m_queue.front();
		m_queue.pop_front();

		m_full.notify_one();

		return result;
	}
}
