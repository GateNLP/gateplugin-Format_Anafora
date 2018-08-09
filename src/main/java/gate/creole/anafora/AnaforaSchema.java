package gate.creole.anafora;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Utils;

public class AnaforaSchema {
  
  private static XMLOutputter outputter = new XMLOutputter();

  private Map<String, Entity> entities = new HashMap<String, Entity>();
  
  private URL schemaURL;

  public AnaforaSchema(URL schemaURL) throws JDOMException, IOException {
    this.schemaURL = schemaURL;
    
    SAXBuilder jdomBuilder = new SAXBuilder();
    Document jdomDocument = jdomBuilder.build(schemaURL);

    // select all the entities
    XPath xpathEntity = XPath.newInstance("//entity");
    XPath xpathProperty = XPath.newInstance("//property");

    for(Element entityElement : (List<Element>)xpathEntity
        .selectNodes(jdomDocument)) {

      Element parent = entityElement.getParentElement();

      Entity entity = new Entity(entityElement.getAttributeValue("type"),
          parent.getAttributeValue("type"));

      for(Element propertyElement : (List<Element>)entityElement
          .getChild("properties").getChildren("property")) {
        entity.getProperties().add(propertyElement.getAttributeValue("type"));
      }

      entities.put(entity.getType(), entity);
    }
  }

  public Element getAnnotations(AnnotationSet annotationSet) throws IOException {
    
    String idSuffix = "@e@"+annotationSet.getDocument().getName()+"@"+annotationSet.getName().split("\\.")[0];
    
    Element root = new Element("annotations");
    
    List<Annotation> annotations = Utils.inDocumentOrder(annotationSet.get(entities.keySet()));
    
    for (Annotation annotation : annotations) {
      Entity schemaEntity = entities.get(annotation.getType());
      
      Element entity = new Element("entity");
      
      Element id = new Element("id");
      id.setText(annotation.getId()+idSuffix);
      entity.addContent(id);
      
      Element span = new Element("span");
      span.setText(annotation.getStartNode().getOffset()+","+annotation.getEndNode().getOffset());
      entity.addContent(span);
      
      Element type = new Element("type");
      span.setText(annotation.getType());
      entity.addContent(type);
      
      Element parent = new Element("parentsType");
      parent.setText(schemaEntity.getParent());
      entity.addContent(parent);
      
      //TODO handle properties
      
      root.addContent(entity);
    }
    
    return root;    
  }
  
  public Set<String> getEntityTypes() {
    return Collections.unmodifiableSet(entities.keySet());
  }
  
  public Entity getEntity(String type) {
    return entities.get(type);
  }

  public static class Entity {

    private String type, parent;

    private Set<String> properties;

    protected Entity(String type, String parent) {
      this.type = type;
      this.parent = parent;
      properties = new HashSet<String>();
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getParent() {
      return parent;
    }

    public void setParent(String parent) {
      this.parent = parent;
    }

    public Set<String> getProperties() {
      return properties;
    }

    public void setProperties(Set<String> properties) {
      this.properties = properties;
    }
  }
}
