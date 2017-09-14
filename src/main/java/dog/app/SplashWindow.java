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
package dog.app;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import java.net.URL;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

public class SplashWindow {

	JFrame frame;

	/**
	 * Create the application.
	 */
	public SplashWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setBounds(100, 100, 400, 220);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// Center window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(12, 182, 376, 25);
		frame.getContentPane().add(progressBar);

		URL logoResource = getClass().getClassLoader()
				.getResource("icons/logo_dog.png");
		Icon logoIcon = new ImageIcon(logoResource);

		JLabel logoLabel = new JLabel(logoIcon);
		logoLabel.setBounds(12, -8, 128, 128);
		frame.getContentPane().add(logoLabel);

		JLabel headlineLabel = new JLabel("DOG");
		headlineLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
		headlineLabel.setBounds(152, 13, 206, 46);
		frame.getContentPane().add(headlineLabel);

		JLabel sloganLabel = new JLabel("Document Generator");
		sloganLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sloganLabel.setBounds(152, 58, 206, 16);
		frame.getContentPane().add(sloganLabel);

		JLabel authorLabel = new JLabel("Version 1.0.2");
		authorLabel.setEnabled(false);
		authorLabel.setBounds(152, 78, 91, 16);
		frame.getContentPane().add(authorLabel);

		JLabel legalLabel = new JLabel("<html>Licensed under Affero General Public License (AGPL)<br>Silk icon set by Mark James under CC BY 2.5</html>");
		legalLabel.setHorizontalAlignment(SwingConstants.CENTER);
		legalLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		legalLabel.setBounds(12, 123, 366, 46);
		frame.getContentPane().add(legalLabel);

		JLabel lblDevelopedByMarc = new JLabel("Developed by Marc Liebig");
		lblDevelopedByMarc.setBounds(152, 104, 166, 16);
		frame.getContentPane().add(lblDevelopedByMarc);



	}
}
