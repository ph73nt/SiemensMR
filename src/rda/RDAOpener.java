package rda;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;



/**
 * Imports a Siemens *.rda sprectroscopy file. Borrows heavily from the
 * Interfile Image-J plug-ins.
 * 
 * @author Neil Thomson
 * 
 */
public class RDAOpener extends ij.io.Opener {

	private static final int BYTES_TO_CHECK = 64;

	public RDAOpener() {
		return;
	}

	/**
	 * Opens the RDA file as an Image.
	 * @param directory The directory to the file.
	 * @param name The filename.
	 * @return The image object as an ImagePlus
	 */
	public ImagePlus openImage(String directory, String name){
		if (directory.length() > 0 && !directory.endsWith(Prefs.separator))
			directory += Prefs.separator;
		String path = directory + name;
		try {
			if (isRDA(path, name)) {
				RDA imp = new RDA();
				imp.run(path);
				return (ImagePlus) imp;
			} else
				return super.openImage(directory, name);
		} catch (IOException e) {
			IJ.error("IOException: " + e.getMessage());
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Opens the RDA file as an Image.
	 * @param path The path to the file.
	 * @return The image object as an ImagePlus
	 */
	public ImagePlus openImage(String path) {
		try {
			if(isRDA(path, getName(path))) {
				RDA imp = new RDA ();
				imp.run(path);
				return (ImagePlus)imp;
			} else
				return super.openImage(getDir(path), getName(path));
		} catch (IOException e) {
			IJ.error("IOException: "+e.getMessage());
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Confirms that a file looks like an RDA file. The header must begin with ">>> Begin of header <<<".
	 * @param path The path to the file.
	 * @param name The filename.
	 * @return True for an RDA file, false otherwise.
	 * @throws IOException
	 */
	boolean isRDA(String path, String name) throws IOException {
		FileReader in;
		in = new FileReader(path);
		char[] cbuf = new char[BYTES_TO_CHECK];
		if (in.read(cbuf, 0, BYTES_TO_CHECK) < 0) {
			throw new EOFException("RDAOpener: <" + BYTES_TO_CHECK + " bytes in path");
		}
		
		String s = new String(cbuf).toLowerCase();
		if (s.indexOf(RDA.HDR_START) >= 0){
			return true;
		}
		return false;
		
	}

	/**
	 * Opens the RDA file as an Image.
	 * @param directory The directory to the file.
	 * @param name The filename.
	 * @return The image object as an ImagePlus
	 */
	public ImagePlus openRDA(String directory, String name) {
		if (!directory.endsWith(Prefs.separator))
			directory += Prefs.separator;
		String path = directory+name;
		RDA imp = new RDA();
		imp.run(path);
		return (ImagePlus)imp;
	}

}
