package rda;

import ij.IJ;
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

	private String header;
	
	public RDADecoder(String directory, String fileName){
		this.directory = directory;
		this.fileName = fileName;
	}
	
	FileInfo getFileInfo() throws IOException {
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

	    // Copy header into a byte array for parsing forwards and backwards
	    f.read(bytHeader, 0, imageOffset);
	    header = getHeader();

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

	  String getHeader() throws IOException {
		    String hdr;
		    //////////////////////////////////////////////////////////////
		    // Administrative header info
		    //////////////////////////////////////////////////////////////

		    // First 10 bytes reserved for preamble
		    hdr = getString(6) + "\n";
		    IJ.log(hdr);                 // says adac01
		    try {
		      short labels = getShort();
		      IJ.log(Integer.toString(labels)); // Number of labels in header
		      IJ.log(Integer.toString(getByte()));  // Number of sub-headers
		      IJ.log(Integer.toString(getByte()));  // Unused byte

		      offset = location;

		      IJ.log("location = " + location);

		      // For each header field available.. get them
		      values.setSize(ADACDictionary.noKeys + 1);

		      for (short i = 0; i < labels; i++) {
		        // Attempt to find the next key...
		        //   ...the keynum (description)
		        //   ...the offset to the value

		        getKeys();
		        // Remember how far through the list of headers we have got
		        offset = location;
		        //IJ.log("location[" + i + "] = " + location);
		        location = fieldOffset;
		        //IJ.log("location[" + i + "] = " + location);
		        switch (datTyp) {
		          case aByte:
		            // Differentiate between byte proper and a string
		            //  (ADAC header does not)
		            if (dict.type[keynum] == aString) {
		              switch (keynum) {
		                case 114:
		                  AD_ex_objs = getString(dict.valLength[keynum]);
		                  values.setElementAt(AD_ex_objs, keynum);
		                  break;
		                case 17:
		                  AD_Type = getStringLessNull(dict.valLength[keynum]);
		                  values.setElementAt(AD_Type, keynum);
		                  break;
		                default:
		                  values.setElementAt(
		                          getStringLessNull(dict.valLength[keynum]), keynum);
		                  break;
		              }
		            } else {
		              values.setElementAt((byte) getByte(), keynum);
		            }
		            break;
		          case aShort:
		            short shortValue = (short) getShort();
		            switch (keynum) {
		              case 39: // X-dimension
		                xdim = shortValue;
		                break;
		              case 40: //Y-dimension
		                ydim = shortValue;
		                break;
		              case 41: //Z dimension
		                zdim = shortValue;
		                break;
		              case 42: // Pixel depth
		                switch (shortValue) {
		                  case 8:
		                    bitDepth = FileInfo.GRAY8;
		                    break;
		                  case 16:
		                    bitDepth = FileInfo.GRAY16_SIGNED;
		                    break;
		                  case 32:
		                    bitDepth = FileInfo.GRAY32_FLOAT;
		                    break;
		                  default:
		                    bitDepth = FileInfo.GRAY16_UNSIGNED;
		                }
		                ;
		                break;
		              case 86:
		                noSets = shortValue;
		                IJ.log("" + noSets);
		                break;
		              case 61:
		                intervals = shortValue;
		                IJ.log("" + intervals);
		                break;
		            }
		            values.setElementAt(shortValue, keynum);
		            break;
		          case anInt:
		            int m_Int = getInt();
		            switch (keynum) {
		              case 46:
		                // Time oper frame
		                frameTime = ((double) m_Int) / 1000d;
		                break;
		            }
		            values.setElementAt(m_Int, keynum);
		            break;
		          case aFloat:
		            float floatValue = getFloat();
		            switch (keynum) {
		              case 38: // Slice thickness
		                slice_t = floatValue;
		            }
		            values.setElementAt(floatValue, keynum);
		            break;
		        }
		        hdr += dict.descriptions[keynum] + " = "
		                + values.elementAt(keynum) + "\n";

		        location = offset;
		      }
		      IJ.log("" + values.size());

		      /////////////// Get ready for the next code:
		      getKeys();
		      IJ.log(Integer.toString(keynum));

		      return hdr;
		    } catch (IOException e) {
		      IJ.error("Failed to retrieve ADAC image file header. "
		              + "Is this an ADAC image file?");
		      return null;
		    }
		  }


	
}
