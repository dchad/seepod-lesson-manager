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
 * Class  : Main
 * 
 * Description: Application entry, displays the splash screen, sets the L&F then creates the top level GUI components.
 * 
 */

package weepod;


import javax.swing.BoxLayout;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class WeePod
{
   private static Config weePodConfig;
   private static JFrame frame;
   public static ColourScheme laf;
   public static WLCPPanel weePodDownloadPanel;
   public static WLCPXmlPanel weePodXmlPanel;
   public static JTabbedPane weePodTabbedPane;
   
   private static void createAndShowGUI() 
   {
      weePodConfig = new Config();
      laf = new ColourScheme(weePodConfig);
      laf.setUILookAndFeel();
      
       //do the main frame
       frame = new JFrame("WeePod WLCP GUI");
       Container fcp = frame.getContentPane();
       fcp.setLayout(new BoxLayout(fcp, BoxLayout.X_AXIS));
       fcp.setPreferredSize(new Dimension(1200,800));

       frame.setBounds(weePodConfig.getScreenX(), weePodConfig.getScreenY(), weePodConfig.getScreenWidth(), weePodConfig.getScreenHeight());

       createMainMenuBar();

       weePodConfig.setAppFrame(frame);

       frame.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { Debug.debug("Main.windowClosing() - saving config file."); weePodConfig.saveConfigFile(); }
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
       });

       //now the WLCP downloader components for the download tab

       weePodDownloadPanel = new WLCPPanel(weePodConfig);

       weePodXmlPanel = new WLCPXmlPanel(weePodConfig);

       weePodConfig.setXMLPanel(weePodXmlPanel);

       //now put it all into the tabbed pane
       weePodTabbedPane = new javax.swing.JTabbedPane();

       weePodTabbedPane.addTab("WLCP Download", null, weePodDownloadPanel, "Download Lessons with WLCP, download ChinesePod RSS feed.");
       weePodTabbedPane.addTab("XML Viewer", null, weePodXmlPanel, "View WlCP XML files.");

       //now pack it all up into the main frame
       fcp.add(weePodTabbedPane);

       frame.pack();
       frame.setVisible(true);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) 
    {
        showStartDialog();
        //Schedule a job for the event dispatch thread to create and show the GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                createAndShowGUI();  
            }
        });

    }
    
    public static void showStartDialog()
    {
       Splash sp = new Splash();
    }
    
    public static void createMainMenuBar()
    {
       JMenuBar mainMenu = new JMenuBar();
       
       JMenu fileMenu = new JMenu("File");
       fileMenu.setMnemonic(KeyEvent.VK_F1);
       mainMenu.add(fileMenu);
       
       fileMenu.addSeparator();

       JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
       exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
       exitItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            weePodConfig.saveConfigFile();
            System.exit(0);
         }
       });
       fileMenu.add(exitItem);
       
       //second popup menu
       JMenu configMenu = new JMenu("Options");
       configMenu.setMnemonic(KeyEvent.VK_F2);
       mainMenu.add(configMenu);
       
       JMenuItem configItem = new JMenuItem("Settings", KeyEvent.VK_C);
       configItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
       configItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            //Debug.debug("Option menu item clicked.");
            ConfigDialog cfd = new ConfigDialog(frame, weePodConfig);
            cfd.showDialog();
         }
       });
       configMenu.add(configItem);
       
       JMenuItem colorItem = new JMenuItem("Colour Scheme", KeyEvent.VK_P);
       colorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
       colorItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            ColourSchemeDialog csd = new ColourSchemeDialog(frame, laf, weePodConfig);
            csd.showDialog();
         }
       });
       configMenu.add(colorItem);
 
       //third popup menu
       JMenu helpMenu = new JMenu("Help");
       helpMenu.setMnemonic(KeyEvent.VK_F3);
       mainMenu.add(helpMenu);
       
       JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
       aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
       aboutItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            AboutDialog abd = new AboutDialog(frame);
         }
       });
       helpMenu.add(aboutItem);

       JMenuItem webItem = new JMenuItem("SeePod.net", KeyEvent.VK_W);
       webItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.ALT_MASK));
       //webItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       webItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            weePodXmlPanel.loadPage("http://www.seepod.net/weepoduserguide.html");
            weePodTabbedPane.setSelectedIndex(1);
         }
       });
       helpMenu.add(webItem);

       JMenuItem cPodItem = new JMenuItem("ChinesePod.com", KeyEvent.VK_H);
       cPodItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
       //webItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       cPodItem.addActionListener(new java.awt.event.ActionListener()
       {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            weePodXmlPanel.loadPage(Config.CPOD_URL);
            weePodTabbedPane.setSelectedIndex(1);
         }
       });
       helpMenu.add(cPodItem);
       
       JMenuItem wlcpItem = new JMenuItem("WLCP Website", KeyEvent.VK_I);
       wlcpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
       //webItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       wlcpItem.addActionListener(new java.awt.event.ActionListener()
       {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            //openBrowser("http://code.google.com/p/wlcp/");
            weePodXmlPanel.loadPage("http://code.google.com/p/wlcp/");
            weePodTabbedPane.setSelectedIndex(1);
         }
       });
       helpMenu.add(wlcpItem);
       
       frame.setJMenuBar(mainMenu);
       
    }

    public static void resetUI()
    {
       createMainMenuBar();
    }

    private static void openBrowser(String url)
    {
       if (Desktop.isDesktopSupported()) 
       {
          Desktop desktop = Desktop.getDesktop();
          URI uri = null;
          try 
          {
            uri = new URI(url);
            desktop.browse(uri);
          } catch(IOException ioe) {
            Debug.debug("Main.openBrowser() - The system cannot find the " + uri + " file specified");
          } catch(URISyntaxException use) {
            Debug.debug("Main.openBrowser() - Illegal character in path");
          }
       }
    }
}
