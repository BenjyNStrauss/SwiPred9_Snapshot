package dev.hssp;

import assist.translation.cplusplus.CppException;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class mas_exception extends CppException {
	private static final long serialVersionUID = 1L;
	
	private char[] m_msg = new char[1024];
	
	public mas_exception(String msg) {
		super(msg);
	}
	//public mas_exception(boost::format msg);

	public char[] what() { return m_msg; }
}

/* FROM:

class mas_exception : public std::exception
{
  public:
          mas_exception(const std::string& msg);
          mas_exception(const boost::format& msg);

  virtual const char*
          what() const throw()  { return m_msg; }

  private:
  char      m_msg[1024];
};

*/