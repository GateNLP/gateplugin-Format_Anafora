package gate.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import gate.Document;
import gate.corpora.AnaforaDocumentFormat;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.ExtensionFileFilter;
import gate.util.Files;

@CreoleResource(name = "Anafora Annotation Loader", tool = true, autoinstances = @AutoInstance)
public class AnaforaAnnotationLoader extends ResourceHelper {

  private static final long serialVersionUID = -3791859865663727392L;

  private JFileChooser fileChooser = new JFileChooser();

  public AnaforaAnnotationLoader() {
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.addChoosableFileFilter(
        new ExtensionFileFilter("Anafora Annotation File", "xml"));
    fileChooser.setAcceptAllFileFilterUsed(true);
  }

  @Override
  protected List<Action> buildActions(NameBearerHandle handle) {
    List<Action> actions = new ArrayList<Action>();

    if(!(handle.getTarget() instanceof Document)) return actions;

    actions.add(new AbstractAction("Add Additional Anafora Annotations...") {

      private static final long serialVersionUID = 4814306038334572408L;

      @Override
      public void actionPerformed(ActionEvent arg0) {

        Document doc = (Document)handle.getTarget();

        try {
          File file = Files.fileFromURL(doc.getSourceUrl());
          fileChooser.setCurrentDirectory(file.getParentFile());
        } catch(IllegalArgumentException e) {
          // doc URL is not a file:// so don't set the initisal location
        }

        if(fileChooser.showOpenDialog(
            MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION)
          return;

        try {
          AnaforaDocumentFormat.addAnnotationsToDocument(doc,
              fileChooser.getSelectedFile());
        } catch(DocumentFormatException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    });

    return actions;
  }

}
