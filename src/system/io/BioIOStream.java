package system.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import biology.BioObject;
import install.DirectoryManager;
import modules.UserInputModule;
import project.Project;
import utilities.LocalToolBase;

/**
 * BioIOStream is a class for handling object I/O
 * Use the static methods, don't try to make an instance of this class
 * 
 * @author Benjy Strauss
 *
 */

public final class BioIOStream extends LocalToolBase {
	public static final String SAVE_PATH = DirectoryManager.FILES_SAVED + "/";
	
	private BioIOStream() { }
	
	/**
	 * Saves multiple BioObject to separate files named by their impliedFileName()
	 * @param objects: the objects to save
	 */
	public static void saveObject(BioObject... objects) { 
		for(BioObject object: objects) {
			saveObject(object, object.saveString());
		}
	}
	
	/**
	 * Saves a BioObject to a files named by its impliedFileName()
	 * @param object: the object to save
	 */
	public static void saveObject(BioObject object) { saveObject(object, object.saveString()); }
	
	/**
	 * Saves a Serializable object to an object file in the files/saved/ directory
	 * @param object: the object to save
	 * @param fileName: what to name the object file
	 */
	public static void saveObject(Serializable object, String fileName) {
		FileOutputStream outFile = null;
		ObjectOutputStream outStream = null;
		
		try {
			outFile = new FileOutputStream(SAVE_PATH + fileName);
			outStream = new ObjectOutputStream(outFile);
			outStream.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outFile != null) {
				try {
					outFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Reads an object from a file
	 * Note that this method does not automatically check in files/saved/
	 * @param fileName: the name of the file to read
	 * @return: the object contained by the file
	 * @throws FileNotFoundRuntimeException: if the file name given as a parameter cannot be found
	 */
	public static Serializable readObject(String fileName) throws FileNotFoundException {
		Serializable retVal = null;
		FileInputStream fileInput = null;
		ObjectInputStream objectInput = null;
		
		File infile = new File(fileName);
		if(!infile.exists() || infile.isDirectory()) {
			error("Error: File does not exist!");
			return null;
		}
		
		fileInput = new FileInputStream(fileName);
		
		try {
			objectInput = new ObjectInputStream(fileInput);
			retVal = (Serializable) objectInput.readObject();
		} catch (InvalidClassException | ClassNotFoundException e) {
			error("Error: a classpath issue occured.");
			boolean useLegacy = UserInputModule.getBooleanFromUser("Use Legacy File Reader?");
			if(useLegacy) { return readLegacyObject(fileName); }
			boolean viewStackTrace = UserInputModule.getBooleanFromUser("View stack trace?");
			if(viewStackTrace) { e.printStackTrace(); }
		} catch (IOException e) {
			error("Error: an IOException occured.");
			boolean viewStackTrace = UserInputModule.getBooleanFromUser("View stack trace?");
			if(viewStackTrace) { e.printStackTrace(); }
		}  finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (objectInput != null) {
				try {
					objectInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return retVal;
	}
	
	public static Serializable readLegacyObject(String fileName) throws FileNotFoundException {
		Serializable retVal = null;
		FileInputStream fileInput = null;
		LegacyObjectInputStream objectInput = null;
		
		File infile = new File(fileName);
		if(!infile.exists() || infile.isDirectory()) {
			error("Error: File does not exist!");
			return null;
		}
		
		fileInput = new FileInputStream(fileName);
		
		try {
			objectInput = new LegacyObjectInputStream(fileInput);
			retVal = (Serializable) objectInput.readObject();
		} catch (InvalidClassException | ClassNotFoundException e) {
			error("Error: a Legacy Reader Failed.");
			e.printStackTrace();
		} catch (IOException e) {
			error("Error: an IOException occured.");
			boolean viewStackTrace = UserInputModule.getBooleanFromUser("View stack trace?");
			if(viewStackTrace) { e.printStackTrace(); }
		}finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (objectInput != null) {
				try {
					objectInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return retVal;
	}
	
	/**
	 * Saves a project to an object file in the files/saved/ directory
	 * The project will have a file name as given by it's saveName() method
	 * @param project: the project to save
	 */
	public static void saveProject(Project project) {
		saveObject(project, project.saveName());
	}
}
