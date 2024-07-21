package model;

import assist.ActuallyCloneable;
import assist.numerical.DecimalCoordinate;
import assist.util.Pair;
import biology.amino.InsertCode;
import biology.amino.SecondaryStructure;
import biology.molecule.MoleculeLookup;
import biology.molecule.types.AminoType;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class DSSP_Entry extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	public final InsertCode insCode;
	
	//amino acid sequence in one letter code
	public final AminoType amino_type;
	//secondary structure summary based on columns 19-38
	public final SecondaryStructure secondary_structure;
	//PPII (kappa) helix
	public final char PPII;
	//3-10 helix
	public final char _3_10_helix;
	//alpha helix
	public final char alpha_helix;
	//pi helix
	public final char pi_helix;
	//geometrical bend
	public final boolean geometrical_bend;
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
	
	public final DecimalCoordinate ɑCarbonLoc;
	
	public DSSP_Entry(DSSP_Measure measure) {
		insCode = measure.insCode.clone();
		
		char tmp = Character.isUpperCase(measure.amino_type) ? measure.amino_type : 'C';
		this.amino_type = MoleculeLookup.parseAmino(tmp);
		this.secondary_structure = SecondaryStructure.parse(measure.secondary_structure);
		this.PPII = measure.PPII;
		this._3_10_helix = measure._3_10_helix;
		this.alpha_helix = measure.alpha_helix;
		this.pi_helix = measure.pi_helix;
		this.geometrical_bend = measure.geometrical_bend == 'S';
		this.chirality = measure.chirality;
		this.beta_bridge_label = measure.beta_bridge_label;
		this.beta_bridge_partner_resnum_1 = measure.beta_bridge_partner_resnum_1;
		this.beta_bridge_partner_resnum_2 = measure.beta_bridge_partner_resnum_2;
		this.beta_sheet_label = measure.beta_sheet_label;
		this.solvent_accessibility = measure.solvent_accessibility;
		this.NH_O_1 = measure.NH_to_O_1();
		this.O_HN_1 = measure.O_to_NH_1();
		this.NH_O_2 = measure.NH_to_O_2();
		this.O_HN_2 = measure.NH_to_O_2();
		this.tco = measure.tco;
		this.kappa = measure.kappa;	
		this.alpha = measure.alpha; 
		this.phi = measure.phi;
		this.psi = measure.psi;
		
		this.ɑCarbonLoc = new DecimalCoordinate(measure.x_ca, measure.y_ca, measure.z_ca);		
	}
	
	private DSSP_Entry(DSSP_Entry entry) {
		insCode = entry.insCode.clone();
		
		this.amino_type = entry.amino_type;
		this.secondary_structure = entry.secondary_structure;
		this.PPII = entry.PPII;
		this._3_10_helix = entry._3_10_helix;
		this.alpha_helix = entry.alpha_helix;
		this.pi_helix = entry.pi_helix;
		this.geometrical_bend = entry.geometrical_bend;
		this.chirality = entry.chirality;
		this.beta_bridge_label = entry.beta_bridge_label;
		this.beta_bridge_partner_resnum_1 = entry.beta_bridge_partner_resnum_1;
		this.beta_bridge_partner_resnum_2 = entry.beta_bridge_partner_resnum_2;
		this.beta_sheet_label = entry.beta_sheet_label;
		this.solvent_accessibility = entry.solvent_accessibility;
		this.NH_O_1 = entry.NH_O_1.clone();
		this.O_HN_1 = entry.O_HN_1.clone();
		this.NH_O_2 = entry.NH_O_2.clone();
		this.O_HN_2 = entry.O_HN_2.clone();
		this.tco = entry.tco;
		this.kappa = entry.kappa;	
		this.alpha = entry.alpha; 
		this.phi = entry.phi;
		this.psi = entry.psi;
		
		this.ɑCarbonLoc = entry.ɑCarbonLoc.clone();	
	}
	
	public DSSP_Entry clone() { return new DSSP_Entry(this); }
}
