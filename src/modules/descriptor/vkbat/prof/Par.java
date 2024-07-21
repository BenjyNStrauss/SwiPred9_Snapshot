package modules.descriptor.vkbat.prof;

import java.io.File;
import java.io.IOException;

import assist.translation.perl.PerlTranslator;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class Par extends PerlTranslator {
	public String dirHome;
	public String dirPhd;
	public String dirProf;
	public String confProf;
	public File packProf;
	
	public Par() { }
	
	public static boolean isSymlink(File file) throws IOException {
		  if (file == null)
			  throw new NullPointerException("File must not be null");
		  File canon;
		  if (file.getParent() == null) {
			  canon = file;
		  } else {
			  File canonDir = file.getParentFile().getCanonicalFile();
			  canon = new File(canonDir, file.getName());
		  }
		  return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}
}
