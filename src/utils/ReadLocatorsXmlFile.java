package utils;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReadLocatorsXmlFile {



  /**
   * Read all the values from the given xml file and saved into HashMap
   * @param xmlFileName Name of the file
   * @param dir Directory where the xml file name can be found
   * @author Ankit
   * 
   */
  public HashMap<String, HashMap<String, HashMap<String, String>>> getObjectRepository(String dir,
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
}
