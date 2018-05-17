package com.kaaphi.recipe.enex;

import com.evernote.enex.EnExport;
import com.evernote.enex.Note;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class EnexToRecipe {
  
  public void processNote(Note note) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(note.getContent().getBytes(Charset.forName("UTF-8"))));
    
    //doc.getFirstChild()
  }
  
  public EnExport loadExport(File file) throws JAXBException, XMLStreamException {
    JAXBContext jc = JAXBContext.newInstance(EnExport.class);

    XMLInputFactory xif = XMLInputFactory.newFactory();
    xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    XMLStreamReader xsr = xif.createXMLStreamReader(new StreamSource(file));

    Unmarshaller unmarshaller = jc.createUnmarshaller();
    EnExport export = (EnExport) unmarshaller.unmarshal(xsr);
    
    return export;
  }
  
  
  public static void main(String[] args) throws Exception {
    File file = new File("/Users/kaaphi/Documents/Evernote/Recipes.enex");
  }
}
