package dev.hssp;

import java.nio.file.Path;

import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.ostream;

/**
 * Code from blast.h and blast.cpp
 * @translator Benjamin Strauss
 *
 */

public class blast {
	
	static final int kAACount = 22;  // 20 + B and Z
	static final int kResCount = 23;  // includes X
	static final int kBits = 5;
	static final int kThreshold = 11;
	static final int kUngappedDropOff = 7;
	static final int kGappedDropOff = 15;
	static final int kGappedDropOffFinal = 25;
	static final int kGapTrigger = 22;

	static final int kMaxSequenceLength = Integer.MAX_VALUE;

	static final int kHitWindow = 40;

	static final double kLn2 = Math.log(2.);

	static final short kSentinalScore = -9999;
	
	void SearchAndWriteResultsAsFastA(ostream inOutFile, Vector<Path> inDatabanks,
			  String inQuery, String inProgram, String inMatrix, int inWordSize, double inExpect,
			  boolean inFilter, boolean inGapped, int inGapOpen, int inGapExtend,
			  int inReportLimit, int inThreads) {
		if (inProgram != "blastp") {
			throw new mas_exception("Unsupported program "+inProgram);
		}
		
		if (inGapped) {
			if (inGapOpen == -1) { inGapOpen = 11; }
		    if (inGapExtend == -1) { inGapExtend = 1; }
		}

		if (inWordSize == 0) { inWordSize = 3; }

		String query = new String(inQuery);
		String queryID = new String("query");
		String queryDef;

		if (inQuery.startsWith(">")) {
			boost.smatch m;
			if (regex_search(inQuery, m, kFastARE, boost.match_not_dot_newline)) {
				queryID = m[4];
				if (queryID.empty()) {
					queryID = m[2];
				}
				queryDef = m[7];
				query = m.suffix();
			} else {
				queryID = inQuery.substr(1, inQuery.find('\n') - 1);
				query = inQuery.substring(queryID.length() + 2, String.npos);
		      
				String.size_type s = queryID.find(' ');
				if (s != String.npos) {
					queryDef = queryID.substr(s + 1);
					queryID.erase(s, String::npos);
				}
			}
		}

		long totalLength = accumulate(inDatabanks.begin(), inDatabanks.end(), 0LL,
				[](long l, Path p) -> long { return l + fs.file_size(p); });

		MProgress progress = new MProgress(totalLength, "blast");

		switch (inWordSize) {
		case 2:
		{
			BlastQuery<2> q(query, inFilter, inExpect, inMatrix, inGapped, inGapOpen,
					inGapExtend, inReportLimit);
			q.Search(inDatabanks, progress, inThreads);
			q.WriteAsFasta(inOutFile);
			break;
		}
		case 3:
		{
			BlastQuery<3> q(query, inFilter, inExpect, inMatrix, inGapped, inGapOpen,
					inGapExtend, inReportLimit);
			q.Search(inDatabanks, progress, inThreads);
			q.WriteAsFasta(inOutFile);
			break;
		}
		case 4:
		{
			BlastQuery<4> q(query, inFilter, inExpect, inMatrix, inGapped, inGapOpen,
					inGapExtend, inReportLimit);
			q.Search(inDatabanks, progress, inThreads);
			q.WriteAsFasta(inOutFile);
			break;
		}
		default:
			throw new mas_exception("Unsupported word size "+inWordSize);
		}	
	}
}
