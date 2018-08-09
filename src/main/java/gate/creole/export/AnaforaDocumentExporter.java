package gate.creole.export;

import java.io.IOException;
import java.io.OutputStream;

import gate.Document;
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;

//@CreoleResource(name = "Anafora XML Exporter", comment = "Export documents in Anafora XML format", tool = true, autoinstances = @AutoInstance)
public class AnaforaDocumentExporter extends DocumentExporter {

  public AnaforaDocumentExporter() {
    super("Anafora XML", "xml", "text/x-anafora");
  }

  private static final long serialVersionUID = -8451295843341636156L;

  @Override
  public void export(Document arg0, OutputStream arg1, FeatureMap arg2)
      throws IOException {
    // TODO Auto-generated method stub

  }

}
