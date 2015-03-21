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
 * Project: SeePod ID3 Tag Editor
 * Author : Derek Chadwick
 * Date   : 1/2/2009
 * Class  : Main
 * 
 * Description: Application entry, displays the splash screen, sets the L&F then creates the top level GUI components.
 * 
 */

package seepodlessonmanager;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class Main 
{
   private static Config seePodConfig;
   private static JFrame frame;
   public static LessonManagerPanel seePodLessonPanel;
   public static ColourScheme laf;
   public static MP3EditorPanel seePodTagPanel;
   public static WLCPPanel seePodDownloadPanel;
   public static VocabularyPanel seePodVocabPanel;
   public static LessonDatabasePanel seePodDatabasePanel;
   
   private static void createAndShowGUI() 
   {
      seePodConfig = new Config();
 
      laf = new ColourScheme(seePodConfig);
      laf.setUILookAndFeel();
      
       //do the main frame
       frame = new JFrame("SeePod Lesson Manager");
       Container fcp = frame.getContentPane();
       fcp.setLayout(new BoxLayout(fcp, BoxLayout.X_AXIS));
       fcp.setPreferredSize(new Dimension(1200,800));
      
       frame.setBounds(seePodConfig.getScreenX(), seePodConfig.getScreenY(), seePodConfig.getScreenWidth(), seePodConfig.getScreenHeight());

       createMainMenuBar();

       seePodConfig.setAppFrame(frame);

       frame.addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e)
            { //Debug.debug("Main.windowClosing() - saving config file."); 
              seePodTagPanel.saveComboBoxItems();
              seePodConfig.saveConfigFile();
              seePodDatabasePanel.saveLessonDatabase();
              seePodLessonPanel.saveLessonSet();
            }
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
       });

       //now add the tag editor components in the first tab

       seePodTagPanel = new MP3EditorPanel(seePodConfig);

       //now the WLCP downloader components for the download tab

       seePodDownloadPanel = new WLCPPanel(seePodConfig);

       //now the lesson manager components for the lesson tab

       seePodLessonPanel = new LessonManagerPanel(seePodConfig);

       //now the Vocab and dictionary panel
     
       seePodVocabPanel = new VocabularyPanel(seePodConfig);

       //now the CPOD lesson database panel

       seePodDatabasePanel = new LessonDatabasePanel(seePodConfig);
       seePodLessonPanel.setDatabasePanel(seePodDatabasePanel);

       //now put it all into the tabbed pane
       JTabbedPane seePodTabbedPane = new javax.swing.JTabbedPane();

       seePodTabbedPane.addTab("Lesson Manager", null, seePodLessonPanel, "Create lesson sets and play audio.");
       seePodTabbedPane.addTab("WLCP Download", null, seePodDownloadPanel, "Download Lessons with WLCP, download ChinesePod RSS feed.");
       seePodTabbedPane.addTab("MP3 Tag Editor", null, seePodTagPanel, "View and edit MP3 Tags.");
       seePodTabbedPane.addTab("Vocabulary", null, seePodVocabPanel, "Dictionary and vocabulary lists.");
       seePodTabbedPane.addTab("Lesson Database", null, seePodDatabasePanel, "List of ChinesePod lessons");
       
       //now pack it pack it all up into the main frame"
       fcp.add(seePodTabbedPane);

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
       //fileMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
       mainMenu.add(fileMenu);
       
       //first popup menu items
       JMenuItem openItem = new JMenuItem("Open Lesson Set", KeyEvent.VK_O);
       openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
       //openItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       openItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            seePodLessonPanel.openLessonSet();
            //Debug.debug("Open menu item clicked.");
         }
       });
       fileMenu.add(openItem);

       JMenuItem saveItem = new JMenuItem("Save Lesson Set", KeyEvent.VK_S);
       saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
       //saveItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       saveItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            seePodLessonPanel.saveLessonSet();
            //Debug.debug("Save menu item clicked.");
         }
       });
       fileMenu.add(saveItem);
       
       JMenuItem delItem = new JMenuItem("Clear Lesson List", KeyEvent.VK_D);
       delItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
       //delItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       delItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            seePodLessonPanel.clearLessonSet();
            //Debug.debug("Clear menu item clicked.");
         }
       });
       fileMenu.add(delItem);
       
       fileMenu.addSeparator();

       JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
       exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
       //exitItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       exitItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            seePodTagPanel.saveComboBoxItems();
            seePodConfig.saveConfigFile();
            seePodDatabasePanel.saveLessonDatabase();
            seePodLessonPanel.saveLessonSet();
            //Debug.debug("Main: saved config file.");
            System.exit(0);
         }
       });
       fileMenu.add(exitItem);
       
       //second popup menu
       JMenu configMenu = new JMenu("Options");
       configMenu.setMnemonic(KeyEvent.VK_F2);
       //configMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
       mainMenu.add(configMenu);
       
       JMenuItem configItem = new JMenuItem("Settings", KeyEvent.VK_C);
       configItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
       //configItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       configItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            //Debug.debug("Option menu item clicked.");
            ConfigDialog cfd = new ConfigDialog(frame, seePodConfig, seePodTagPanel);
            cfd.showDialog();
         }
       });
       configMenu.add(configItem);
       
       JMenuItem colorItem = new JMenuItem("Colour Scheme", KeyEvent.VK_P);
       colorItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
       //colorItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       colorItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            ColourSchemeDialog csd = new ColourSchemeDialog(frame, laf, seePodConfig); 
            csd.showDialog();
         }
       });
       configMenu.add(colorItem);
 
       //third popup menu
       JMenu helpMenu = new JMenu("Help");
       helpMenu.setMnemonic(KeyEvent.VK_F3);
       //helpMenu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
       mainMenu.add(helpMenu);
       
       JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
       aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
       //aboutItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       aboutItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            //Debug.debug("About menu item clicked.");
            //a dialog with an image and close button
            AboutDialog abd = new AboutDialog(frame);
         }
       });
       helpMenu.add(aboutItem);
       
       JMenuItem guideItem = new JMenuItem("User Guide", KeyEvent.VK_U);
       guideItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
       //guideItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       guideItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            //display the simple PDF viewer with seepod-user-guide.pdf
            PDFViewerDialog guideDialog = new PDFViewerDialog();
            File guidePDF = new File(seePodConfig.getHomeDirectory() + File.separator + "userguide" + File.separator + "seepoduserguide-v-1.3.pdf");
            if (guidePDF.exists())
            {
               guideDialog.showDialog(guidePDF);
            }
            else
            {
               Debug.debug("Main.menu() - User Guide not found.");
            }
         }
       });
       helpMenu.add(guideItem);

       JMenuItem webItem = new JMenuItem("Website", KeyEvent.VK_W);
       webItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.ALT_MASK));
       //webItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
       webItem.addActionListener(new java.awt.event.ActionListener() 
       {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            openBrowser("http://www.seepod.net/userguide.html");
            //Debug.debug("Website menu item clicked.");
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
            openBrowser(Config.CPOD_URL);
            //Debug.debug("ChinesePod.com menu item clicked.");
         }
       });
       helpMenu.add(cPodItem);
       
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
