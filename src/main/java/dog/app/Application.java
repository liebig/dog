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

// TODO
// Adding PDF creation

package dog.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

import dog.app.domain.DocumentToGenerate;
import dog.app.domain.Folder;
import dog.app.domain.FolderTree;
import dog.app.domain.TreeDirectoryNode;
import dog.app.domain.TreeImageNode;
import dog.app.service.FileService;
import dog.app.service.GeneratorService;

import javax.swing.JTextPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.swing.JProgressBar;

public class Application {

	private JFrame frmDogDocument;

	private JTree treeFolder;

	private JCheckBox chckbxRtf;

	private JCheckBox chckbxPdf;

	private FolderTree treeService;

	private static JScrollPane scrollPaneLogs;

	private static JTextPane textPaneLogs;

	private static JButton btnFolderAdd;

	private static JButton btnFolderDelete;

	private static JButton btnGenerate;

	private static JButton btnDeleteAll;

	private static JButton btnRefresh;
	
	private static JProgressBar progressBar;
	
	private static JFrame configWindow = new ConfigWindow();

	final static Logger logger = Logger.getLogger(Application.class);

	final private static File PROPERTIES_FILE = new File(
			"application.properties");

	/*
	 * Structure of properties file:
	 * 
	 * header-suffix: "" 
	 * company: "" 
	 * resizing-value: 0
	 * scaling-value: 0
	 * log-level: warn
	 */
	public static Properties properties = new Properties();
	private JButton btnConfig;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// Load application properties
		try {
			properties.load(new FileInputStream(Application.PROPERTIES_FILE));
		} catch (Exception ex) {
			logger.warn("Could not load properties", ex);
		}
		
		// Load log level
		String logLevel = Application.properties.getProperty("log-level", "");
		// Set log level
		switch (logLevel.toLowerCase()) {
			case "all": 	logger.setLevel(Level.ALL);
	    				 	break;
			case "debug":  	logger.setLevel(Level.DEBUG);
			 			   	break;
			case "error":  	logger.setLevel(Level.ERROR);
							break;
			case "fatal":  	logger.setLevel(Level.FATAL);
							break;
			case "info":  	logger.setLevel(Level.INFO);
							break;
			case "off":  	logger.setLevel(Level.OFF);
							break;
			case "trace":  	logger.setLevel(Level.TRACE);
							break;
			case "warn":  	logger.setLevel(Level.WARN);
							break;
			default: 		logger.setLevel(Level.WARN);
		}
		
		// Create and show Splash Window
		final SplashWindow splashWindow = new SplashWindow();
		splashWindow.frame.setVisible(true);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					Properties props = new Properties();
					props.put("logoString", "");
					AcrylLookAndFeel.setCurrentTheme(props);
					UIManager
							.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");

					URL resource = Application.class
							.getResource("/resources/icons/picture.png");
					Icon pictureIcon = new ImageIcon(resource);

					UIManager.put("Tree.leafIcon", pictureIcon);

					FileService.addExtension("jpg");
					FileService.addExtension("jpeg");
					FileService.addExtension("png");

					final Application window = new Application();
					
