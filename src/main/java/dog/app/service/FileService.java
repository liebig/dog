/*
 * Copyright (c) 2015 Marc Liebig
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
package dog.app.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import dog.app.domain.Image;

public class FileService {

	private static ArrayList<String> extensions = new ArrayList<String>();
	
	public static void setExtensions(ArrayList<String> extensions) {
		FileService.extensions = extensions;
	}
	
	public static ArrayList<String> getExtensions() {
		return FileService.extensions;
	}
	
	public static void addExtension(String extension) {
		FileService.extensions.add(extension);
	}
	
	public static void removeExtension(String extension) {
		FileService.extensions.remove(extension);
	}
	
	public static File[] getFilesFromFolder(File folder) {
		
		// Get all files from folder
		File[] fileList = folder.listFiles();
		
		// Sort files array
		Arrays.sort(fileList);
		
		return fileList;
	}
	
	public static File[] getFilesFromFolderByExtension(File folder, String extension) {
		ArrayList<String> extensions = new ArrayList<String>();
		extensions.add(extension);
		return FileService.getFilesFromFolderByExtensions(folder, extensions);
	}
	
	public static File[] getFilesFromFolderByExtensions(File folder, final ArrayList<String> extensions) {
		
		File[] files = folder.listFiles(new FilenameFilter() { 
	         public boolean accept(File dir, String filename) {
	        	 for(String extension : extensions) {
	        		 if (filename.toLowerCase().endsWith("." + extension.toLowerCase())) {
	        			 return true;
	        		 }
	        	 }
	        	 return false;
        	 }
		});
		
		// Sort files array
		Arrays.sort(files);
		
		return files;
	}
	
	public static File[] getFilesFromFolderByExtensions(File folder) {
		return FileService.getFilesFromFolderByExtensions(folder, FileService.extensions);
	}
	
	public static ArrayList<Image> getImagesFromFiles(File[] files) {
		
		ArrayList<Image> imageArrayList = new ArrayList<Image>();
		
		for(File file: files) {
			imageArrayList.add(new Image(file));
		}
		
		return imageArrayList;
		
	}
	
}
