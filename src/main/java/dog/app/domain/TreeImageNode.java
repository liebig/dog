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

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeImageNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	public Folder getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	private Folder parentFolder;
	private Image image;

	public TreeImageNode(Image image, Folder parentFolder) {
		this.image = image;
		this.parentFolder = parentFolder;
	}

	@Override
	public String toString() {
		return this.image.getFile().getName();
	}

}
