/*
 * Copyright (c) 1995-2018, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */

package gate.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;

@CreoleResource(name = "Anafora Corpus Populator", tool = true, autoinstances = @AutoInstance)
public class AnaforaCorpusPopulator extends ResourceHelper {

  private static final long serialVersionUID = 1414858374591590472L;

  private static final Logger log =
      Logger.getLogger(AnaforaCorpusPopulator.class);

  @Override
  protected List<Action> buildActions(NameBearerHandle handle) {
    List<Action> actions = new ArrayList<Action>();

    if(!(handle.getTarget() instanceof Corpus)) return actions;

    actions.add(new AbstractAction("Populate from Anafora Corpus...") {

      private static final long serialVersionUID = 4814306038334572408L;

      @Override
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if(fileChooser.showOpenDialog(
            MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION)
          return;

        populate((Corpus)handle.getTarget(), fileChooser.getSelectedFile());
      }

    });

    return actions;
  }

  public void populate(Corpus corpus, File directory) {

    for(File docDir : directory.listFiles((File file) -> file.isDirectory())) {
      try {
        File docTxt = new File(docDir, docDir.getName());

        if(docTxt.exists()) {
          FeatureMap params = Factory.newFeatureMap();

          params.put(Document.DOCUMENT_URL_PARAMETER_NAME,
              docTxt.toURI().toURL());
          params.put(Document.DOCUMENT_MIME_TYPE_PARAMETER_NAME,
              "text/x-anafora");
          
          //TODO check is it always safe to assume UTF-8 for this format?
          params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");

          Document document =
              (Document)Factory.createResource("gate.corpora.DocumentImpl",
                  params, Factory.newFeatureMap(), docTxt.getName());

          corpus.add(document);

          if(corpus.getLRPersistenceId() != null) {
            corpus.unloadDocument(document);
            Factory.deleteResource(document);
          }
        }
      } catch(Exception e) {
        log.warn("Failed to correctly load Anafora document '"
            + docDir.getName() + "'", e);
      }
    }
  }
}
