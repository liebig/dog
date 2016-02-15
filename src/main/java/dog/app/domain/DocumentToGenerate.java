/*
 * Copyright (c) 2015 - 2016 Marc Liebig
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public 
 * License along with this program.  If not, see 
 * <http://www.gnu.org/licenses/>.
 * 
 */
package dog.app.domain;

import java.util.ArrayList;

public class DocumentToGenerate {

	private String outputPath;
	private String filename;
	private String outputType;
	private ArrayList<dog.app.domain.Image> images;

	public DocumentToGenerate() {
		
	}

	public DocumentToGenerate(String outputPath, String filename, String outputType,
			ArrayList<dog.app.domain.Image> images) {
		this.outputPath = outputPath;
		this.filename = filename;
		this.outputType = outputType;
		this.images = images;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public ArrayList<dog.app.domain.Image> getImages() {
		return images;
	}

	public void setImages(ArrayList<dog.app.domain.Image> images) {
		this.images = images;
	}

}
