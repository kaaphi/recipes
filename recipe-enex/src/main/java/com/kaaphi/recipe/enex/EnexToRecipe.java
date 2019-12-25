package com.kaaphi.recipe.enex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import com.evernote.enex.EnExport;
import com.evernote.enex.Note;
import com.evernote.enex.NoteAttributes;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.txtformat.IngredientParser;

public class EnexToRecipe {
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");
  
  public RecipeBookEntry processNote(Note note) throws ParserConfigurationException, SAXException, IOException  {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    //factory.setValidating(true);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    SAXParser saxParser = factory.newSAXParser();
    
    
    NoteParser handler = new NoteParser();
    saxParser.parse(new ByteArrayInputStream(note.getContent().getBytes(Charset.forName("UTF-8"))), handler);
    
    IngredientParser ingredientParser = new IngredientParser();
    
    List<String> sources = getSources(note);
    
    Recipe recipe = new Recipe(
        note.getTitle(), 
        Collections.singletonList(new IngredientList(null,handler.getIngredients().stream().map(ingredientParser::fromString).collect(Collectors.toList()))),
        handler.getMethod(),
        sources
        );
    
    Instant created = parseInstant(note.getCreated(), Instant::now);
    Instant updated = parseInstant(note.getUpdated(), () -> null);
   
    return new RecipeBookEntry(UUID.randomUUID(), recipe, created, updated, null);
  }

  private List<String> getSources(Note note) {
    List<String> sources = Optional.ofNullable(note.getNoteAttributes())
        .map(NoteAttributes::getSourceUrl)
        .map(url -> Arrays.asList(url))
        .orElse(Collections.emptyList());
    return sources;
  }
  
  private Instant parseInstant(String enexTimestamp, Supplier<Instant> supplyDefault) {
    return Optional.ofNullable(enexTimestamp)
    .map(txt -> ZonedDateTime.parse(txt, TIME_FORMATTER).toInstant())
    .orElseGet(supplyDefault);
  }
  
  public RecipeBookEntry processFailedNote(Note note) {
    List<String> sources = getSources(note);
    
    Recipe recipe = new Recipe(
        note.getTitle(), 
        Collections.emptyList(),
        "",
        sources
        );
    
    Instant created = parseInstant(note.getCreated(), Instant::now);
    Instant updated = parseInstant(note.getUpdated(), () -> null);
   
    return new RecipeBookEntry(UUID.randomUUID(), recipe, created, updated, null);
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
  
  public Set<RecipeBookEntry> toRecipeBook(EnExport export, BiConsumer<Note, Throwable> failureHandler) {
    return export.getNote().stream()
        .map(note -> {
          try {
            return processNote(note);
          } catch (SAXException | ParserConfigurationException | IOException e) {
            failureHandler.accept(note, e);     
            return processFailedNote(note);
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
  
  
  public static void main(String[] args) throws Exception {
    File file = new File("/Users/kaaphi/Documents/Evernote/Recipes.enex");
    
    EnexToRecipe converter = new EnexToRecipe();
    
    AtomicInteger failed = new AtomicInteger();
    Set<RecipeBookEntry> book = converter.toRecipeBook(converter.loadExport(file), (note, exception) -> {
      System.out.format("Failed: <%s> (%s)%n", note.getTitle(), exception);
      failed.getAndIncrement();
    });
    
    
    
    System.out.println(failed);
  }
}