					// Timer for showing splash screen only 4 seconds
					new Timer().schedule(new TimerTask() {
					    public void run() {
					    	splashWindow.frame.setVisible(false);
					    	window.frmDogDocument.setVisible(true);
					    }
					}, 4000);
					
					
				} catch (Exception ex) {
					logger.error("Exception while configuring the application",
							ex);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		logger.debug("Starting initializing");

		frmDogDocument = new JFrame();
		frmDogDocument
				.setIconImage(Toolkit.getDefaultToolkit().getImage(
						Application.class
								.getResource("/resources/icons/logo_dog.png")));
		frmDogDocument.setTitle("DOG - Document Generator");
		frmDogDocument.setBounds(100, 100, 450, 457);
		frmDogDocument.setLocationRelativeTo(null);
		frmDogDocument.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Center window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frmDogDocument.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frmDogDocument.getHeight()) / 2);
	    frmDogDocument.setLocation(x, y);

		JPanel mainPane = new JPanel();
		frmDogDocument.getContentPane().add(mainPane, BorderLayout.CENTER);

		JScrollPane scrollPaneFolder = new JScrollPane();

		btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean generateRtf = chckbxRtf.isSelected();
				boolean genearetPdf = chckbxPdf.isSelected();

				if (!generateRtf && !genearetPdf) {
					JOptionPane.showMessageDialog(null,
							"No output format is selected. Please select one.",
							"Please select output format",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				logger.debug("Starting generating");

				ArrayList<File> filesToGenerateArray = new ArrayList<File>();

				ArrayList<Folder> foldersToGenerateArray = treeService
						.getFolders();

				for (Folder folder : foldersToGenerateArray) {
					filesToGenerateArray.add(folder.getFile());
				}

				// Get Folders from files
				ArrayList<Folder> foldersToGenerate = new ArrayList<Folder>();
				for (File file : filesToGenerateArray) {
					for (Folder folder : treeService.getFolders()) {
						if (folder.getFile().getAbsolutePath()
								.equals(file.getAbsolutePath())) {
							foldersToGenerate.add(folder);
						}
					}
				}

				logger.debug("Generating folders");

				ArrayList<DocumentToGenerate> documentsToGenerate = new ArrayList<DocumentToGenerate>();

				for (Folder folder : foldersToGenerate) {

					logger.debug("Generating folder: "
							+ folder.getFile().getAbsolutePath());

					if (genearetPdf) {
						documentsToGenerate.add(new DocumentToGenerate(folder
								.getFile().getAbsolutePath(), folder.getFile()
								.getName(), GeneratorService.PDF, folder
								.getImages()));
					}

					if (generateRtf) {
						documentsToGenerate.add(new DocumentToGenerate(folder
								.getFile().getAbsolutePath(), folder.getFile()
								.getName(), GeneratorService.RTF, folder
								.getImages()));
					}

				}

				GeneratorService generator = new GeneratorService(
						documentsToGenerate);
				Thread t1 = new Thread(generator);
				t1.start();

			}
		});
		btnGenerate.setIcon(new ImageIcon(Application.class
				.getResource("/resources/icons/cog_go.png")));

		this.chckbxRtf = new JCheckBox("RTF");
		chckbxRtf.setEnabled(false);
		chckbxRtf.setSelected(true);

		this.chckbxPdf = new JCheckBox("PDF");
		chckbxPdf.setEnabled(false);

		scrollPaneLogs = new JScrollPane();

		btnFolderAdd = new JButton("");
		btnFolderAdd.setToolTipText("Add folder");
		btnFolderAdd.setIcon(new ImageIcon(Application.class
				.getResource("/resources/icons/folder_add.png")));
		btnFolderAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser addFolderChooser = new JFileChooser();

				// Show directories only
				addFolderChooser
						.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// Enable multiple selection of folders
				addFolderChooser.setMultiSelectionEnabled(true);

				// Show dialog
				int chooserValue = addFolderChooser.showOpenDialog(null);

				if (chooserValue == JFileChooser.APPROVE_OPTION) {

					logger.debug("Adding directories");

					File[] selectedFiles = addFolderChooser.getSelectedFiles();

					for (File selectedFile : selectedFiles) {

						File[] imageFiles = FileService
								.getFilesFromFolderByExtensions(selectedFile);

						Folder newFolder = new Folder();
						newFolder.setFile(selectedFile);
						newFolder.setImages(FileService
								.getImagesFromFiles(imageFiles));

						logger.debug("Adding directory: "
								+ newFolder.getFile().getAbsolutePath());

						treeService.addFolder(newFolder);
					}
				} else if (chooserValue == JFileChooser.CANCEL_OPTION) {
					logger.debug("Adding directory canceled by user");
				}

			}
		});

		btnFolderDelete = new JButton("");
		btnFolderDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TreePath[] paths = treeFolder.getSelectionPaths();

				if (paths == null || paths.length < 1) {
					// Nothing is selected.
					logger.error("No entries are selected for deleting");
					JOptionPane.showMessageDialog(frmDogDocument,
							"Please select a folder to delete it.",
							"No folder is selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				for (TreePath path : paths) {

					try {
						if (path.getPathComponent(path.getPathCount() - 1) instanceof dog.app.domain.TreeDirectoryNode) {
							TreeDirectoryNode node = (TreeDirectoryNode) path
									.getPathComponent(path.getPathCount() - 1);

							logger.debug("Removing directory by user select: "
									+ node.getFile().getAbsolutePath());
							treeService.removeFolder(node.getFile());

						} else if (path.getPathComponent(path.getPathCount() - 1) instanceof dog.app.domain.TreeImageNode) {

							TreeImageNode node = (TreeImageNode) path
									.getPathComponent(path.getPathCount() - 1);

							logger.debug("Removing directory by user leaf select: "
									+ node.getParentFolder().getFile()
											.getAbsolutePath());

							treeService.removeFolder(node.getParentFolder()
									.getFile());

						} else {
							// Root element is selected
							int selectedOption = JOptionPane.showConfirmDialog(
									null, "Do you want to delete all folders?",
									"Delete all folders",
									JOptionPane.YES_NO_OPTION);
							if (selectedOption == JOptionPane.YES_OPTION) {
								logger.debug("Removing all directories by user");
								treeService.removeAllFolders();
							} else {
								logger.debug("Removing all directories canceled by user");
							}
							return;
						}

					} catch (Exception ex) {
						logger.error("Exception while deleting directory", ex);
					}
				}
			}
		});
		btnFolderDelete.setToolTipText("Delete Folder");
		btnFolderDelete.setIcon(new ImageIcon(Application.class
				.getResource("/resources/icons/folder_delete.png")));

		btnDeleteAll = new JButton("");
		btnDeleteAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int selectedOption = JOptionPane.showConfirmDialog(null,
						"Do you want to delete all folders?",
						"Delete all folders", JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) {
					treeService.removeAllFolders();
				}
			}
		});
		btnDeleteAll.setToolTipText("Delete all folders");
		btnDeleteAll.setIcon(new ImageIcon(Application.class
				.getResource("/resources/icons/delete.png")));

		btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int selectedOption = JOptionPane.showConfirmDialog(null,
						"Do you want to reload all folders?",
						"Reload all folders", JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) {
					treeService.reloadFolders();
				}

			}
		});
		btnRefresh.setToolTipText("Refresh folders");
		btnRefresh.setIcon(new ImageIcon(Application.class
				.getResource("/resources/icons/folder_magnify.png")));
		
		progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		
		btnConfig = new JButton("");
		btnConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configWindow.setVisible(true);
			}
		});
		btnConfig.setToolTipText("Configuration");
		btnConfig.setIcon(new ImageIcon(Application.class.getResource("/resources/icons/cog.png")));
		GroupLayout gl_mainPane = new GroupLayout(mainPane);
		gl_mainPane.setHorizontalGroup(
			gl_mainPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_mainPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mainPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPaneFolder, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
						.addComponent(scrollPaneLogs, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
						.addGroup(gl_mainPane.createSequentialGroup()
							.addComponent(btnGenerate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxRtf)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(chckbxPdf))
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
						.addGroup(gl_mainPane.createSequentialGroup()
							.addComponent(btnFolderAdd)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFolderDelete)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDeleteAll)
							.addPreferredGap(ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
							.addComponent(btnRefresh)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnConfig)))
					.addContainerGap())
		);
		gl_mainPane.setVerticalGroup(
			gl_mainPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_mainPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mainPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_mainPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnFolderAdd)
							.addComponent(btnFolderDelete)
							.addComponent(btnDeleteAll))
						.addComponent(btnRefresh)
						.addComponent(btnConfig))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPaneFolder, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_mainPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_mainPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnGenerate)
							.addComponent(chckbxRtf))
						.addComponent(chckbxPdf))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPaneLogs, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(8))
		);

		textPaneLogs = new JTextPane();
		scrollPaneLogs.setViewportView(textPaneLogs);
		textPaneLogs.setEditable(false);
		textPaneLogs.setContentType("text/html");

		this.treeFolder = new JTree();
		treeService = new FolderTree("Projects", treeFolder);
		treeFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				int selRow = treeFolder.getRowForLocation(e.getX(), e.getY());

				if (selRow != -1) {
					if (e.getClickCount() == 2) {

						Object selectedElement = treeFolder
								.getLastSelectedPathComponent();

						if (selectedElement instanceof dog.app.domain.TreeDirectoryNode) {

							TreeDirectoryNode selectedTreeElement = (TreeDirectoryNode) treeFolder
									.getLastSelectedPathComponent();

							if (selectedTreeElement.isLeaf()) {

								if (selectedTreeElement.getParent() == null
										|| !(selectedTreeElement.getParent() instanceof dog.app.domain.TreeDirectoryNode)) {
									// Clicked on empty directory
								} else {
									// Clicked on image
								}

							} else {
								// Clicked on directory
							}

						} else if (selectedElement instanceof dog.app.domain.TreeImageNode) {
							// Clicked on image
						} else {
							// Clicked on root element
						}
					}
				}

			}
		});

		scrollPaneFolder.setViewportView(treeFolder);
		mainPane.setLayout(gl_mainPane);

		treeFolder.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				Component c = super.getTreeCellRendererComponent(tree, value,
						selected, expanded, isLeaf, row, focused);

				URL resource = Application.class
						.getResource("/resources/icons/picture.png");
				Icon pictureIcon = new ImageIcon(resource);

				resource = Application.class
						.getResource("/resources/icons/folder.png");
				Icon folderIcon = new ImageIcon(resource);

				resource = Application.class
						.getResource("/resources/icons/folder_go.png");
				Icon rootIcon = new ImageIcon(resource);

				if (row == 0) {
					setIcon(rootIcon);
					return c;
				}

				try {

					if (value instanceof dog.app.domain.TreeImageNode) {
						setIcon(pictureIcon);
					} else {
						setIcon(folderIcon);
					}

				} catch (Exception ex) {
					logger.error("Exception while setting icon", ex);
					setIcon(folderIcon);
				}

				return c;
			}
		});

		logger.debug("Finished initializing");

	}

	public static synchronized void enableButtons() {
		
		// Disable progress bar
		progressBar.setIndeterminate(false);
		progressBar.setEnabled(false);
		
		// Enable buttons
		btnDeleteAll.setEnabled(true);
		btnFolderAdd.setEnabled(true);
		btnFolderDelete.setEnabled(true);
		btnGenerate.setEnabled(true);
		btnRefresh.setEnabled(true);
	}

	public static synchronized void disableButtons() {
		
		// Disable buttons
		btnDeleteAll.setEnabled(false);
		btnFolderAdd.setEnabled(false);
		btnFolderDelete.setEnabled(false);
		btnGenerate.setEnabled(false);
		btnRefresh.setEnabled(false);
		
		// Enable progress bar
		progressBar.setEnabled(true);
		progressBar.setIndeterminate(true);
	}

	public static synchronized void log(String message) {
		HTMLDocument doc = (HTMLDocument) textPaneLogs.getStyledDocument();
		try {

			if (doc.getLength() > 0) {
				doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
						"<br />");
			}

			doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
					message);

		} catch (Exception ex) {
			logger.error("Exception while logging to text pane", ex);
		}

		textPaneLogs.setCaretPosition(textPaneLogs.getDocument().getLength());
	}

	public static synchronized void saveProperties() {
		OutputStream output = null;

		try {

			output = new FileOutputStream(Application.PROPERTIES_FILE);
			Application.properties.store(output, null);

		} catch (IOException ex) {
			Application.logger.error("Cannot save properties file", ex);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException ex) {
					Application.logger
							.error("Cannot close FileOutputStream while saving properties file",
									ex);
				}
			}

		}
	}

}
