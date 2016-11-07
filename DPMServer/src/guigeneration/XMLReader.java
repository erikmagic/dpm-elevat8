package guigeneration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException; 
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;

import gui.MainWindow;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {
	
	public static EV3GeneratedPanel createEV3Panel(String file, MainWindow mw){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			doc = db.parse(new File(file));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		EV3GeneratedPanel EV3Panel = new EV3GeneratedPanel(mw, doc);
		return EV3Panel;
	}
	/*
	public static Document trimWhitespaceNodes(Document Doc){
		XPathFactory xpf = XPathFactory.newInstance();
		XPathExpression expression = xpf.newXPath().compile("//node().T)
		NodeList trimmedList 
		Node node;
		for (int i = 0; i < nodes.getLength(); i++){
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				
			}
		}
	}*/
	
	//TODO use in createEV3Panel method to clean code.
	public static Document getContentsDoc(String file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			doc = db.parse(new File(file));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return doc;
	}
	
	public static Node getNextNonTextNode(Node node, boolean returnText){
		Node temp1, temp2;
		temp1 = node;
		while (temp1.getNodeName() == "#text"){
			temp2 = node.getNextSibling();
			if (temp2 != null){
				temp1 = temp2;
			} else {
				if (returnText){
					return temp1;
				} else {
					return null;
				}
			}
		}
		return temp1;
	}
}
