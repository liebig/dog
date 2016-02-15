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
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import dog.app.service.FileService;

public class FolderTree {

	private ArrayList<Folder> folders;
	private String name;
	private JTree folderTree;
	private DefaultTreeModel treeModel;
	
	final static Logger logger = Logger.getLogger(FolderTree.class);

	public FolderTree(String name, JTree folderTree) {

		this.name = name;

		this.treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(
				this.name) {
			private static final long serialVersionUID = 1L;
		}, false);

		folderTree.setModel(this.treeModel);
		this.folderTree = folderTree;
		this.folders = new ArrayList<Folder>();
	}

	public void addFolder(Folder folder) {

		for (Folder currentFolder : folders) {
			if (currentFolder.getFile().getAbsolutePath()
					.equals(folder.getFile().getAbsolutePath())) {
				JOptionPane.showMessageDialog(null, "The selected folder '"
						+ currentFolder.getFile().getName()
						+ "' was already added.", "Folder was already added",
						JOptionPane.ERROR_MESSAGE);
				logger.info("Folder to add was already added");
				return;
			}
		}

		this.folders.add(folder);
		this.refresh();
	}

	public ArrayList<Folder> getFolders() {
		return this.folders;
	}

	public void removeFolder(File deleteFolder) {
		logger.debug("Removing folders");
		for (Folder folder : folders) {
			if (folder.getFile().getAbsolutePath()
					.equals(deleteFolder.getAbsolutePath())) {
				logger.debug("Removing folder: " + folder.getFile().getAbsolutePath());
				this.folders.remove(folder);
				this.refresh();
				return;
			}
		}
	}

	public void removeAllFolders() {
		logger.debug("Remove all folders");
		this.folders.clear();
		this.refresh();
	}


	public void refresh() {

		logger.debug("Refreshing folders");
		
		this.sortFolders();

		DefaultTreeModel newTreeModel = new DefaultTreeModel(

		new DefaultMutableTreeNode(name) {
			private static final long serialVersionUID = 1L;

			{
				for (Folder folder : folders) {

					TreeDirectoryNode node = new TreeDirectoryNode(
							folder.getFile());

					for (Image image : folder.getImages()) {
						node.add(new TreeImageNode(image, folder));
					}

					add(node);
				}
			}
		}, false);

		this.treeModel = newTreeModel;
		folderTree.setModel(this.treeModel);
	}

	public void reloadFolders() {
		logger.debug("Reloading folders");
		for (Folder folder : folders) {
			File[] reloadedImages = FileService
					.getFilesFromFolderByExtensions(folder.getFile());
			folder.setImages(FileService.getImagesFromFiles(reloadedImages));
		}
		this.refresh();
	}

	private void sortFolders() {
		logger.debug("Sort folders");
		Collections.sort(folders, new Comparator<Folder>() {
			public int compare(Folder folder1, Folder folder2) {
				return folder1.getFile().getName()
						.compareTo(folder2.getFile().getName());
			}
		});
	}

}
