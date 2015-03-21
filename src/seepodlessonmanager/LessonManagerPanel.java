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
 * Class  : LessonManagerPanel 
 * 
 * Description: Container for the lesson manager panels, lessonlist, lessonControl and lessonSetList, lessonMediaPlayer.
 * 
 *
 * 
 */


package seepodlessonmanager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class LessonManagerPanel extends JPanel 
{
   private LessonListPanel lessonListPanel;
   private LessonManagerControl lessonControlPanel;
   private LessonSetPanel lessonSetPanel;
   private Config seePodConfig;
   private MediaPlayerPanel mp3PlayerPanel;
   
   public LessonManagerPanel(Config spc)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("LessonManagerPanel() - Config is null.");
         return;
      }
      
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      
      lessonControlPanel = new LessonManagerControl(seePodConfig);
      lessonListPanel = new LessonListPanel(lessonControlPanel, seePodConfig);
      lessonSetPanel = new LessonSetPanel(lessonControlPanel, lessonListPanel, seePodConfig);
      lessonListPanel.setLessonSetPanel(lessonSetPanel);
      mp3PlayerPanel = new MediaPlayerPanel(lessonControlPanel, seePodConfig);
      lessonListPanel.setMediaPlayerPanel(mp3PlayerPanel);
      
      Box setBox = Box.createVerticalBox();
      setBox.add(lessonSetPanel);
      setBox.add(mp3PlayerPanel);
      
      add(lessonListPanel);
      add(setBox);
      add(lessonControlPanel);
      
      return;
   }
   
   public void openLessonSet()
   {
      lessonListPanel.openLessonSet();
   }
   
   public void saveLessonSet()
   {
      lessonListPanel.saveLessonSet();
   }

   public void clearLessonSet()
   {
      lessonListPanel.clearLessonSet();
   }

   public void resetUI()
   {
      lessonListPanel.resetUI();
      lessonSetPanel.resetUI();
      lessonControlPanel.resetUI();
      mp3PlayerPanel.resetUI();
   }

   public void setDatabasePanel(LessonDatabasePanel ldp)
   {
      lessonListPanel.setDatabasePanel(ldp);
   }
}