package tools.reader.fasta.exceptions;

import utilities.LocalToolBase;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * When a line in a PDB file fails a Sanity Check
 * @author Benjamin Strauss
 *
 */

public class PDBSanityCheckException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public final String filePath;
	public final String line;
	private final String[] fields;
	
	public PDBSanityCheckException(String filePath, String line, String[] fields, int fieldNo) {
		super(formMessage(filePath, line, fields, fieldNo));
		this.filePath = filePath;
		this.line = line;
		this.fields = fields;
	}
	
	private static String formMessage(String filePath, String line, String[] fields, int fieldNo) {
		StringBuilder builder = new StringBuilder();
		builder.append("Sanity Check failed on line: fieldNo="+fieldNo +"\n");
		builder.append(">\t\""+ line +"\"\n");
		builder.append(">\t of PDB file: "+ filePath +"\n");
		builder.append(">\tFields [");
		for(int ii = 0; ii < fields.length; ++ii) {
			if(ii == fieldNo) {
				builder.append("-->\""+fields[ii]+"\", ");
			} else {
				builder.append("\""+fields[ii]+"\", ");
			}
		}
		LocalToolBase.trimLastChar(builder);
		LocalToolBase.trimLastChar(builder);
		builder.append("]");
		return builder.toString();
	}

	public String getField(int fieldNo) { return fields[fieldNo]; }
	
	public int numFields() { return fields.length; }
}
