package gate.creole.export;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.creole.anafora.AnaforaSchema;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;

//@CreoleResource(name = "Anafora XML Exporter", comment = "Export documents in Anafora XML format", tool = true, autoinstances = @AutoInstance)
public class AnaforaDocumentExporter extends DocumentExporter {

  public AnaforaDocumentExporter() {
    super("Anafora XML", "xml", "text/x-anafora");
  }

  private static final long serialVersionUID = -8451295843341636156L;
  
  private static XMLOutputter outputter = new XMLOutputter();
  
  @RunTime
  @CreoleParameter
  public void setSchemaLocation(URL schemaLocation) { }
  public URL getSchemaLocation() { return null; }
  
  
  @RunTime
  @CreoleParameter(defaultValue=".*\\.completed")
  public void setAnnotationSetRegexp(String regexp) { }
  public String getAnnotationSetRegexp() { return null; }

  @Override
  public void export(Document doc, OutputStream out, FeatureMap options)
      throws IOException {

    if (!options.containsKey("schemaLocation"))
      throw new IOException("Schema Location Not Specified");
    
    if (!options.containsKey("annotationSetRegexp"))
      throw new IOException("No AnnotationSet Regexp specified");
    
    try {
      AnaforaSchema schema = new AnaforaSchema((URL)options.get("schemaLocation"));
      
      Pattern setNamePattern = Pattern.compile((String)options.get("annotationSetRegexp"));
      
      for (String name : doc.getAnnotationSetNames()) {
        if (setNamePattern.matcher(name).find()) {
          System.out.println("exporting " + name);
          
          Element annotations = schema.getAnnotations(doc.getAnnotations(name));
          
          outputter.output(annotations, System.out);
        }
      }
      
      
    } catch(JDOMException e) {
      throw new IOException(e);
    }
    
    //use one (or more) regexps to select annotation sets
  }

}
