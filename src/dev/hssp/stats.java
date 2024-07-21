package dev.hssp;

import assist.Deconstructable;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class stats implements Deconstructable {
	public long m_max, m_count, m_cumm;
	
	public stats() { }
	
	void operator(int i){
		if (m_max < i) {
	      m_max = i;
		}
	    ++m_count;
	    m_cumm += i;
	}

	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

/* FROM:

#ifndef NDEBUG
struct stats
{
  stats() : m_max(0), m_count(0), m_cumm(0) {}
  ~stats();

  void operator()(uint32 i)
  {
    if (m_max < i)
      m_max = i;
    ++m_count;
    m_cumm += i;
  }

  uint32 m_max, m_count, m_cumm;
};
#endif

*/