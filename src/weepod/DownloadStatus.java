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
 * Class  : DownloadStatus 
 * 
 * Description: Displays WlCP and RSS status messages.
 * 
 * 
 */


package weepod;

import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

public class DownloadStatus extends JPanel 
{
   
   protected File curMP3File;
   private String wlcpDirectory;
   private JTextArea statusMessageList;
   private JScrollPane listScroller;
   private JViewport listView;
   
   public DownloadStatus() 
   {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      //setPreferredSize(new Dimension(375,600))
      setBorder(BorderFactory.createTitledBorder("Download Status"));
      
      statusMessageList = new JTextArea(100,5); //(rows,columns)
      statusMessageList.setEditable(false);
      
      listScroller = new JScrollPane();
      listView = listScroller.getViewport();
      listScroller.getViewport().add(statusMessageList);
      add(listScroller);
     
   }
   
   public synchronized void printStatusLine(String statusMsg)
   {
      if (statusMsg != null)
      {
         statusMessageList.append(statusMsg.concat(Config.LINE_FEED));
         statusMessageList.setCaretPosition(statusMessageList.getText().length());
      }
   }
   
}