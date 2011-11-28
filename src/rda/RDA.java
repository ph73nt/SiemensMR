package rda;

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;

/**
 * This ImageJ plugin, which was modified from Interfile_TP (available from the
 * ImageJ plugins page of the website), decodes Siemens *.rda MRI spectroscopy
 * files. If the input <code>arg</code> is empty, it displays a file open dialog
 * and opens and displays the image selected by the user. If <code>arg</code> is
 * a path, it opens the specified image and the calling routine can display it
 * using <code>((ImagePlus)IJ.runPlugIn("ij.plugin.RDA", path)).show()</code>.
 * 
 * @author Neil Thomson
 * 
 */
public class RDA extends ImagePlus {

	public static final String HDR_START = ">>> Begin of header <<<";
	public static final String HDR_END = ">>> End of header <<<";

	public RDA() {
		super();
	}

	public void run(String arg) {
		OpenDialog od = new OpenDialog("Open Interfile...", arg);
		String directory = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName==null)
			return;
		IJ.showStatus("Opening: " + directory + fileName);
		RDADecoder rdad = new RDADecoder(directory, fileName);
	}
}
