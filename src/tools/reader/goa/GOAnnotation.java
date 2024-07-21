package tools.reader.goa;

import assist.ActuallyCloneable;
import assist.Deconstructable;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class GOAnnotation extends DataObject implements ActuallyCloneable, Deconstructable {
	private static final long serialVersionUID = 1L;
	
	public final String DB;
	public final String DB_Object_ID;
	public final String DB_Object_Symbol;
	public final String Qualifier;
	public final String GO_ID;
	public final String DB_Reference;
	public final String Evidence_Code;
	public final String With_or_From;
	public final String Aspect;
	public final String DB_Object_Name;
	public final String DB_Object_Synonym;
	public final String DB_Object_Type;
	public final String Taxon_and_Interacting_taxon;
	public final String Date;
	public final String Assigned_By;
	public final String Annotation_Extension;
	public final String Gene_Product_Form_ID;
	
	public GOAnnotation(String gafLine) {
		String[] fields = gafLine.split("\t");
		DB							= fields[ 0];
		DB_Object_ID				= fields[ 1];
		DB_Object_Symbol			= fields[ 2];
		Qualifier					= fields[ 3];
		GO_ID						= fields[ 4];
		DB_Reference				= fields[ 5];
		Evidence_Code				= fields[ 6];
		With_or_From				= fields[ 7];
		Aspect						= fields[ 8];
		DB_Object_Name				= fields[ 9];
		DB_Object_Synonym			= fields[10];
		DB_Object_Type				= fields[11];
		Taxon_and_Interacting_taxon = fields[12];
		Date						= fields[13];
		Assigned_By					= fields[14];
		Annotation_Extension		= fields[15];
		Gene_Product_Form_ID		= fields[16];
	}
	
	private GOAnnotation(GOAnnotation cloneFrom) {
		DB							= cloneFrom.DB;
		DB_Object_ID				= cloneFrom.DB_Object_ID;
		DB_Object_Symbol			= cloneFrom.DB_Object_Symbol;
		Qualifier					= cloneFrom.Qualifier;
		GO_ID						= cloneFrom.GO_ID;
		DB_Reference				= cloneFrom.DB_Reference;
		Evidence_Code				= cloneFrom.Evidence_Code;
		With_or_From				= cloneFrom.With_or_From;
		Aspect						= cloneFrom.Aspect;
		DB_Object_Name				= cloneFrom.DB_Object_Name;
		DB_Object_Synonym			= cloneFrom.DB_Object_Synonym;
		DB_Object_Type				= cloneFrom.DB_Object_Type;
		Taxon_and_Interacting_taxon = cloneFrom.Taxon_and_Interacting_taxon;
		Date						= cloneFrom.Date;
		Assigned_By					= cloneFrom.Assigned_By;
		Annotation_Extension		= cloneFrom.Annotation_Extension;
		Gene_Product_Form_ID		= cloneFrom.Gene_Product_Form_ID;
	}

	public GOAnnotation clone() { return new GOAnnotation(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GOAnnotation) {
			GOAnnotation ogoa = (GOAnnotation) other;
			
			if(!DB.equals(ogoa.DB)) { return false; }
			if(!DB_Object_ID.equals(ogoa.DB_Object_ID)) { return false; }
			if(!DB_Object_Symbol.equals(ogoa.DB_Object_Symbol)) { return false; }
			if(!Qualifier.equals(ogoa.Qualifier)) { return false; }
			if(!GO_ID.equals(ogoa.GO_ID)) { return false; }
			if(!DB_Reference.equals(ogoa.DB_Reference)) { return false; }
			if(!Evidence_Code.equals(ogoa.Evidence_Code)) { return false; }
			if(!With_or_From.equals(ogoa.With_or_From)) { return false; }
			if(!Aspect.equals(ogoa.Aspect)) { return false; }
			if(!DB_Object_Name.equals(ogoa.DB_Object_Name)) { return false; }
			if(!DB_Object_Synonym.equals(ogoa.DB_Object_Synonym)) { return false; }
			if(!DB_Object_Type.equals(ogoa.DB_Object_Type)) { return false; }
			if(!Taxon_and_Interacting_taxon.equals(ogoa.Taxon_and_Interacting_taxon)) { return false; }
			if(!Date.equals(ogoa.Date)) { return false; }
			if(!Assigned_By.equals(ogoa.Assigned_By)) { return false; }
			if(!Annotation_Extension.equals(ogoa.Annotation_Extension)) { return false; }
			if(!Gene_Product_Form_ID.equals(ogoa.Gene_Product_Form_ID)) { return false; }
			
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return DB+"\t"+DB_Object_ID+"\t"+DB_Object_Symbol+"\t"+Qualifier+"\t"+GO_ID+"\t"+
				DB_Reference+"\t"+Evidence_Code+"\t"+With_or_From+"\t"+Aspect+"\t"+
				DB_Object_Name+"\t"+DB_Object_Synonym+"\t"+DB_Object_Type+"\t"+
				Taxon_and_Interacting_taxon+"\t"+Date+"\t"+Assigned_By+"\t"+
				Annotation_Extension+"\t"+Gene_Product_Form_ID;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
