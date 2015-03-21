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
 * Class  : DownloadStatus 
 * 
 * Description: Displays WlCP and RSS download messages.
 * 
 * 
 */


package seepodlessonmanager;

import java.awt.Dimension;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

public class StatusPanel extends JPanel 
{
   
   protected File curMP3File;
   private JTextArea statusMessageList;
   private JScrollPane listScroller;
   private JViewport listView;
   
   public StatusPanel() 
   {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setPreferredSize(new Dimension(400,600));
      setBorder(BorderFactory.createTitledBorder("Status"));
      
      statusMessageList = new JTextArea(100,80); //(rows,columns)
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