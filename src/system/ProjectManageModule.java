package system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import modules.UserInputModule;
import project.Project;
import project.ProteinDataset;
import project.SwiPredDataset;
import project.UnsupportedFileTypeException;
import system.io.BioIOStream;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public final class ProjectManageModule extends SysTools {
	private static final String[] MULTI_SET = { "-m", "-multi", "-multiset", "-multi-set" };
	private static final String[] PROJECT_TYPE = { "-project-type", "-type" };
	private static final String UNTITLED_PROJECT = "Untitled Protein Project";
	
	private ProjectManageModule() { }
	
	/**
	 * Create a new project
	 * @param instr
	 * @return
	 */
	public static Project newProject(Instruction instr) {
		String nameArg = instr.getFirstArgumentNamed(true, NAME_ARG);
		String projectName = (nameArg != null) ? nameArg : UNTITLED_PROJECT;
		boolean flag = (nameArg != null);
		
		if(projectName.equals(UNTITLED_PROJECT) && instr.values().size() > 0) {
			projectName = instr.values.get(0);
		}
		
		String email = instr.getFirstArgumentNamed(true, EMAIL_ARG);
		
		if(email == null) {
			if(flag && instr.values.size() > 0) {
				email = instr.values.get(0);
			} else if(instr.values.size() > 1) {
				email = instr.values.get(1);
			}
		}
		
		return new Project(projectName, email);
	}
	
	public static SwiPredDataset newDataset(Instruction instr) {
		String nameArg = instr.getFirstArgumentNamed(true, NAME_ARG);
		boolean isMulti = instr.hasArgumentNamed(MULTI_SET);
		
		String projectName = (nameArg != null) ? nameArg : UNTITLED_PROJECT;
		
		if(projectName.equals(UNTITLED_PROJECT) && instr.values().size() > 0) {
			projectName = instr.values.get(0);
		}
		
		FilterType projectType = FilterType.parse(instr.getFirstArgumentNamed(PROJECT_TYPE));
		
		switch(projectType) {
		case PROTEIN:			return new ProteinDataset(projectName, isMulti);
		default:				throw new SwiPredRuntimeException("Unknown Project Type");
		}
	}

	/**
	 * Load a project from the system
	 * @param instr
	 * @return
	 * @throws FileNotFoundException 
	 * @throws UnsupportedFileTypeException 
	 */
	public static Project loadProject(Instruction instr) throws FileNotFoundException, UnsupportedFileTypeException {
		String filename = instr.getFirstArgumentNamed(true, FILE_ARG);
		Serializable raw_loaded = null;
		
		if(filename == null && instr.values.size() > 0) {
			filename = instr.values.get(0);
		}
		
		boolean fullPath = false;
		if(instr.getArgumentNamed(true, FULL_PATH_ARG) != null) {
			fullPath = instr.getFirstArgumentNamed(true, FULL_PATH_ARG).equalsIgnoreCase("true");
		}
		
		if(fullPath) {
			File projectFile = new File(filename);
			if(!projectFile.exists() || projectFile.isDirectory()) {
				throw new FileNotFoundException(filename);
			} else {
				raw_loaded = BioIOStream.readObject(filename);
			}
		} else {
			if(!filename.startsWith(BioIOStream.SAVE_PATH)) {
				filename = BioIOStream.SAVE_PATH + filename;
			}
			File projectFile = new File(filename);
			if(!projectFile.exists() || projectFile.isDirectory()) {
				throw new FileNotFoundException(filename);
			} else {
				raw_loaded = BioIOStream.readObject(filename);
			}
		}

		if(raw_loaded instanceof Project) {
			return (Project) raw_loaded;
		} else {
			throw new project.UnsupportedFileTypeException(filename);
		}
	}
	
	/**
	 * Saves the project as something else
	 * @param instr: instruction containing what to save the project as
	 * @param project
	 */
	public static void saveProjectAs(Instruction instr, Project project) {
		String newName = instr.getFirstArgumentNamed(true, NAME_ARG);
		
		if(newName == null && instr.values().size() > 0) {
			newName = instr.values.get(0);
		}
		
		if(newName == null) {
			newName = UserInputModule.getStringFromUser("Enter name to save as: ");
		}
		
		if(newName.contains(":")) {
			error("Error: invalid save name: cannot contain the character ':'");
		} else if(newName.length() == 0) {
			error("Error: name cannot be zero length!");
		} else {
			project.setSaveName(newName);
			BioIOStream.saveProject(project);
		}
	}
	
	/**
	 * 
	 * @param instr
	 * @param activeProject
	 */
	public static void setEmail(Instruction instr, Project activeProject) {
		String emailArg = instr.getFirstArgumentNamed(true, EMAIL_ARG);
		if(instr.values().size() > 0) {
			emailArg = instr.values.get(0);
		}
		if(emailArg == null) {
			error("Warning: setting email for \"" + activeProject.toString() + "\" to null!");
		}
		activeProject.setEmail(emailArg);
	}
}
