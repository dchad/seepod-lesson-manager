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
 * Class  : WLCPXmlPanel
 * 
 * Description: Main panel for the XML file list and viewer.
 * 
 * 
 */


package weepod;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class WLCPXmlPanel extends JPanel
{
    private XMLFileList xList;
    private XMLViewer xView;
    private Config weePodConfig;

    public WLCPXmlPanel(Config wpc)
    {
        super(false);

        if (wpc != null)
        {
            weePodConfig = wpc;
        }
        else
        {
            Debug.debug("WLCPXmlPanel() - Config is null.");
            return;
        }

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        xView = new XMLViewer(weePodConfig);
        xList = new XMLFileList(xView, weePodConfig);
        
        add(xList);
        add(Box.createRigidArea(new Dimension(0,5)));
        add(xView);
    }

    public void resetList()
    {
        xList.listDirectory();
    }

    public void loadPage(String url)
    {
        xView.loadURL(url);
    }
}
