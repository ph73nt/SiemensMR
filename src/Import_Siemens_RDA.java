import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import rda.RDA;
import rda.RDADecoder;
import rda.RDAOpener;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.OpenDialog;

public class Import_Siemens_RDA extends ImagePlus implements ij.plugin.PlugIn {

	private BufferedInputStream inputStream;

	public Import_Siemens_RDA() {
	}

	public Import_Siemens_RDA(InputStream is) {
		this(new BufferedInputStream(is));
	}

	/** Constructs a DICOM reader that using an BufferredInputStream. */
	public Import_Siemens_RDA(BufferedInputStream bis) {
		inputStream = bis;
	}

	public void run(String arg) {

		if (arg.equalsIgnoreCase("about")) {
			showAbout();
			return;
		}

		OpenDialog od = new OpenDialog("Open RDA image file...", arg);
		String directory = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName == null) {
			return;
		}
		IJ.showStatus("Opening: " + directory + fileName);
		FileInfo fi = null;
		RDADecoder rdad = new RDADecoder(directory, fileName);
		try {
			rdad.getFileInfo();
		} catch (IOException e) {
			IJ.log("Error opening RDA file");
		}
	}

	public void showAbout() {
		IJ.showMessage(
				"About Import_Siemens_RDA",
				"Calls an instance of RDAOpener which will open an RDA format file. If the file format is not recognised, it will call open(), which will open other Image-J recognised files.");
		return;
	}

}
