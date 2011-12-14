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
		byte[] text = new byte[23];
		f.read(text, 0, 23);
		header = text.toString(); 
		if (header.equalsIgnoreCase(">>> Begin of header <<<"))
			return true;
		return false;
	}

	
}
