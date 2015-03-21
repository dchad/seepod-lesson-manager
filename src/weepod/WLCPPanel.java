
/*  Copyright 2009 Derek Chadwick
 
    This file is part of the WeePod WLCP GUI.

    WeePod WLCP GUI is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WeePod WLCP GUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WeePod WLCP GUI.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Project: WeePod WLCP GUI
 * Author : Derek Chadwick
 * Date   : 1/2/2009
 * Class  : WLCPPanel 
 * 
 * Description: Container for the lesson manager panels, lessonlist, lessonControl and lessonSetList, lessonMediaPlayer.
 * 
 * 
 */


package weepod;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class WLCPPanel extends JPanel 
{
   private Config seePodConfig;
   private DownloadStatus dlStatus;
   private WLCPLessonDownloadList seePodDownloadList;
   private WLCPController seePodDownloadController;
   private RssFeed seePodRssFeed;
   
   public WLCPPanel(Config spc)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("WLCPPanel() - Config is null.");
      }
      
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
      Box seePodDLTopPanel = Box.createHorizontalBox();
      
      dlStatus = new DownloadStatus();
      seePodDownloadController = new WLCPController(seePodConfig, dlStatus);
      seePodDownloadList = new WLCPLessonDownloadList(seePodDownloadController, seePodConfig, dlStatus);
      seePodRssFeed = new RssFeed(seePodDownloadList, seePodDownloadController, seePodConfig, dlStatus);
    
      seePodDLTopPanel.add(seePodDownloadList);
      seePodDLTopPanel.add(seePodRssFeed);
      seePodDLTopPanel.add(seePodDownloadController);

      add(seePodDLTopPanel);
    
      add(dlStatus);
    
      seePodRssFeed.checkRSSAutoStart(); //should not do this here!
      
      return;
   }

   public void resetUI()
   {
      seePodRssFeed.resetUI();
      seePodDownloadController.resetUI();
   }

}