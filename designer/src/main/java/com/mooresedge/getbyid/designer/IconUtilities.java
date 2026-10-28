package com.mooresedge.getbyid.designer;

import javax.swing.Icon;
import java.io.IOException;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import com.inductiveautomation.ignition.client.icons.SvgIconUtil;
import com.inductiveautomation.perspective.designer.DesignerHook;

/**
 * Utility class for handling SVG icons in the Ignition Designer.
 * This class provides methods to load and create Swing Icon objects from SVG
 * files.
 */
public class IconUtilities {

	/**
	 * The XML parser class name used for parsing SVG documents.
	 * This is obtained from Apache Batik's XMLResourceDescriptor.
	 */
	public static String xmlParser = XMLResourceDescriptor.getXMLParserClassName();

	/**
	 * Factory for creating SVG documents from XML input.
	 * This factory is initialized with the XML parser specified above.
	 */
	public static SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(xmlParser);

	/**
	 * Loads an SVG file and converts it into a Swing Icon object.
	 * 
	 * This method performs the following steps:
	 * 1. Loads the SVG file from the specified path using the DesignerHook's class
	 * loader.
	 * 2. Creates an SVG document from the loaded file.
	 * 3. Converts the SVG document into a Swing Icon with dimensions of 16x16
	 * pixels.
	 *
	 * @param filePath The path to the SVG file, relative to the classpath.
	 * @return An Icon object representing the loaded SVG.
	 * @throws RuntimeException If there's an error loading or processing the SVG
	 *                          file.
	 */
	public static Icon getSvgIcon(String filePath) {
		final Icon componentIcon;
		try (var inputStream = DesignerHook.class.getResourceAsStream(filePath)) {
			// Create an SVG document from the input stream
			SVGDocument document = factory.createSVGDocument(filePath, inputStream);
			// Convert the SVG document to a Swing Icon
			componentIcon = new SvgIconUtil.SvgIcon(document, 16, 16);
		} catch (IOException e) {
			// If there's an error loading the icon, throw a RuntimeException
			throw new RuntimeException("Unable to load resource icon", e);
		}

		return componentIcon;
	}
}