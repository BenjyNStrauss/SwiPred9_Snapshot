package modules.descriptor.vkbat;

import java.util.List;

import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import project.ProteinDataset;
import utilities.DataObject;
import utilities.exceptions.MissingFieldException;
import utilities.exceptions.ProteinParsingException;

/**
 * Allows the user to add vkbat data from a file
 * @author Benjy Strauss
 *
 */

public class VkbatAsgPatch extends DataObject {
	private static final long serialVersionUID = 1L;
	
	private final ChainID id;
	private final VKPred algorithm;
	private final String sequence;
	
	/**
	 * 
	 * @param protein
	 * @param algorithm
	 * @param sequence
	 * @throws ProteinParsingException
	 * @throws MissingFieldException
	 */
	public VkbatAsgPatch(String protein, String algorithm, String sequence) throws ProteinParsingException, MissingFieldException {
		id = new ChainID();
		if(protein.contains(":")) {
			String[] parts = protein.split(":");
			if(parts.length == 2) {
				id.setProtein(parts[0]);
				id.setChain(parts[1]);
			} else {
				throw new ProteinParsingException("Bad Protein: " + protein);
			}
		} else {
			id.setUniprot(protein);
		}
		
		if(algorithm == null) {
			throw new MissingFieldException("No vkbat algorithm specified!");
		}
		this.algorithm = VKPred.parse(algorithm);
		if(this.algorithm == VKPred.UNKNOWN) {
			throw new MissingFieldException("Vkbat prediction unknown.  (Check Spelling?)");
		}
		
		if(sequence == null) {
			throw new MissingFieldException("Error: a sequence must be specified for: " + id + " with " + this.algorithm);
		}
		
		this.sequence = sequence;
	}
	
	/**
	 * 
	 * @param learn
	 * @param test
	 * @throws VKAssignmentLengthException
	 */
	public void assign(List<ProteinDataset> projects) throws VKAssignmentLengthException {
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					if(chain.id().equals(id)) {
						chain.setVK(algorithm, sequence);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param learn
	 * @param test
	 * @throws VKAssignmentLengthException
	 */
	public void assign(ProteinDataset... projects) throws VKAssignmentLengthException {
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					if(chain.id().equals(id)) {
						chain.setVK(algorithm, sequence);
					}
				}
			}
		}
	}
	
	public String sequence() { return sequence; }
}
