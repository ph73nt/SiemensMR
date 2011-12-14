package rda;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class RDADecoder {
	
	private String directory, fileName;
	
	BufferedInputStream inputStream, f;

	private byte[] bytHeader;

	private int imageOffset;

	public String header;
	public final static String headerBegin = ">>> Begin of header <<<";
	public final static String headerEnd = ">>> End of header <<<";
	
	public ImagePlus imp;
	
	public RDADecoder(String directory, String fileName){
		this.directory = directory;
		this.fileName = fileName;
		// create dummy image
		imp = IJ.createImage("Untitled", "8-bit White", 400, 400, 3);
		imp.show();
	}
	
	public FileInfo getFileInfo() throws IOException {
	    FileInfo fi = new FileInfo();
	    fi.fileFormat = FileInfo.RAW;
	    fi.fileName = fileName;
	    if (directory.indexOf("://") > 0) { // is URL
	      URL u = new URL(directory + fileName);
	      inputStream = new BufferedInputStream(u.openStream());
	      fi.inputStream = inputStream;
	    } else if (inputStream != null) {
	      fi.inputStream = inputStream;
	    } else {
	      fi.directory = directory;
	    }

	    if (inputStream != null) {
	      f = inputStream;
	    } else {
	      f = new BufferedInputStream(new FileInputStream(directory + fileName));
	    }
	    if (IJ.debugMode) {
	      IJ.log("");
	      IJ.log("RDADecoder: decoding " + fileName);
	    }
	    
	    if (!isRDA()){
	    	IJ.log("Not an RDA file");
	    	return fi;
	    }
	    
	    imp.setProperty("Info", header);

	    // Set some default values for testing
//	    fi.width = xdim;
//	    fi.height = ydim;
//	    fi = setFrames(fi);
//	    fi.pixelDepth = slice_t;
//	    fi.fileType = bitDepth;
//	    fi.frameInterval = frameTime;
//	    fi.intelByteOrder = false;            // Big endian on Sun Solaris
//	    fi.offset = imageOffset;
//	    fi = parseADACExtras(fi);

	    return fi;
	  }

	public boolean isRDA() throws IOException {
		// Files begin with >>> Begin of header <<<
		int location = headerBegin.length();
		byte[] text = new byte[location];
		f.read(text, 0, location);
		header = new String(text); 		
		if (!header.equalsIgnoreCase(headerBegin))
			return false;
		
		// and ends with ">>> End of header <<<"
		text = new byte[1];
		f.read(text, 0, 1);

		while ((text[0] != 3) && (text[0] != 4)) {
			header += new String(text);
			String end = header.substring(header.length() - headerEnd.length());
			if (end.equalsIgnoreCase(headerEnd)){
				return true;
			}
			f.read(text, 0, 1);
		}
		
		return false;
	}

	
}
