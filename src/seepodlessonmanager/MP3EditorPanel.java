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
 * Date   : 1/2/2009
 * Class  : MP3EditorPanel 
 * 
 * Description: Container for the lesson manager panels, lessonlist, lessonControl and lessonSetList, lessonMediaPlayer.
 * 
 * 
 */


package seepodlessonmanager;


import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class MP3EditorPanel extends JPanel 
{
   private Config seePodConfig;
   private StatusPanel edStatus;
   private DirectoryTree treePanel;
   private MP3Editor mp3Panel;
   private static FileList filePanel;
   
   public MP3EditorPanel(Config spc)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("MP3EditorPanel() - Config is null.");
         return;
      }
      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      
      edStatus = new StatusPanel();
      
      treePanel = new DirectoryTree(seePodConfig, edStatus);
      seePodConfig.setTreePanel(treePanel);
      treePanel.update();
   
      mp3Panel = new MP3Editor(seePodConfig, treePanel, edStatus);
  
      filePanel = new FileList(mp3Panel, seePodConfig);
  
      mp3Panel.setFileList(filePanel);
    
      add(treePanel);
      add(filePanel);
      add(mp3Panel);
      
      return;
   }

   public void saveComboBoxItems()
   {
      mp3Panel.saveComboBoxItems();
   }

   public void resetUI()
   {
      filePanel.resetUI();
      mp3Panel.resetUI();
      treePanel.update();
   }

    void resetComboBoxItems()
    {
       mp3Panel.resetComboBoxItems();
    }
}