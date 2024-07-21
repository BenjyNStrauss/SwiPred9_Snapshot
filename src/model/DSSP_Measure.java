package model;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import assist.util.Pair;
import biology.amino.InsertCode;
import utilities.DataObject;

/**
 * A measurement of an Amino Acid Residue found in a DSSP File
 * See: https://pdb-redo.eu/dssp/about
 * 
 * skips chainID
 * Still under development
 * 
 * TODO: NOTE: fix all TODOs before using
 * 
 * @author Benjamin Strauss
 * 
 */

public class DSSP_Measure extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final int EXPECTED_DSSP_LINE_LEN = 136;
	
	private static final DecimalFormat DF1 = new DecimalFormat("0.0");
	private static final DecimalFormat DF3 = new DecimalFormat("0.000");
	
	private static final StringBuilder BUILDER = new StringBuilder(); 
	
	public final String raw_input;
	//sequential residue number, including chain breaks as extra residues
	public final int sequential;
	//original resname, not necessarily sequential, may contain letters for insertion codes
	public final InsertCode insCode;
	
	public final char chain;
	
	//amino acid sequence in one letter code
	public final char amino_type;
	//secondary structure summary based on columns 19-38
	public final char secondary_structure;
	//PPII (kappa) helix
	public final char PPII;
	//3-10 helix
	public final char _3_10_helix;
	//alpha helix
	public final char alpha_helix;
	//pi helix
	public final char pi_helix;
	//geometrical bend
	public final char geometrical_bend;
	//chirality
	public final char chirality;
	//beta bridge label
	public final String beta_bridge_label;
	//beta bridge partner resnum
	public final int beta_bridge_partner_resnum_1;
	public final int beta_bridge_partner_resnum_2;
	//beta sheet label
	public final char beta_sheet_label;
	//solvent accessibility
	public final int solvent_accessibility;
	
	//first pair of columns
	private final Pair<Integer, Double> NH_O_1;
	private final Pair<Integer, Double> O_HN_1;
	
	//second pair of columns
	private final Pair<Integer, Double> NH_O_2;
	private final Pair<Integer, Double> O_HN_2;
	
	public final double tco;
	public final double kappa;
	public final double alpha;
	public final double phi;
	public final double psi;
	public final double x_ca;
	public final double y_ca;
	public final double z_ca;
	
	/**
	 * 
	 * @param sequential
	 * @param insCode
	 * @param chain
	 * @param amino_type
	 * @param secondary_structure
	 * @param PPII
	 * @param _3_10_helix
	 * @param alpha_helix
	 * @param pi_helix
	 * @param geometrical_bend
	 * @param chirality
	 * @param beta_bridge_label
	 * @param beta_bridge_partner_resnum_1
	 * @param beta_bridge_partner_resnum_2
	 * @param beta_sheet_label
	 * @param solvent_accessibility
	 * @param NH_O_1
	 * @param O_HN_1
	 * @param NH_O_2
	 * @param O_HN_2
	 * @param tco
	 * @param kappa
	 * @param alpha
	 * @param phi
	 * @param psi
	 * @param x_ca
	 * @param y_ca
	 * @param z_ca
	 */
	public DSSP_Measure(int sequential, InsertCode insCode, char chain, char amino_type, char secondary_structure, 
			char PPII, char _3_10_helix, char alpha_helix, char pi_helix, char geometrical_bend,
			char chirality, String beta_bridge_label, int beta_bridge_partner_resnum_1, int beta_bridge_partner_resnum_2,
			char beta_sheet_label, int solvent_accessibility, Pair<Integer, Double> NH_O_1,
			Pair<Integer, Double> O_HN_1, Pair<Integer, Double> NH_O_2, Pair<Integer, Double> O_HN_2,
			double tco, double kappa, double alpha, double phi, double psi, double x_ca, double y_ca,
			double z_ca) {
		
		raw_input = createRawLine(sequential, insCode, chain, amino_type, secondary_structure, PPII, _3_10_helix,
				alpha_helix, pi_helix, geometrical_bend, chirality, beta_bridge_label, beta_bridge_partner_resnum_1,
				beta_bridge_partner_resnum_2, beta_sheet_label, solvent_accessibility, NH_O_1,
				O_HN_1, NH_O_2, O_HN_2, tco, kappa, alpha, phi, psi, x_ca, y_ca, z_ca);
		
		this.sequential = sequential;
		this.insCode = insCode.clone();
		this.chain = chain;
		this.amino_type = amino_type;
		this.secondary_structure = secondary_structure;
		this.PPII = PPII;
		this._3_10_helix = _3_10_helix;
		this.alpha_helix = alpha_helix;
		this.pi_helix = pi_helix;
		this.geometrical_bend = geometrical_bend;
		this.chirality = chirality;
		this.beta_bridge_label = beta_bridge_label;
		this.beta_bridge_partner_resnum_1 = beta_bridge_partner_resnum_1;
		this.beta_bridge_partner_resnum_2 = beta_bridge_partner_resnum_2;
		this.beta_sheet_label = beta_sheet_label;
		this.solvent_accessibility = solvent_accessibility;
		this.NH_O_1 = NH_O_1.clone();
		this.O_HN_1 = O_HN_1.clone();
		this.NH_O_2 = NH_O_2.clone();
		this.O_HN_2 = O_HN_2.clone();
		this.tco = tco;
		this.kappa = kappa;	
		this.alpha = alpha; 
		this.phi = phi;
		this.psi = psi;
		this.x_ca = x_ca;
		this.y_ca = y_ca;
		this.z_ca = z_ca;
	}
	
	/**
	 * 
	 * @param sequential
	 * @param insCode
	 * @param chain
	 * @param amino_type
	 * @param secondary_structure
	 * @param PPII
	 * @param _3_10_helix
	 * @param alpha_helix
	 * @param pi_helix
	 * @param geometrical_bend
	 * @param chirality
	 * @param beta_bridge_label
	 * @param beta_bridge_partner_resnum_1
	 * @param beta_bridge_partner_resnum_2
	 * @param beta_sheet_label
	 * @param solvent_accessibility
	 * @param NH_O_1_1
	 * @param NH_O_1_2
	 * @param O_HN_1_1
	 * @param O_HN_1_2
	 * @param NH_O_2_1
	 * @param NH_O_2_2
	 * @param O_HN_2_1
	 * @param O_HN_2_2
	 * @param tco
	 * @param kappa
	 * @param alpha
	 * @param phi
	 * @param psi
	 * @param x_ca
	 * @param y_ca
	 * @param z_ca
	 */
	public DSSP_Measure(int sequential, InsertCode insCode, char chain, char amino_type, char secondary_structure, 
			char PPII, char _3_10_helix, char alpha_helix, char pi_helix, char geometrical_bend,
			char chirality, String beta_bridge_label, int beta_bridge_partner_resnum_1, int beta_bridge_partner_resnum_2,
			char beta_sheet_label, int solvent_accessibility, int NH_O_1_1, double NH_O_1_2,
			int O_HN_1_1, double O_HN_1_2, int NH_O_2_1, double NH_O_2_2, int O_HN_2_1, double O_HN_2_2,
			double tco, double kappa, double alpha, double phi, double psi, double x_ca, double y_ca,
			double z_ca) {
		
		this(sequential, insCode, chain, amino_type, secondary_structure, PPII, _3_10_helix,
				alpha_helix, pi_helix, geometrical_bend, chirality, beta_bridge_label, beta_bridge_partner_resnum_1,
				beta_bridge_partner_resnum_2, beta_sheet_label, solvent_accessibility, new Pair<Integer, Double>(NH_O_1_1, NH_O_1_2),
				new Pair<Integer, Double>(O_HN_1_1, O_HN_1_2), new Pair<Integer, Double>(NH_O_2_1, NH_O_2_2),
				new Pair<Integer, Double>(O_HN_2_1, O_HN_2_2), tco, kappa, alpha, phi, psi, x_ca, y_ca, z_ca);
	}

	/**
	 * 
	 * @param dsspLine
	 * @throws DSSPBorderException
	 */
	public DSSP_Measure(String dsspLine) throws DSSPBorderException {
		raw_input = dsspLine;
		sequential = Integer.parseInt(dsspLine.substring(0,5).trim());
		
		String ins_code_segment = dsspLine.substring(6,11).trim();
		if(ins_code_segment.length() == 0) {
			throw new DSSPBorderException("Invalid DSSP Line");
		}
		
		insCode = new InsertCode(ins_code_segment);
		chain = dsspLine.charAt(11);
		amino_type = dsspLine.charAt(13);
		secondary_structure = dsspLine.charAt(16);
		
		PPII = dsspLine.charAt(17);
		_3_10_helix = dsspLine.charAt(18);
		alpha_helix = dsspLine.charAt(19);
		pi_helix = dsspLine.charAt(20);
		geometrical_bend = dsspLine.charAt(21);
		chirality = dsspLine.charAt(22);
		
		beta_bridge_label = dsspLine.substring(23,25).trim();
		
		beta_bridge_partner_resnum_1 = Integer.parseInt(dsspLine.substring(26,29).trim());
		beta_bridge_partner_resnum_2 = Integer.parseInt(dsspLine.substring(30,33).trim());
		beta_sheet_label = dsspLine.charAt(33);
		solvent_accessibility = Integer.parseInt(dsspLine.substring(35,38).trim());
		
		NH_O_1 = new Pair<Integer, Double>(
				Integer.parseInt(dsspLine.substring(41,45).trim()),
				getDouble(dsspLine.substring(46,50).trim()));
		O_HN_1 = new Pair<Integer, Double>(
				Integer.parseInt(dsspLine.substring(53,56).trim()),
				getDouble(dsspLine.substring(57,61).trim()));
		
		NH_O_2 = new Pair<Integer, Double>(
				Integer.parseInt(dsspLine.substring(64,67).trim()),
				getDouble(dsspLine.substring(68,72).trim()));
		O_HN_2 = new Pair<Integer, Double>(
				Integer.parseInt(dsspLine.substring(75,78).trim()),
				getDouble(dsspLine.substring(79,83).trim()));
		
		tco   = getDouble(dsspLine.substring(85,91).trim());
		kappa = getDouble(dsspLine.substring(91,97).trim());
		alpha = getDouble(dsspLine.substring(97,103).trim());
		phi   = getDouble(dsspLine.substring(103,109).trim());
		psi   = getDouble(dsspLine.substring(109,115).trim());
		x_ca  = getDouble(dsspLine.substring(116,122).trim());
		y_ca  = getDouble(dsspLine.substring(122,129).trim());
		z_ca  = getDouble(dsspLine.substring(129,136).trim());
	}
	
	/**
	 * 
	 * @param cloneFrom
	 */
	private DSSP_Measure(DSSP_Measure cloneFrom) {
		raw_input = cloneFrom.raw_input;
		sequential = cloneFrom.sequential;
		
		insCode = cloneFrom.insCode.clone();
		chain = cloneFrom.chain;
		amino_type = cloneFrom.amino_type;
		secondary_structure = cloneFrom.secondary_structure;
		
		PPII = cloneFrom.PPII;
		_3_10_helix = cloneFrom._3_10_helix;
		alpha_helix = cloneFrom.alpha_helix;
		pi_helix = cloneFrom.pi_helix;
		geometrical_bend = cloneFrom.geometrical_bend;
		chirality = cloneFrom.chirality;
		
		beta_bridge_label = cloneFrom.beta_bridge_label;
		
		beta_bridge_partner_resnum_1 = cloneFrom.beta_bridge_partner_resnum_1;
		beta_bridge_partner_resnum_2 = cloneFrom.beta_bridge_partner_resnum_2;
		beta_sheet_label = cloneFrom.beta_sheet_label;
		solvent_accessibility = cloneFrom.solvent_accessibility;
		
		NH_O_1 = cloneFrom.NH_O_1.clone();
		O_HN_1 = cloneFrom.O_HN_1.clone();
		NH_O_2 = cloneFrom.NH_O_2.clone();
		O_HN_2 = cloneFrom.O_HN_2.clone();
		
		tco   = cloneFrom.tco;
		kappa = cloneFrom.kappa;
		alpha = cloneFrom.alpha;
		phi   = cloneFrom.phi;
		psi   = cloneFrom.psi;
		x_ca  = cloneFrom.x_ca;
		y_ca  = cloneFrom.y_ca;
		z_ca  = cloneFrom.z_ca;
	}
	
	public final Pair<Integer, Double> NH_to_O_1() { return NH_O_1.clone(); }
	public final Pair<Integer, Double> O_to_NH_1() { return O_HN_1.clone(); }
	public final Pair<Integer, Double> NH_to_O_2() { return NH_O_2.clone(); }
	public final Pair<Integer, Double> O_to_NH_2() { return O_HN_2.clone(); }
	
	public DSSP_Measure clone() { return new DSSP_Measure(this); }
	
	public int hashCode() { return raw_input.hashCode(); }
	
	public boolean equals(Object other) {
		return (other instanceof DSSP_Measure) ? toString().equals(other.toString()) : false;
	}
	
	public String toString() { return raw_input; }
	
	public static void main(String[] args) throws Exception {
		//              0....;....1....;....2....;....3....;....4....;....5....;....6....;....7....;....8....;....9....;....A....;....B....;....C....;....D....;....E
		String line =  "   93   94 A G        -     0   0    0    176,-2.5     2,-0.4    -2,-0.3   142,-0.2  -0.540  21.2-127.6-105.8-179.9   52.8   47.3   62.8";
		qp(line);
		DSSP_Measure test = new DSSP_Measure(line);
		qp(test.raw_input);
		qp(line.equals(test.raw_input));
		
		/*qp("----------------------");
		String line2 = "    2    2 A T  E     -a   34   0A  66    -31,-2.0    33,-2.1     1,-0.1     2,-0.7  -0.456 360.0-169.6 -87.8 130.5    7.7    8.8   59.8";
		DSSP_Measure test2 = new DSSP_Measure(line2);
		test2.printFields();*/
	}
	
	public static boolean isChain(char chain, String line) {
		return chain == line.charAt(11);
	}
	
	public void printFields() {
		qp("Sequential:                "+sequential);
		qp("Insertion Code:            "+insCode);
		qp("Amino Acid Char:           "+amino_type);
		qp("Secondary Structure Char:  "+secondary_structure);
		qp("PPII:                      "+PPII);
		qp("3-10 Helix:                "+_3_10_helix);
		qp("Alpha Helix:               "+alpha_helix);
		qp("PI Helix:                  "+pi_helix);
		qp("Geometrical Bend:          "+geometrical_bend);
		qp("Chirality:                 "+chirality);
		qp("Beta Bridge Label:         "+beta_bridge_label);
		qp("β-Bridge Partner ResNo #1: "+beta_bridge_partner_resnum_1);
		qp("β-Bridge Partner ResNo #2: "+beta_bridge_partner_resnum_2);
		qp("Beta Sheet Label:          "+beta_sheet_label);
		qp("Solvent Accessibility:     "+solvent_accessibility);
		qp("N-H-->O #1:                "+NH_O_1);
		qp("O-->H-N #1:                "+O_HN_1);
		qp("N-H-->O #2:                "+NH_O_2);
		qp("O-->H-N #2:                "+O_HN_2);
		qp("TCO:                       "+lineup(tco));
		qp("KAPPA:                     "+lineup(kappa));
		qp("ALPHA:                     "+lineup(alpha));
		qp("PHI:                       "+lineup(phi));
		qp("PSI:                       "+lineup(psi));
		qp("X-CA:                      "+lineup(x_ca));
		qp("Y-CA:                      "+lineup(y_ca));
		qp("Z-CA:                      "+lineup(z_ca));
	}
	
	/**
	 * 
	 * @param sequential
	 * @param insCode
	 * @param chain
	 * @param amino_type
	 * @param secondary_structure
	 * @param PPII
	 * @param _3_10_helix
	 * @param alpha_helix
	 * @param pi_helix
	 * @param geometrical_bend
	 * @param chirality
	 * @param beta_bridge_label
	 * @param beta_bridge_partner_resnum_1
	 * @param beta_bridge_partner_resnum_2
	 * @param beta_sheet_label
	 * @param solvent_accessibility
	 * @param NH_O_1
	 * @param O_HN_1
	 * @param NH_O_2
	 * @param O_HN_2
	 * @param tco
	 * @param kappa
	 * @param alpha
	 * @param phi
	 * @param psi
	 * @param x_ca
	 * @param y_ca
	 * @param z_ca
	 * @return
	 */
	private static String createRawLine(int sequential, InsertCode insCode, char chain, char amino_type, char secondary_structure,
			char PPII, char _3_10_helix, char alpha_helix, char pi_helix, char geometrical_bend, char chirality,
			String beta_bridge_label, int beta_bridge_partner_resnum_1, int beta_bridge_partner_resnum_2,
			char beta_sheet_label, int solvent_accessibility, Pair<Integer, Double> NH_O_1,
			Pair<Integer, Double> O_HN_1, Pair<Integer, Double> NH_O_2, Pair<Integer, Double> O_HN_2, double tco,
			double kappa, double alpha, double phi, double psi, double x_ca, double y_ca, double z_ca) {
		
		final char[] lineChars = new char[EXPECTED_DSSP_LINE_LEN];
		for(int index = 0; index < EXPECTED_DSSP_LINE_LEN; ++index) {
			lineChars[index] = ' ';
		}
		
		lineChars[45] = lineChars[56] = lineChars[67] = lineChars[78] = ',';
		
		String tmp = ""+sequential;
		insertValue(lineChars, 5-tmp.length(), tmp);
		
		tmp = ""+insCode.index;
		insertValue(lineChars, 10-tmp.length(), tmp);
		
		if(insCode.code != '*') {
			tmp = ""+insCode.code;
			insertValue(lineChars, 11-tmp.length(), tmp);
		}
		
		lineChars[11] = chain;
		lineChars[13] = amino_type;
		lineChars[16] = secondary_structure;
		lineChars[17] = PPII;
		lineChars[18] = _3_10_helix;
		lineChars[19] = alpha_helix;
		lineChars[20] = pi_helix;
		lineChars[21] = geometrical_bend;
		lineChars[22] = chirality;
		insertValue(lineChars, 23, beta_bridge_label);
		
		tmp = ""+beta_bridge_partner_resnum_1;
		insertValue(lineChars, 29-tmp.length(), tmp);
		
		tmp = ""+beta_bridge_partner_resnum_2;
		insertValue(lineChars, 33-tmp.length(), tmp);
		
		lineChars[33] = beta_sheet_label;
		
		tmp = ""+solvent_accessibility;
		insertValue(lineChars, 38-tmp.length(), tmp);
		
		tmp = ""+NH_O_1.x;
		insertValue(lineChars, 45-tmp.length(), tmp);
		tmp = ""+DF1.format(NH_O_1.y);
		insertValue(lineChars, 50-tmp.length(), tmp);
		
		tmp = ""+O_HN_1.x;
		insertValue(lineChars, 56-tmp.length(), tmp);
		tmp = ""+DF1.format(O_HN_1.y);
		insertValue(lineChars, 61-tmp.length(), tmp);
		
		tmp = ""+NH_O_2.x;
		insertValue(lineChars, 67-tmp.length(), tmp);
		tmp = ""+DF1.format(NH_O_2.y);
		insertValue(lineChars, 72-tmp.length(), tmp);
		
		tmp = ""+O_HN_2.x;
		insertValue(lineChars, 78-tmp.length(), tmp);
		tmp = ""+DF1.format(O_HN_2.y);
		insertValue(lineChars, 83-tmp.length(), tmp);
		
		tmp = ""+DF3.format(tco);
		insertValue(lineChars, 91-tmp.length(), tmp);
		
		tmp = ""+DF1.format(kappa);
		insertValue(lineChars, 97-tmp.length(), tmp);
		
		tmp = ""+DF1.format(alpha);
		insertValue(lineChars, 103-tmp.length(), tmp);
		
		tmp = ""+DF1.format(phi);
		insertValue(lineChars, 109-tmp.length(), tmp);
		
		tmp = ""+DF1.format(psi);
		insertValue(lineChars, 115-tmp.length(), tmp);
		
		tmp = ""+DF1.format(x_ca);
		insertValue(lineChars, 122-tmp.length(), tmp);
		
		tmp = ""+DF1.format(y_ca);
		insertValue(lineChars, 129-tmp.length(), tmp);
		
		tmp = ""+DF1.format(z_ca);
		insertValue(lineChars, 136-tmp.length(), tmp);
		return new String(lineChars);
	}
	
	/**
	 * 
	 * @param buffer
	 * @param offset
	 * @param insert
	 */
	private static void insertValue(char[] buffer, int offset, String insert) {
		char[] insertChars = insert.toCharArray();
		System.arraycopy(insertChars, 0, buffer, offset, insertChars.length);
	}
	
	private static double getDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException NFE) {
			return Double.NaN;
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private static String lineup(double value) {
		BUILDER.setLength(0);
		if(value > 0)   { BUILDER.append(" "); }
		if(Math.abs(value) < 100) { BUILDER.append(" "); }
		if(Math.abs(value) < 10)  { BUILDER.append(" "); }
		BUILDER.append(value);
		
		return BUILDER.toString();
	}
}
