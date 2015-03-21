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
 * Class  : WLCPThread
 * 
 * Description: JPanel for embedding MozSwing.
 * 
 * 
 */

package weepod;

import java.awt.Dimension;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.mozilla.browser.IMozillaWindow.VisibilityMode;
import org.mozilla.browser.MozillaPanel;
//import org.mozilla.interfaces.nsIDOMDocument;
//import org.mozilla.interfaces.nsIWebBrowserChrome;
//import org.mozilla.interfaces.nsIWebNavigation;

public class XMLViewer extends JPanel
{
   final MozillaPanel moz;
   private String xulRunnerHome;

   public XMLViewer(Config wpc)
   {
       super(false);
       
       setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
       setPreferredSize(new Dimension(600,600));
       setBorder(BorderFactory.createTitledBorder("XML Viewer"));
       xulRunnerHome = wpc.getHomeDirectory() + File.separator + "lib";
       System.setProperty("MOZSWING_XULRUNNER_HOME", xulRunnerHome);
       moz = new MozillaPanel(VisibilityMode.FORCED_VISIBLE, VisibilityMode.FORCED_VISIBLE);
       moz.setMinimumSize(new Dimension(500,500));
       moz.setUpdateTitle(false);
       moz.load("about:"); 
       add(moz);

   }
   
   public void displayPage(File xml)
   {
       //Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
       //setCursor(hourglassCursor);
       moz.setUpdateTitle(true);
       moz.load(xml.getAbsolutePath());
       //Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
       //setCursor(normalCursor);
       
   }
   
   public void loadURL(String url)
   {
       moz.setUpdateTitle(true);
       moz.load(url);
   }

   public void initPlugins()
   {
       //peraperakun popup dictionary
       //moz.
   }
}

