package dev.control.experiments;

//import assist.base.Assist;
import assist.util.LabeledList;
import modules.descriptor.vkbat.control.LocalVK;
import utilities.LocalToolBase;

/**
 * Used to Test Vkbat algorithm runtime
 * @author Benjy Strauss
 *
 */

public class RuntimeTester extends LocalToolBase {
	private static final int MAX = 3000000;
	private static final int MAX_TRIALS = 8;
	private static final int START_SIZE = 8;
	
	public static void main(String[] args) throws Exception {
		LabeledList<String> fileLines = new LabeledList<String>();
		fileLines.add("DATA,try1,try2,try3,try4,try5,try6,try7,try8,avg");
		
		testAlgorithm(generateSequence(40));
		
		long[][] data = runTests();
		/*for(long[] l: data) {
			Assist.qpln(l);
		}*/
		//System.exit(0);
		
		StringBuilder builder = new StringBuilder();
		for(int size = START_SIZE, row = 0; size <= MAX; size*=2, ++row) {
			builder.setLength(0);
			builder.append("Size = "+ size + ",");
			
			for(int ii = 0; ii <= MAX_TRIALS; ++ii) {
				builder.append(data[ii][row] + ",");
			}
			trimLastChar(builder);
			
			fileLines.add(builder.toString());
		}
		
		writeFileLines("runtimes/cf2.csv", fileLines);
	}
	
	public static long[][] runTests() throws Exception {
		//try 1, try 2, ..., try 8, average
		long[][] data = new long[MAX_TRIALS+1][29];
		
		for(int size = START_SIZE, row = 0; size <= MAX; size*=2, ++row) {
			long average = 0;
			for(int trial = 0; trial < MAX_TRIALS; ++trial) {
				data[trial][row] = testAlgorithm(generateSequence(size));
				average += data[trial][row];
			}
			data[MAX_TRIALS][row] = (average/MAX_TRIALS);
		}
		
		return data;
	}
	
	
	private static long testAlgorithm(String seq) throws Exception {
		long time = System.currentTimeMillis();
		//TODO
		//LocalVK.runPsiPred_Single(seq);
		//LocalVK.runJNet(seq);
		LocalVK.runChouFasman(seq, true);
		//LocalVK.runGor4(seq);
		//LocalVK.runDSC(seq);
		
		long elapsed = System.currentTimeMillis() - time;
		qp("Runtime for [" + seq.length() + "] = " + elapsed);
		return elapsed;
	}
	
	private static String generateSequence(int length) {
		char[] randomSeq = new char[length];
		for(int ii = 0; ii < length; ++ii) {
			randomSeq[ii] = getRandomAmino();
			for(int jj = 0; jj < 3; ++jj) {
				if(isNonStandard(randomSeq[ii])) {
					randomSeq[ii] = getRandomAmino();
				} else {
					break;
				}
			}
		}
		
		return new String(randomSeq);
	}
	
	private static boolean isNonStandard(char c) {
		return (c == 'B') || (c == 'J') || (c == 'X') || (c == 'Z') || (c == 'O') || (c == 'U');
	}

	private static char getRandomAmino() {
		double rand = Math.random() * 26;
		rand += 65;
		return (char) rand;
	}
	
}
