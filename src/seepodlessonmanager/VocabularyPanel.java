/*  Copyright 2009 Derek Chadwick
 
    This file is part of the SeePod Lesson Manager.

    SeePod Lesson Manager is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SeePod Lesson Manager is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SeePod Lesson Manager.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Project: SeePod Lesson Manager
 * Author : Derek Chadwick
 * Date   : 7/May/2009
 * Class  : VocabularyPanel
 * 
 * Description: Main panel for Vocabulary tab pane.
 * 
 */

package seepodlessonmanager;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class VocabularyPanel extends JPanel
{
   private Config seePodConfig;
   private CEDICTPanel cedictPanel;
   private VocabularyFileListPanel vocabFilesPanel;
   private VocabularyListPanel vocabListPanel;
   private VocabularyItemPanel vocabItemPanel;

   public VocabularyPanel(Config spc)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("VocabularyPanel() - Config is null.");
      }
      
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
      cedictPanel = new CEDICTPanel(seePodConfig);
     
      Box vocabBox = Box.createHorizontalBox();
      vocabBox.setMinimumSize(new Dimension(800,250));

      vocabItemPanel = new VocabularyItemPanel();
      vocabListPanel = new VocabularyListPanel(seePodConfig, vocabItemPanel);
      vocabFilesPanel = new VocabularyFileListPanel(seePodConfig, vocabListPanel);

      
      vocabBox.add(vocabFilesPanel);
      vocabBox.add(Box.createRigidArea(new Dimension(5,0)));
      vocabBox.add(vocabListPanel);
      vocabBox.add(Box.createRigidArea(new Dimension(5,0)));
      vocabBox.add(vocabItemPanel);
      
      add(cedictPanel);
      add(vocabBox);
      
      return;
   }

   public void resetUI()
   {
      vocabFilesPanel.resetUI();
   }

}
