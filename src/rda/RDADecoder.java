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

	private int imageOffset = 0;
	public boolean littleEndian = true;
	private int location = 0;
	public String header;
	public final static String headerBegin = ">>> Begin of header <<<";
	public final static String headerEnd = ">>> End of header <<<";
	public float[] Re, Im;
	public ImagePlus imp;

	private byte[] text;

	public RDADecoder(String directory, String fileName) {
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
			f = new BufferedInputStream(new FileInputStream(directory
					+ fileName));
		}
		if (IJ.debugMode) {
			IJ.log("");
			IJ.log("RDADecoder: decoding " + fileName);
		}

		if (!isRDA()) {
			IJ.log("Not an RDA file");
			return fi;
		}

		// Now have header information
		imp.setProperty("Info", header);

		location = imageOffset;

		fillMatrices();

		// Set some default values for testing
		// fi.width = xdim;
		// fi.height = ydim;
		// fi = setFrames(fi);
		// fi.pixelDepth = slice_t;
		// fi.fileType = bitDepth;
		// fi.frameInterval = frameTime;
		// fi.intelByteOrder = false; // Big endian on Sun Solaris
		// fi.offset = imageOffset;
		// fi = parseADACExtras(fi);

		return fi;
	}

	public void fillMatrices() {
		// Data is double-complex 8bytes Re, 8bytes Im
		int len = 2048 * 2;
		Re = new float[len];
		Im = new float[len];

		try {
			for (int i = 0; i < len; i++) {
				Re[i] = getFloat();
				Im[i] = getFloat();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isRDA() throws IOException {
		// Files begin with >>> Begin of header <<<
		int len = headerBegin.length();
		text = new byte[len];
		f.read(text, 0, len);
		header = new String(text);
		if (!header.equalsIgnoreCase(headerBegin))
			return false;

		imageOffset = 23;
		// and ends with ">>> End of header <<<"
		text = new byte[1];
		f.read(text, 0, 1);
		imageOffset++;

		while ((text[0] != 3) && (text[0] != 4)) {
			header += new String(text);
			String end = header.substring(header.length() - headerEnd.length());
			if (end.equalsIgnoreCase(headerEnd)) {
				return true;
			}
			f.read(text, 0, 1);
			imageOffset++;
		}

		return false;
	}

	byte getByte() throws IOException {
		f.read(text, 0, 1);
		byte b = text[0];
		++location;
		return b;
	}

	float getFloat() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int res = 0;
		if (littleEndian) {
			res += b0;
			res += (((long) b1) << 8);
			res += (((long) b2) << 16);
			res += (((long) b3) << 24);
		} else {
			res += b3;
			res += (((long) b2) << 8);
			res += (((long) b1) << 16);
			res += (((long) b0) << 24);
		}
		return Float.intBitsToFloat(res);
	}

}
