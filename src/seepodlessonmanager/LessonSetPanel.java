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
 * Class  : LessonSetPanel
 * 
 * Description: Displays a list of lesson sets stored in XML format.
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class LessonSetPanel extends JPanel
{
   private LessonManagerControl lessonControllerPanel;
   private LessonListPanel lessonManagerPanel;
   private DefaultListModel fileListModel;
   private JList setList;
   private Vector lessonSetList;
   private Config seePodConfig;
   private JPopupMenu popup;
   private String selectedLessonSet;
   private File lessonSetFile;
   private int index;
   
   public LessonSetPanel(LessonManagerControl lmc, LessonListPanel lml, Config spc)
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(400,700));
      //setMinimumSize(new Dimension(700,600));
      setBorder(BorderFactory.createTitledBorder("Lesson Sets"));

      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("LessonSetPanel() - Config is null.");
         return;
      }
      if (lmc != null)
      {
         lessonControllerPanel = lmc;
      }
      else
      {
         Debug.debug("LessonSetPanel() - LessonController is null.");
         return;
      }
      if (lml != null)
      {
         lessonManagerPanel = lml;
      }
      else
      {
         Debug.debug("LessonSetPanel() - LessonManagerList is null.");
         return;
      }
            
      fileListModel = new DefaultListModel();
      
      lessonSetList = new Vector();
      
      setList = new JList(fileListModel); 
      
      createPopupMenu();
      
      MouseListener mouseListener = new MouseAdapter() //add lessons in set to the lesson list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {    
               if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
               {
                  index = setList.locationToIndex(e.getPoint());
                  if (lessonSetList.size() > 0)
                  {
                     lessonSetFile = (File) lessonSetList.get(index);
                     selectedLessonSet = lessonSetFile.getName();
                     if (lessonSetFile.exists())
                     {
                        lessonManagerPanel.loadLessonSet(lessonSetFile);
                     }
                     else
                     {
                        lessonControllerPanel.printStatusMessage("Lesson set file not found..." + lessonSetFile.getAbsolutePath());
                        //Debug.debug("LessonSetPanel() - Lesson set file not found.", lessonSetFile.getAbsolutePath());
                     }
                  }
               }
         }
         
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = setList.locationToIndex(e.getPoint());
               if (lessonSetList.size() > 0)
               {
                     lessonSetFile = (File) lessonSetList.get(index);
                     selectedLessonSet = lessonSetFile.getName();
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }
            }
        }
      
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = setList.locationToIndex(e.getPoint());
               if (lessonSetList.size() > 0)
               {
                     lessonSetFile = (File) lessonSetList.get(index);
                     selectedLessonSet = lessonSetFile.getName();
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }
            }
        }
     
      };
      setList.addMouseListener(mouseListener);
      
      // Lastly, put the JList into a JScrollPane.
      JScrollPane scrollpane = new JScrollPane();
      scrollpane.getViewport().add(setList);
      
      add(BorderLayout.CENTER, scrollpane);
      add(Box.createRigidArea(new Dimension(0,10)));
      
      loadLessonSets();

   }

   private void createPopupMenu()
   {
      JMenuItem menuItem;
      popup = new JPopupMenu();
      menuItem = new JMenuItem("Add All Lessons to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            //update the lesson list and add to playlist
            if (lessonSetFile.exists())
            {
               lessonManagerPanel.loadLessonSet(lessonSetFile);
               lessonManagerPanel.addLessonSetToPlayList();
            }

         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Delete Lesson Set");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            //delete the lesson set file
            if (JOptionPane.showConfirmDialog(seePodConfig.getAppFrame(), "Delete lesson set?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
               if (lessonSetFile.exists())
               {
                  lessonSetFile.delete();
               }
               fileListModel.remove(index);
               lessonSetList.remove(index);
               saveLessonSets();
               lessonControllerPanel.printStatusMessage("Deleted lesson set...");
            }
         }
      });
      popup.add(menuItem);

   }

   public void resetUI()
   {
      createPopupMenu();
   }

   public void addLessonSet(File lessonSetFile)
   {
      Boolean setFound = false;
      String filename = lessonSetFile.getName();
      if (lessonSetFile.exists())
      {
         for (int i = 0; i < fileListModel.size(); i++)
         {
             String listEntry = (String) fileListModel.get(i);
             if (filename.equals(listEntry))
             {
                setFound = true;
                //Debug.debug("LessonSetPanel.addLesson() - found lesson set: ", filename + " - " + listEntry);
             }
         }
         if (!setFound)
         {
            lessonSetList.add(lessonSetFile); //file path
            fileListModel.addElement(lessonSetFile.getName()); //lesson set file name
            saveLessonSets();
            if (Config.DEBUG)
               Debug.debug("LessonSetPanel.addLesson() - received lesson set: ", lessonSetFile.getName());
         }
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("LessonSetPanel.addLesson() - Lesson set file does not exist.");
      }
      

   }
   
   private void clearLessonSet() //TODO: delete file?
   {
      lessonSetList.removeAllElements();  //clear the lists
      fileListModel.removeAllElements();
      lessonControllerPanel.printStatusMessage("Cleared lesson set.");
   }
   
   private void loadLessonSets()
   {
      File lessonSetOpenFile = new File(seePodConfig.getHomeDirectory() + File.separator + Config.SP_LESSON_SETS_FILE);
      
      if (!lessonSetOpenFile.exists())
      {
         return; //bail out - no lesson sets created yet!
      }
      
      SAXBuilder builder = new SAXBuilder();
      Document lessonSets;

      // Get the root element and iterate through the tree setting the lesson name and file path lists
      
      lessonSetList.removeAllElements();  //clear the lists
      fileListModel.removeAllElements();
      
      try 
      {
         lessonSets = builder.build(lessonSetOpenFile);
         Element root = lessonSets.getRootElement();
         
         List nodeList = root.getChildren();
         int len = nodeList.size();
 
         for (int i = 0; i < len; i++)
         {
            Element tmpNode = root.getChild("LessonSet"+i);
            Element filePathNode = tmpNode.getChild("LessonSetFilePath");
            String filePath = filePathNode.getText();
            File tmpFile = new File(filePath);
            lessonSetList.add(tmpFile);                    //file path
            fileListModel.addElement(tmpFile.getName());   //lesson item to display
         }
         
         
      } catch (JDOMException ex) {
         Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
      }
          
      return;
   }
   
   private void saveLessonSets()
   {
      File lessonSetSaveFile = new File(seePodConfig.getHomeDirectory() + File.separator + Config.SP_LESSON_SETS_FILE);
            //write out the config file with JDOM
      
      Element root = new Element("SeePodLessonSetDatabase");
      Document newLessonSet = new Document(root); 
         
      for (int i = 0; i < lessonSetList.size(); i++)
      {
          Element tmpLeaf = new Element("LessonSet"+i);
          Element filePath = new Element("LessonSetFilePath");
          File tmpFile = (File) lessonSetList.get(i);
          filePath.setText(tmpFile.getAbsolutePath());
          tmpLeaf.addContent(filePath);
          root.addContent(tmpLeaf);
      }
      
      try 
      {
          PrintWriter wout = new PrintWriter(lessonSetSaveFile, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!!
          serializer.output(newLessonSet, wout);
          wout.close();
          
          lessonControllerPanel.printStatusMessage("Saved lesson set file: " + lessonSetSaveFile.getName());
      }
      catch (IOException e) {
          if (Config.DEBUG)
             Debug.debug("LessonSetPanel.saveLessonSets() - Could not write configuration file.");
      }
      
      return;
   }
   
}