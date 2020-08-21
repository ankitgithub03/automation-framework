package utils;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReadLocatorsXmlFile {

  private static Logger log = LoggerFactory.getLogger(ReadLocatorsXmlFile.class);

  /**
   * Read all the values from the given xml file and saved into HashMap
   * @param xmlFileName Name of the file
   * @param dir Directory where the xml file name can be found
   * @author Ankit
   * 
   */
  public HashMap<String, HashMap<String, HashMap<String, String>>> getLocators(String dir,
      String xmlFileName) {
    HashMap<String, HashMap<String, HashMap<String, String>>> map = new HashMap<>();
    try {
      Document doc = readXMLFile(dir, xmlFileName);
      NodeList pList = doc.getDocumentElement().getChildNodes();

      for (int pages = 0; pages < pList.getLength(); pages++) {
        NodeList pEList = (NodeList) pList.item(pages);
        if (pList.item(pages).getNodeType() == 1) {
          String pageName = pList.item(pages).getNodeName();
          HashMap<String, HashMap<String, String>> pageElementMap = new HashMap<String, HashMap<String, String>>();
          for (int pageElements = 0; pageElements < pEList.getLength(); pageElements++) {
            NodeList eList = (NodeList) pEList.item(pageElements);
            if (pEList.item(pageElements).getNodeType() == 1) {
              String pageElementName = pEList.item(pageElements).getNodeName();
              HashMap<String, String> elementMap = new HashMap<String, String>();
              for (int identifier = 0; identifier < eList.getLength(); identifier++) {
                if (eList.item(identifier).getNodeType() == 1) {
                  Node iIdentifierNode = eList.item(identifier);
                  if (iIdentifierNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) iIdentifierNode;
                    String tagName = eElement.getTagName();
                    String textContent = eElement.getTextContent();
                    elementMap.put(tagName, textContent);
                  }
                }
              }
              pageElementMap.put(pageElementName, elementMap);
            }
          }
          map.put(pageName, pageElementMap);
        }
      }
    } catch (Exception e) {
      //      logger.info("Object repository :" + xmlFileName + " not found and " + e.getMessage());
      System.exit(0);
    }
    return map;
  }


  public Document readXMLFile(String dir, String xmlFileName) throws Exception {
    Document doc = null;
    try {
      String xml = new MachineSearch().searchMachineForFile(dir, xmlFileName);
      File fXmlFile = new File(xml);
      if(!fXmlFile.exists()) {
        throw new Exception("File not present");
      }
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(fXmlFile);
      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Unable to read XML file");
      throw new Exception("Unable to read XML file");
    }
    return doc;
  }

  /**
   * Get XML pass node value
   *
   * @param path
   * @param parentNode
   * @return
   */
  public HashMapNew getXMLNodeValue(String path, String parentNode){
    HashMapNew map = new HashMapNew();
    try
    {
      File fXmlFile = new File(path);

      if(!fXmlFile.exists()) {
        System.err.println(path+" does not exists");
        return map;
      }

      DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
      Document document = docBuilder.parse(fXmlFile);

      XPathFactory xPathFactory = XPathFactory.newInstance();
      XPath xpath = xPathFactory.newXPath();

      XPathExpression expr = xpath.compile(parentNode);
      Object obj = expr.evaluate(document, XPathConstants.NODESET);
      if(obj != null){
        Node node = ((NodeList)obj).item(0);
        if(node != null){
          NodeList nl = node.getChildNodes();
          for (int child = 0; child < nl.getLength(); child++) {
            String nodeName = nl.item(child).getNodeName().trim();
            String nodeValue = nl.item(child).getTextContent().trim();
            nodeValue = System.getProperty(nodeName) != null && !System.getProperty(nodeName).trim().equalsIgnoreCase("") ? System.getProperty(nodeName).trim() : nodeValue;
            map.put(nodeName, nodeValue);
          }
        }
      }
    }
    catch (Exception e){
      log.info("Threw a Exception in BaseFramework :: while reading xml, full stack trace follows:", e);
      e.printStackTrace();
      System.err.println("Unable to read XML file");
    }
    return map;
  }
}
