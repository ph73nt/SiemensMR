import java.io.BufferedInputStream;
import java.io.InputStream;

import rda.RDA;
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

		RDAOpener rdao = new RDAOpener();
			OpenDialog od = new OpenDialog("Open an RDA file...", "");
			String directory = od.getDirectory();
			String name = od.getFileName();
			if (name==null)
				return;
			String path = directory + name;
			IJ.showStatus("Opening: " + path);
			FileInfo fi = null;
			
			rdao.openRDA(directory, name).show();		
	}

	public void showAbout() {		
		IJ.showMessage(
				"About Import_Siemens_RDA",
				"Calls an instance of RDAOpener which will open an RDA format file. If the file format is not recognised, it will call open(), which will open other Image-J recognised files.");
		return;
	}

}
