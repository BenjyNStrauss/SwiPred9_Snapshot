package utilities;
import assist.base.ToolBeltLimited;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class SwiPredThread extends Thread implements ToolBeltLimited {
	//thread's system assigned id (not java id!)
	private final int idNo;
	
	public SwiPredThread(int idNo) {
		this.idNo = idNo;
	}
	
	/** @return: thread's system ID (not java id!) */
	public int idNo() { return idNo; }
	
	public static void qp(Object arg) { LocalToolBase.qp(arg); }
	public static void qp() { LocalToolBase.qp(); }
	public static void qerr(Object arg) { LocalToolBase.qerr(arg); }
}
