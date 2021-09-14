/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaoca480;

/**
 *
 * @author Vinicius
 */
/**
 * This class is used to display a XML document in a form of a
 * interactive visible tree. When the window is closed, the system
 * does not exit (it only release resource). If the client of this class
 * wants to quit the whole system, the client program need either
 * set the windowListener, or need to run System.exit(0) explictly.
 */
import java.io.IOException;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.Iterator;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;

public final class XMLTree{
 
//The JTree to display the XML
private JTree xmlTree;
//The XML document to be output to the JTree
private Document xmlDoc;
DefaultMutableTreeNode tn;

public XMLTree(JTree xmlTree){
    this.xmlTree = xmlTree;    
}
 
public void create(Document doc){
    this.xmlDoc = doc;
    tn= new DefaultMutableTreeNode("XML");
    processElement(xmlDoc.getRootElement(), tn);
    ((DefaultTreeModel)xmlTree.getModel()).setRoot(tn);   
}
 

private void processElement(Element el, DefaultMutableTreeNode dmtn) {
    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(el.getName());
    String text = el.getTextNormalize();
    if((text != null) && (!text.equals(""))) currentNode.add(new DefaultMutableTreeNode(text));
    processAttributes(el, currentNode);
    Iterator children = el.getChildren().iterator();
    while(children.hasNext()) processElement((Element)children.next(), currentNode);
    dmtn.add(currentNode);
}
 
private void processAttributes(Element el, DefaultMutableTreeNode dmtn) {
    Iterator atts = el.getAttributes().iterator();
    while(atts.hasNext()){
        Attribute att = (Attribute) atts.next();
        DefaultMutableTreeNode attNode = new DefaultMutableTreeNode("@"+att.getName());
        attNode.add(new DefaultMutableTreeNode(att.getValue()));
        dmtn.add(attNode);
    }
 }

public void process(String xml){
    SAXBuilder builder = new SAXBuilder();
    Document doc = null;
    try {
        doc = builder.build(new InputSource(new StringReader(xml)));
    } catch (JDOMException | IOException ex) {
        Logger.getLogger(XMLTree.class.getName()).log(Level.SEVERE, null, ex);
    }
    create(doc);
}

}
