package project;

import java.util.Objects;

import assist.util.LabeledHash;

/**
 * A Project stores a user's data sets, each identified by a string.
 * @author Benjamin Strauss
 *
 */

public class Project extends LabeledHash<String, SwiPredDataset> {
	private static final long serialVersionUID = 1L;
	
	//the user's email
	protected String email;
	
	/**
	 * Construct a new project
	 * @param saveName: what to save the project as (cannot be null)!
	 * @param email: the user's email (can be null)
	 */
	public Project(String saveName, String email) {
		Objects.requireNonNull(saveName, "Save name cannot be null.");
		
		label = saveName;
		this.email = email;
	}
	
	public void put(SwiPredDataset newDataset) {
		super.put(newDataset.label(), newDataset);
	}
	
	/** @param: what the project will be saved as */
	public void setSaveName(String saveName) { this.label = saveName; }
	
	/** @return: what the project will be saved as */
	public String saveName() { return label; }
	
	/** @param: what the project will be saved as */
	public void setEmail(String email) { this.email = email; }
	
	/** @return: project's email as on file */
	public String email() { return email; }
	
	public String toString() {
		return (email != null) ? label + " (" + email + ")" : label;
	}
}
