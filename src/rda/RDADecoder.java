package rda;

import java.io.IOException;

import ij.IJ;

public class RDADecoder {
	
	private String directory, fileName;
	
	public RDADecoder(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
		IJ.register(RDA.class);
		IJ.register(RDADecoder.class);
		return;
	}
	
	public void getHeaderInfo() throws IOException {
		
	}

}
