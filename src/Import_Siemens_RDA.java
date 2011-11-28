import rda.RDA;
import rda.RDAOpener;
import ij.IJ;
import ij.io.OpenDialog;

public class Import_Siemens_RDA implements ij.plugin.PlugIn {
	
	public void run(String arg) {

		if (arg.equalsIgnoreCase("about")) {
			showAbout();
			return;
		}

		RDAOpener rdao = new RDAOpener();
		if(arg.toLowerCase().indexOf(RDA.HDR_START) >= 0) {
			OpenDialog od = new OpenDialog("Open an RDA file...", "");
			String directory = od.getDirectory();
			String name = od.getFileName();
			if (name==null)
				return;
			String path = directory + name;
			IJ.showStatus("Opening: " + path);
			rdao.openRDA(directory, name).show();
		} else
		{
			// Just call the normal IJ open routine
			rdao.open();
		}
		
	}

	public void showAbout() {		
		IJ.showMessage(
				"About Import_Siemens_RDA",
				"Calls an instance of RDAOpener which will open an RDA format file. If the file format is not recognised, it will call open(), which will open other Image-J recognised files.");
		return;
	}

}
