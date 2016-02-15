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

import java.io.File;
import java.util.ArrayList;

public class Folder {


	private File file;
	private ArrayList<Image> images;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public ArrayList<Image> getImages() {
		return images;
	}
	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

	
		
	
}
