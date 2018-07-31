package gate.creole.anafora;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.corpora.MimeType;
import gate.corpora.TextualDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.Files;
import gate.util.InvalidOffsetException;

@CreoleResource(name = "Anafora Standoff Document Format", isPrivate = true, autoinstances = {
    @AutoInstance(hidden = true)})
public class AnaforaDocumentFormat extends TextualDocumentFormat {

  private static final long serialVersionUID = -2000783960590128554L;

  private static final Logger log =
      Logger.getLogger(AnaforaDocumentFormat.class);

  @Override
  public Boolean supportsRepositioning() {
    return false;
  }

  @Override
  public Resource init() throws ResourceInstantiationException {

    // create the MIME type object
    MimeType mime = new MimeType("text", "x-anafora");

    // Register the class handler for this mime type
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),
        this);

    // Register the mime type with mine string
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);

    // Set the mimeType for this language resource
    setMimeType(mime);

    return this;
  }

  @Override
  public void cleanup() {
    super.cleanup();

    MimeType mime = getMimeType();

    mimeString2ClassHandlerMap.remove(mime.getType() + "/" + mime.getSubtype());
    mimeString2mimeTypeMap.remove(mime.getType() + "/" + mime.getSubtype());
  }

  @Override
  @SuppressWarnings("unchecked")
  public void unpackMarkup(final Document doc) throws DocumentFormatException {
    super.unpackMarkup(doc);

    if(doc.getSourceUrl() == null) return;

    File txtFile;

    try {
      txtFile = Files.fileFromURL(doc.getSourceUrl());
    } catch(IllegalArgumentException e) {
      log.warn(
          "Can't load Anafora annotations for none file URL based documents");
      return;
    }

    File docFolder = txtFile.getParentFile();

    SAXBuilder builder = new SAXBuilder(false);

    for(File annotationFile : docFolder
        .listFiles((File file) -> file.getName().endsWith(".xml"))) {
      String[] fileData = annotationFile.getName().split("\\.");

      if(!fileData[0].equals(txtFile.getName())) continue;

      System.out.println("need to process " + annotationFile.getName());

      AnnotationSet annotationSet = doc.getAnnotations(
          fileData[fileData.length - 3] + "-" + fileData[fileData.length - 2]);

      try {
        org.jdom.Document annotationDocument = builder.build(annotationFile);

        Element root = annotationDocument.getRootElement();

        Element annotations = root.getChild("annotations");

        for(Element entity : (List<Element>)annotations.getChildren("entity")) {
          String id = entity.getChildTextTrim("id");
          String[] span = entity.getChildTextTrim("span").split(",");
          String type = entity.getChildTextTrim("type");

          if(span.length != 2) {
            log.warn("skipping annotation with more than two offsets: " + id);
            continue;
          }

          FeatureMap features = Factory.newFeatureMap();
          features.put("id", id);

          for(Element property : (List<Element>)entity.getChild("properties")
              .getChildren()) {
            String value = property.getTextTrim();
            if(!value.isEmpty())
              features.put(property.getName(), property.getTextTrim());
          }

          long end = Long.parseLong(span[1]);
          if (end > doc.getContent().toString().length()) {
            end = doc.getContent().toString().length();
            log.warn("annotation runs over end of document and has been truncated: "+span[1]+ " > " + end);
          }
          
          annotationSet.add(Long.valueOf(span[0]), end, type,
              features);
        }

        // TODO process relations using the GATE relations API

      } catch(JDOMException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch(NumberFormatException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch(InvalidOffsetException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
