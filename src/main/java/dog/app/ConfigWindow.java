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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class ConfigWindow extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -6215513356222484179L;
	private JPanel contentPane;
	private JTextField companyField;
	private JTextField suffixField;
	private JSpinner resizeSpinner;
	private JSpinner scaleSpinner;
	private JComboBox comboBox;


	/**
	 * Create the frame.
	 */
	public ConfigWindow() {
		setTitle("Configuration");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("icons/cog.png")));
		setAlwaysOnTop(true);
		setResizable(false);
		setBounds(100, 100, 400, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Center window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
	    setLocation(x, y);

		JLabel configLabel = new JLabel("Configuration");
		configLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		configLabel.setBounds(12, 13, 370, 33);
		contentPane.add(configLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 44, 394, 2);
		contentPane.add(separator);

		companyField = new JTextField();
		companyField.setToolTipText("Company name will be displayed in header");
		companyField.setBounds(154, 59, 228, 22);
		contentPane.add(companyField);
		companyField.setColumns(10);

		JLabel companyLabel = new JLabel("Company");
		companyLabel.setBounds(12, 59, 130, 22);
		contentPane.add(companyLabel);

		suffixField = new JTextField();
		suffixField.setToolTipText("Suffix will be displayed in header after folder name");
		suffixField.setColumns(10);
		suffixField.setBounds(154, 94, 228, 22);
		contentPane.add(suffixField);

		JLabel suffixLabel = new JLabel("Header Suffix");
		suffixLabel.setBounds(12, 94, 130, 22);
		contentPane.add(suffixLabel);

		resizeSpinner = new JSpinner();
		resizeSpinner.setToolTipText("Resizing will reduce image size and quality");
		resizeSpinner.setBounds(154, 129, 90, 22);
		contentPane.add(resizeSpinner);

		JLabel resizeLabel = new JLabel("Image resizing");
		resizeLabel.setBounds(12, 129, 130, 22);
		contentPane.add(resizeLabel);

		JLabel pixelLabel = new JLabel("px");
		pixelLabel.setBounds(256, 129, 56, 22);
		contentPane.add(pixelLabel);

		JLabel pixelLabel2 = new JLabel("px");
		pixelLabel2.setBounds(256, 164, 56, 22);
		contentPane.add(pixelLabel2);

		scaleSpinner = new JSpinner();
		scaleSpinner.setToolTipText("Scaling will reduce the displayed image size");
		scaleSpinner.setBounds(154, 164, 90, 22);
		contentPane.add(scaleSpinner);

		JLabel scaleLabel = new JLabel("Image scaling");
		scaleLabel.setBounds(12, 164, 130, 22);
		contentPane.add(scaleLabel);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "All",
				"Debug", "Info", "Warn", "Error", "Fatal", "Off" }));
		comboBox.setSelectedIndex(0);
		comboBox.setToolTipText("Select which messages should be logged");
		comboBox.setBounds(154, 199, 119, 22);
		comboBox.setEnabled(true);
		comboBox.setEditable(false);
		contentPane.add(comboBox);

		JLabel LoglevelLabel = new JLabel("Log level");
		LoglevelLabel.setBounds(12, 199, 56, 22);
		contentPane.add(LoglevelLabel);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		cancelButton.setBounds(285, 249, 97, 25);
		contentPane.add(cancelButton);

		JButton OkButton = new JButton("OK");
		OkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Application.properties.setProperty("company", companyField.getText());
				Application.properties.setProperty("header-suffix", suffixField.getText());
				Application.properties.setProperty("resizing-value", String.valueOf(resizeSpinner.getValue()));
				Application.properties.setProperty("scaling-value", String.valueOf(scaleSpinner.getValue()));
				Application.properties.setProperty("log-level", String.valueOf(comboBox.getSelectedItem()));

				Application.saveProperties();

				setVisible(false);
			}
		});
		OkButton.setBounds(176, 249, 97, 25);
		contentPane.add(OkButton);

		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				/* code run when component hidden */

				// Center window
				Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
			    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
			    setLocation(x, y);
			}

			public void componentShown(ComponentEvent e) {

				// Get configs
				String companyName = Application.properties.getProperty("company", "");
				String suffix = Application.properties.getProperty("header-suffix", "");

				String maxScalingString = Application.properties
						.getProperty("scaling-value", "NaN");
				int maxScalingValue;
				try {
					maxScalingValue = Integer.parseInt(maxScalingString);
				} catch (Exception ex) {
					maxScalingValue = 0;
				}

				String maxResizingString = Application.properties
						.getProperty("resizing-value", "NaN");
				int maxResizingValue;
				try {
					maxResizingValue = Integer.parseInt(maxResizingString);
				} catch (Exception ex) {
					maxResizingValue = 0;
				}

				String loglevel = Application.properties
						.getProperty("log-level", "warn");

				// Set configs
				setConfig(companyName, suffix, maxResizingValue, maxScalingValue, loglevel);
			}
		});
	}

	private void setConfig(String company, String suffix, int resizing, int scaling, String loglevel) {
		this.companyField.setText(company);
		this.suffixField.setText(suffix);
		this.resizeSpinner.setValue(resizing);
		this.scaleSpinner.setValue(scaling);
		this.comboBox.setSelectedItem(loglevel);
	}
}
