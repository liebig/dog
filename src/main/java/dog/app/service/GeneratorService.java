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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.field.RtfTotalPageNumber;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.style.RtfFont;

import dog.app.Application;
import dog.app.domain.DocumentToGenerate;

public class GeneratorService implements Runnable {

	public final static String PDF = "pdf";
	public final static String RTF = "rtf";

	final static Logger logger = Logger.getLogger(GeneratorService.class);

	private ArrayList<DocumentToGenerate> documentsToGenerate;

	public GeneratorService(ArrayList<DocumentToGenerate> documentsToGenerate) {
		this.documentsToGenerate = documentsToGenerate;
	}

	public void run() {
		Application.disableButtons();

		for (DocumentToGenerate documentToGenerate : documentsToGenerate) {

			File toFile = null;

			// Create a new Document
			Document document = new Document();

			String outputPath = documentToGenerate.getOutputPath();
			String filename = documentToGenerate.getFilename();
			String outputType = documentToGenerate.getOutputType();
			ArrayList<dog.app.domain.Image> images = documentToGenerate
					.getImages();

			try {

				if (outputType.equals(GeneratorService.PDF)) {

					logger.debug("Writing PDF file");
					toFile = new File(outputPath + File.separator + filename
							+ ".pdf");

					int fileVersion = 1;
					// Check if file already exists, if true increment file
					// version
					while (toFile.exists()) {
						logger.debug("File to write already exists, increment filename: "
								+ toFile.getAbsolutePath());
						toFile = new File(outputPath + File.separator
								+ filename + "." + fileVersion + ".pdf");
						fileVersion++;
					}

					PdfWriter.getInstance(document,
							new FileOutputStream(toFile));
				} else if (outputType.equals(GeneratorService.RTF)) {

					logger.debug("Writing RTF file");
					toFile = new File(outputPath + File.separator + filename
							+ ".rtf");

					int fileVersion = 1;
					// Check if file already exists, if true increment file
					// version
					while (toFile.exists()) {
						logger.debug("File to write already exists, increment filename: "
								+ toFile.getAbsolutePath());
						toFile = new File(outputPath + File.separator
								+ filename + "." + fileVersion + ".rtf");
						fileVersion++;
					}

					RtfWriter2.getInstance(document, new FileOutputStream(
							toFile));
				} else {
					throw new IllegalArgumentException(
							"Only the output type pdf or rtf is supported. Given type: "
									+ outputType);
				}

				logger.debug("Writing to document: " + toFile.getAbsolutePath());
				Application.log("Writing to document: "
						+ toFile.getAbsolutePath());

				// Open the document.
				document.open();

				RtfFont fontDefault = new RtfFont("Arial", 12);
				RtfFont fontDefaultBold = new RtfFont("Arial", 12, Font.BOLD);
				RtfFont fontSmall = new RtfFont("Arial", 9);

				Paragraph breakParagraph = new Paragraph("");

				Paragraph p = new Paragraph("\n", fontSmall);
				p.add("- Page ");
				p.add(new RtfPageNumber());
				p.add(" of ");
				p.add(new RtfTotalPageNumber());
				p.add(" -");
				p.setAlignment(Element.ALIGN_CENTER);
				RtfHeaderFooter footer1 = new RtfHeaderFooter(p);
				document.setFooter(footer1);

				String headerText;
				if (toFile.getParentFile().getName() != null) {
					headerText = toFile.getParentFile().getName();
				} else {
					headerText = "00000000-000 - XXXX0000000";
				}
				String headerSuffix = Application.properties.getProperty(
						"header-suffix", "");

				for (int i = 0; i < images.size(); i++) {

					dog.app.domain.Image currentImage = images.get(i);

					BufferedImage loadedImage = ImageIO.read(currentImage
							.getFile());

					// Load maxResizingString from properties
					String maxResizingString = Application.properties
							.getProperty("resizing-value", "NaN");
					
					// Try to parse integer, or use default (1024) instead
					int maxResizingValue;
					try {
						maxResizingValue = Integer.parseInt(maxResizingString);
					} catch (Exception ex) {
						maxResizingValue = 0;
					}
					
					// If resizing value is greater than 0, resize image
					if (maxResizingValue > 0) {
					
						// Load current image to read height and width
						BufferedImage currentBufferedImage = ImageIO
								.read(currentImage.getFile());
	
						// If current image width is lower than its height, we have
						// to resize the height
						// (always the higher value, because we are using a max
						// resizing value)
						if (currentBufferedImage.getWidth() < currentBufferedImage
								.getHeight()) {
							// Check if the current image height is higher than the
							// max resizing value
							// (because we don't want to make the image higher, only
							// down scaling)
							if (currentBufferedImage.getHeight() > maxResizingValue) {
								logger.debug("Resizing image "
										+ currentImage.getFile().getName()
										+ " to height " + maxResizingValue);
								Application.log("Resizing image "
										+ currentImage.getFile().getName());
								loadedImage = Scalr.resize(loadedImage,
										Scalr.Method.QUALITY, Mode.FIT_TO_HEIGHT,
										maxResizingValue);
							}
						} else {
							// If current image width is higher than its height, we
							// have to resize the width
							// (always the higher value, because we are using a max
							// resizing value)
							// Check if the current image width is higher than the
							// max resizing value
							// (because we don't want to make the image wider, only
							// down scaling)
							if (currentBufferedImage.getWidth() > maxResizingValue) {
								logger.debug("Resizing image "
										+ currentImage.getFile().getName()
										+ " to width " + maxResizingValue);
								Application.log("Resizing image "
										+ currentImage.getFile().getName());
								loadedImage = Scalr.resize(loadedImage,
										Scalr.Method.QUALITY, Mode.FIT_TO_WIDTH,
										maxResizingValue);
							}
						}
					}
						
					String ext = FilenameUtils.getExtension(
							currentImage.getFile().getAbsolutePath())
							.toLowerCase();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(loadedImage, ext, baos);
					baos.flush();
					byte[] imageInByte = baos.toByteArray();
					baos.close();

					Image img = Image.getInstance(imageInByte);

					// Load maxScalingString from properties
					String maxScalingString = Application.properties
							.getProperty("scaling-value", "NaN");
					
					// Try to parse integer, or use default (0) instead
					int maxScalingValue;
					try {
						maxScalingValue = Integer.parseInt(maxScalingString);
					} catch (Exception ex) {
						maxScalingValue = 0;
					}
					
					// If scaling value is greater than 0, scale image
					if (maxScalingValue > 0) {
						float widthScaler = (maxScalingValue * 100) / img.getWidth();
						float heightScaler = (maxScalingValue * 100) / img.getHeight();

						float scaler;

						if (widthScaler > heightScaler) {
							scaler = heightScaler;
						} else {
							scaler = widthScaler;
						}

						logger.debug("Scaling image "
								+ currentImage.getFile().getName()
								+ " with factor " + scaler);
						Application.log("Scaling image "
								+ currentImage.getFile().getName());

						img.scalePercent(scaler);
					}

					
					img.setCompressionLevel(9);
					img.setAlignment(Image.MIDDLE);

					Paragraph imageNumberParagraph = new Paragraph(
							String.valueOf(i), fontDefaultBold);
					imageNumberParagraph.setAlignment(Element.ALIGN_CENTER);

					logger.debug("Adding image "
							+ currentImage.getFile().getName() + " to document");
					Application
							.log("Adding image "
									+ currentImage.getFile().getName()
									+ " to document");

					// Create upper page content
					if ((i % 2) == 0) {

						// Create first header line with company name
						Paragraph headerFirstLine = new Paragraph(
								Application.properties.getProperty("company",
										""), fontDefaultBold);
						// Set first header line alignment
						headerFirstLine.setAlignment(Element.ALIGN_RIGHT);

						// Create second header line with header text and header
						// suffix
						Paragraph headerSecondLine = new Paragraph(headerText
								+ headerSuffix, fontDefault);
						// Set second header line alignment
						headerSecondLine.setAlignment(Element.ALIGN_LEFT);

						// Create header elements
						Element[] headerElements = new Element[3];
						headerElements[0] = headerFirstLine;
						headerElements[1] = headerSecondLine;
						headerElements[2] = breakParagraph;

						// Create header object from elements
						HeaderFooter header = new RtfHeaderFooter(
								headerElements);

						// Set header object
						document.setHeader(header);

						document.add(breakParagraph);
						document.add(breakParagraph);

						Paragraph imageParagraph = new Paragraph();
						imageParagraph.setFont(fontDefault);
						imageParagraph.setAlignment(Element.ALIGN_CENTER);
						imageParagraph.add(img);
						imageParagraph.add(Chunk.NEWLINE);
						// Add image number
						imageParagraph.add(new Phrase(GeneratorService
								.getFormattedImageNumber(i)));
						imageParagraph.add(breakParagraph);
						imageParagraph.add(breakParagraph);

						document.add(imageParagraph);

					} else {
						// Create lower page content

						Paragraph imageParagraph = new Paragraph();
						imageParagraph.setFont(fontDefault);
						imageParagraph.setAlignment(Element.ALIGN_CENTER);
						imageParagraph.add(img);
						imageParagraph.add(Chunk.NEWLINE);
						// Add image number
						imageParagraph.add(new Phrase(GeneratorService
								.getFormattedImageNumber(i)));
						imageParagraph.add(breakParagraph);
						imageParagraph.add(breakParagraph);

						document.add(imageParagraph);

						// Check if there are more images to add and create a new page if true
						if (i + 1 < images.size()) {
							document.newPage();
						}

					}

				}

			} catch (Exception ex) {
				logger.error("Error while generating file: "
						+ toFile.getAbsolutePath(), ex);
				Application.log("Error while generating file: "
						+ toFile.getAbsolutePath());
			} finally {
				document.close();

				logger.debug("Document finished: " + toFile.getAbsolutePath());
				Application.log("Document finished: "
						+ toFile.getAbsolutePath());
			}

		}

		Application.enableButtons();
	}

	public static String getFormattedImageNumber(int imageNumber) {

		if ((imageNumber + 1) < 10) {
			return ("0" + String.valueOf(imageNumber + 1));
		} else {
			return (String.valueOf(imageNumber + 1));
		}

	}

}
