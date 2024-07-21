package system;

import java.io.FileNotFoundException;

import assist.exceptions.NotYetImplementedError;
import assist.exceptions.UnmappedEnumValueException;
import assist.script.PythonScript;
import modules.descriptor.charge.Charge;
import modules.descriptor.entropy.Entropy;
import modules.descriptor.flex.Flex;
import modules.descriptor.isUnstruct.IsUnstructModule;
import modules.descriptor.vkbat.Vkbat;
import modules.encode.esm.FacebookESM;
import project.Project;
import project.ProteinDataset;
import project.SwiPredDataset;
import project.UnsupportedFileTypeException;
import system.io.BioIOStream;
import tools.DataSource;
import tools.writer.csv.AbstractDescriptorCSVWriter;
import tools.writer.csv.DynamicWriterFactory;

/**
 * Executes Instructions
 * TODO: add instructions
 * [1] set-save-interval
 * [2] print-dataset-as-json
 * [4] assign-vkbat-from-file
 * 
 * @author Benjamin Strauss
 *
 */

public class SwipredShell extends ProjectFilter {
	private DataSource fastaSource;
	private Project activeProject;
	private boolean autosave = true;
	private AbstractDescriptorCSVWriter writer;
	
	/**
	 * Create a SwiPred Shell to execute instructions
	 * @param defaultFastaSource: fasta source to start with
	 */
	public SwipredShell(DataSource defaultFastaSource) {
		fastaSource = defaultFastaSource;
	}
	
	public boolean execute(Instruction instr) {
		switch(instr.type) {
		//assign the amber95 descriptor
		case ASSIGN_CHARGE:
			Charge.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		//assign the entropy descriptor
		case ASSIGN_ENTROPY:
			Entropy.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		//assign an encoding from Facebook's ESM
		case ASSIGN_ESM:
			FacebookESM.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		//assign the flexibility descriptor
		case ASSIGN_FLEX:
			Flex.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		//assign the isunstruct descriptorå
		case ASSIGN_ISUNSTRUCT:
			IsUnstructModule.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		//assign one or more vkbat predictions
		case ASSIGN_VKBAT:
			Vkbat.assign(instr, applicableProt(activeProject, instr));
			return autosave;
		case NEW_DATA_SET:
			createDataset(instr);
			return autosave;
		case ERASE_DATA_SET:
			for(SwiPredDataset pp: applicable(activeProject, instr)) {
				pp.clear();
			}
			return autosave;
		case LOAD_PROJECT:
			try {
				activeProject = ProjectManageModule.loadProject(instr);
				return autosave;
			} catch (FileNotFoundException e) {
				error("Error: file \"" + e.getMessage() + "\" not found.");
				return false;
			} catch (UnsupportedFileTypeException e) {
				error("Error: file \"" + e.getMessage() + "\" was not a Swipred project file.");
				return false;
			}
		case NEW_PROJECT:
			activeProject = ProjectManageModule.newProject(instr);
			return autosave;
		case NONE:
			break;
		case PRINT_DATA_SET:
			PrintModule.printDataset(instr, applicableProt(activeProject, instr));
			break;
		case PRINT_FASTA_SOURCE:
			qp("Fasta Source is: " + fastaSource);
			return false;
		case PRINT_PROJECT_EMAIL:
			qp("Project email: " + activeProject.email());
			return false;
		case PRINT_PROJECT_NAME:
			qp("Project Name: " + activeProject.saveName());
			return false;
		case PRINT_VERSION:
			qp("Version: " + SwiPred.VERSION);
			return false;
		//exit the program
		case QUIT:
			qp(FINISHED);
			System.exit(0);
		case READ_CLUSTER_FILE:
			ClusteringSystem.readClusters(instr, applicableProt(activeProject, instr), fastaSource);
			return autosave;
		case SAVE_PROJECT:
			saveCurrentProject();
			return false;
		case SAVE_PROJECT_AS:
			ProjectManageModule.saveProjectAs(instr, activeProject);
			return false;
		//sets the fasta source
		case SET_FASTA_SRC:
			setFastaSrc(instr);
			return false;
		case SET_PROJECT_EMAIL:
			ProjectManageModule.setEmail(instr, activeProject);
			return autosave;
		//sets the Python Path
		case SET_PYTHON_PATH:
			if(instr.hasArgumentNamed("path")) {
				PythonScript.setPythonPath(instr.getFirstArgumentNamed("path"));
			} else {
				try {
					PythonScript.setPythonPath(instr.values().get(0));
				} catch (IndexOutOfBoundsException IOOBE) {
					error("Python path not specified!");
				}
			}
			return false;
		case WRITE_DATA:
			writer = DynamicWriterFactory.makeWriter(instr);
			this.writeData(instr);
			return false;
		//disables autosave
		case DISABLE_AUTO_SAVE:
			autosave = false;
			qp("SwiPred will no longer autosave when applicable.");
			return false;
		//enable autosave
		case ENABLE_AUTO_SAVE:
			autosave = true;
			qp("SwiPred will now autosave when applicable.");
			return false;
		default:
			qp("Unknown Instruction: ");
		}
		
		return false;
	}
	
	/**
	 * TODO creates a dataset from the instruction
	 * @param instr
	 */
	private void createDataset(Instruction instr) {
		SwiPredDataset newData;
		
		String name = instr.getFirstArgumentNamed(ParamTable.PROJECT_CODES_PLUS);
		if(name == null && instr.values().size() > 0) {
			name = instr.values().get(0);
		}
		
		FilterType type = FilterType.parse(instr.getFirstArgumentNamed("-t"));
		
		switch(type) {
		case PROTEIN:
			boolean multi = instr.hasArgumentNamed(ParamTable.MULTI);
			newData = new ProteinDataset(name, multi);
			break;
		default:
			throw new NotYetImplementedError("Only protein datasets are implemented thus far.");
		}
		
		activeProject.put(newData);
	}
	
	public void saveCurrentProject() {
		BioIOStream.saveProject(activeProject);
	}
	
	private void setFastaSrc(Instruction instr) {
		if(instr.values.size() > 0) {
			fastaSource = DataSource.parse(instr.values.get(0));
		} else if(instr.size() > 0) {
			for(String key: instr.keySet()) {
				try {
					fastaSource = DataSource.parse(key);
					return;
				} catch (UnmappedEnumValueException UEVE) { }
			}
		} else {
			error("No fasta source specified");
		}
	}
	
	public void setFastaSrc(DataSource fastaSource) {
		this.fastaSource = fastaSource;
	}
	
	public DataSource fastaSrc() { return fastaSource; }
	
	/**
	 * Warning: use SPARINGLY
	 * @return
	 */
	Project getProject() {
		return activeProject;
	}
	
	public void setProject(Project newProject) {
		activeProject = newProject;
	}
	
	public void setWriter(AbstractDescriptorCSVWriter writer) {
		this.writer = writer;
	}
	
	private void writeData(Instruction instr) {
		for(ProteinDataset set: applicableProt(activeProject, instr)) {
			writer.writeData(activeProject.saveName()+"-"+set.label()+".csv", set);
		}
	}
}
