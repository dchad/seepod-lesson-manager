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
 * Class  : LessonDatabasePanel
 * 
 * Description: Display a list of ChinesePod lessons.
 * 
 * 
 */

package seepodlessonmanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class LessonDatabasePanel extends JPanel
{
   private HashMap dbItemList;
   private Vector dbStringList;
   private File dbFile;
   private JList dbList;
   private DefaultListModel dbListModel;
   private BufferedReader inputStream;
   private JCheckBox cpodAllLessons;
   private JCheckBox cpodNewbie;
   private JCheckBox cpodElementary;
   private JCheckBox cpodIntermediate;
   private JCheckBox cpodUpperIntermediate;
   private JCheckBox cpodAdvanced;
   private JCheckBox cpodMedia;
   private JCheckBox cpodAutoUpdate;
   private JCheckBox cpodAutoSave;
   private JPopupMenu popup;
   private int index;
   private boolean listChanged;
   private Config seePodConfig;
   private StatusPanel statusMessages;

    public LessonDatabasePanel(Config spc)
    {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(1000,800));

        seePodConfig = spc;
        
        dbItemList = new HashMap<String, LessonDatabaseItem>();
        dbStringList = new Vector();
        dbListModel = new DefaultListModel();
        dbList = new JList(dbListModel);
        LessonListRenderer listRend = new LessonListRenderer();
        dbList.setCellRenderer(listRend);   
        
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setMaximumSize(new Dimension(600,1000)); 
      
        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setBorder(BorderFactory.createTitledBorder("Level Selection"));
        topButtonPanel.setLayout(new BorderLayout());
        topButtonPanel.setMaximumSize(new Dimension(600,300));

      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) //add double click to play lesson audio, and right click for popup menu with play, read pdf, delete etc...
         {
      
               if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
               {
                  index = dbList.locationToIndex(e.getPoint());
               }
               else if (e.getClickCount() == 2)
               {
                  index = dbList.locationToIndex(e.getPoint());
               }
  
         }
         
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = dbList.locationToIndex(e.getPoint());
               if (dbListModel.size() > 0)
               {
                     dbList.setSelectedIndex(index);
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }               
            }
        }
      
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = dbList.locationToIndex(e.getPoint());
               if (dbListModel.size() > 0)
               {
                     dbList.setSelectedIndex(index);
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }             
            }
        }
     
      };
      dbList.addMouseListener(mouseListener);
      
      Box checkBox = Box.createVerticalBox();      
      
      Box cpodAllLessonsBox = Box.createHorizontalBox();
      cpodAllLessons = new JCheckBox("All lessons", true);
      cpodAllLessons.setToolTipText("Include all lessons in the list.");
      
      cpodAllLessons.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          cpodAllLessonsActionPerformed();
      }});
      
      cpodAllLessonsBox.add(cpodAllLessons);
      cpodAllLessonsBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodAllLessonsBox);
      
      
      Box cpodNewbieBox = Box.createHorizontalBox();
      cpodNewbie = new JCheckBox("Newbie ", false);
      cpodNewbie.setToolTipText("Include Newbie lessons in list.");
      cpodNewbieBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodNewbieBox.add(cpodNewbie);
      cpodNewbieBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodNewbieBox);
      
      Box cpodElementaryBox = Box.createHorizontalBox();
      cpodElementary = new JCheckBox("Elementary ", false);
      cpodElementary.setToolTipText("Include Elementary lessons in list.");
      cpodElementaryBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodElementaryBox.add(cpodElementary);
      cpodElementaryBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodElementaryBox);
     
      Box cpodIntermediateBox = Box.createHorizontalBox();
      cpodIntermediate = new JCheckBox("Intermediate ", false);
      cpodIntermediate.setToolTipText("Include Intermediate lessons in list.");
      cpodIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodIntermediateBox.add(cpodIntermediate);
      cpodIntermediateBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodIntermediateBox);
      
      Box cpodUpperIntermediateBox = Box.createHorizontalBox();
      cpodUpperIntermediate = new JCheckBox("Upper-Intermediate ", false);
      cpodUpperIntermediate.setToolTipText("Include Upper-Intermediate lessons in list.");
      cpodUpperIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodUpperIntermediateBox.add(cpodUpperIntermediate);
      cpodUpperIntermediateBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodUpperIntermediateBox);
     
      Box cpodAdvancedBox = Box.createHorizontalBox();
      cpodAdvanced = new JCheckBox("Advanced ", false);
      cpodAdvanced.setToolTipText("Include Advanced lessons in list.");
      cpodAdvancedBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodAdvancedBox.add(cpodAdvanced);
      cpodAdvancedBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodAdvancedBox);
      
      Box cpodMediaBox = Box.createHorizontalBox();
      cpodMedia = new JCheckBox("Media ", false);
      cpodMedia.setToolTipText("Include Media lessons in list.");
      cpodMediaBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodMediaBox.add(cpodMedia);
      cpodMediaBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodMediaBox);

      Box buttonBox = Box.createHorizontalBox();

      JButton refreshButton = new JButton("Refresh");
      refreshButton.setToolTipText("Reload the lesson list.");
      refreshButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            reloadDatabase();
         }
      });
      buttonBox.add(Box.createRigidArea(new Dimension(25,0)));
      buttonBox.add(refreshButton);
      buttonBox.add(Box.createHorizontalGlue());

      checkBox.add(Box.createRigidArea(new Dimension(0,10)));
      checkBox.add(buttonBox);
      checkBox.add(Box.createRigidArea(new Dimension(0,10)));
      
      topButtonPanel.add(checkBox);

      controlPanel.add(topButtonPanel);
        
      JPanel dbOptionsPanel = new JPanel();
      dbOptionsPanel.setLayout(new BoxLayout(dbOptionsPanel, BoxLayout.Y_AXIS));
      dbOptionsPanel.setBorder(BorderFactory.createTitledBorder("Lesson List Options"));
      dbOptionsPanel.setMaximumSize(new Dimension(600,300));     
      
      Box cpodAutoUpdateBox = Box.createHorizontalBox();
      cpodAutoUpdate = new JCheckBox("Auto update lesson list", false);
      cpodAutoUpdate.setToolTipText("Automatically update the lesson list from ChinesePod.");
      //cpodAutoUpdateBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodAutoUpdate.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setAutoUpdateDatabase(cpodAutoUpdate.isSelected());
      }});     
      cpodAutoUpdateBox.add(cpodAutoUpdate);
      cpodAutoUpdateBox.add(Box.createHorizontalGlue());
      dbOptionsPanel.add(cpodAutoUpdateBox);
      //get the option state from config
      
      Box cpodAutoSaveBox = Box.createHorizontalBox();
      cpodAutoSave = new JCheckBox("Auto save lesson list", false);
      cpodAutoSave.setToolTipText("Automatically save changes to the lesson list.");
      //cpodAutoSaveBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodAutoSave.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setAutoSaveDatabase(cpodAutoSave.isSelected());
      }});     
      cpodAutoSaveBox.add(cpodAutoSave);
      cpodAutoSaveBox.add(Box.createHorizontalGlue());
      dbOptionsPanel.add(cpodAutoSaveBox);
      //get the option state from config

      controlPanel.add(dbOptionsPanel);
      
      JPanel dbUpdatePanel = new JPanel();
      dbUpdatePanel.setLayout(new BoxLayout(dbUpdatePanel, BoxLayout.X_AXIS));
      dbUpdatePanel.setBorder(BorderFactory.createTitledBorder("Lesson List Update"));
      dbUpdatePanel.setMaximumSize(new Dimension(600,300));     
      
      JButton updateButton = new JButton("Update");
      updateButton.setToolTipText("Update the lesson list from the ChinesePod website.");
      updateButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            startDatabaseUpdater();
         }
      });   
      
      JButton saveButton = new JButton("Save");
      saveButton.setToolTipText("Save changes to the lesson list.");
      saveButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveLessonDatabase();
         }
      });         
      
      dbUpdatePanel.add(Box.createHorizontalGlue());
      dbUpdatePanel.add(updateButton);
      dbUpdatePanel.add(Box.createHorizontalGlue());
      dbUpdatePanel.add(saveButton);
      dbUpdatePanel.add(Box.createHorizontalGlue());
      dbUpdatePanel.add(Box.createRigidArea(new Dimension(0,50)));

      controlPanel.add(dbUpdatePanel);

      statusMessages = new StatusPanel();
      controlPanel.add(statusMessages);
      
      JPanel dbListPanel = new JPanel();
      dbListPanel.setBorder(BorderFactory.createTitledBorder("ChinesePod Lessons"));
      dbListPanel.setLayout(new BorderLayout());

      JScrollPane scroller = new JScrollPane();
      scroller.getViewport().add(dbList);
      dbListPanel.add(scroller);

      add(dbListPanel);
      add(controlPanel);

      loadCPODDatabase();

      cpodAllLessonsActionPerformed();

      createPopupMenu();

      listChanged = false;

      getConfigItems();
    }

   public void resetUI()
   {
       createPopupMenu();
   }

   private void getConfigItems()
   {
       cpodAutoSave.setSelected(seePodConfig.getAutoSaveDatabase());
       cpodAutoUpdate.setSelected(seePodConfig.getAutoUpdateDatabase());
       if (cpodAutoUpdate.isSelected())
       {
           startDatabaseUpdater();
       }
   }

   private void createPopupMenu()
   {
      JMenuItem menuItem;
      popup = new JPopupMenu();
      
      menuItem = new JMenuItem("Bookmark Lesson");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            //get all audio files for the selected lesson and add to playlist
            if (dbListModel.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_BOOKMARKED);
            }
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Mark Lesson For Review");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (dbListModel.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_REVIEW);
            }
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Mark Lesson As Completed");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (dbListModel.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_COMPLETED);
            }
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Un-Mark Lesson");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (dbListModel.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_NONE);
            }
         }
      });
      popup.add(menuItem);


   }

   public synchronized void updateLessonDatabase(RSSLesson rssl)
   {
      //do a runnable to add new item to the hashmap 
        final LessonDatabaseItem ldi = new LessonDatabaseItem(rssl.getLessonNumber(), DateUtils.now("dd MMMMM yyyy"), rssl.getLessonLevel(), rssl.getRSSTitle(), SPConstants.LESSON_STATUS_NONE);
        final String disval = ldi.toString();
        
        if (dbItemList.containsKey(ldi.getLessonNumber()))
        {
            //Debug.debug("LessonDatabasePanel.updateLessonDatabase() - already has lesson: ", disval);
            return;  //bail out, lesson is already in the database
        }

        final Runnable listUpdater = new Runnable()
        {
                @Override
                public void run()
                {
                        dbItemList.put(ldi.getLessonNumber(), ldi);
                        dbListModel.addElement(disval);
                        dbStringList.add(disval);
                        statusMessages.printStatusLine("Added lesson: " + ldi.toString());
                        //Debug.debug("LessonDatabasePanel.updateLessonDatabase() - added lesson: ", ldi.toString());
                }
        };
        try
        {
            SwingUtilities.invokeAndWait(listUpdater);
        } catch (InterruptedException ex) {
           Debug.debug("LessonDatabasePanel.updateLessonDatabase() - interrupted exception. "); 
        } catch (InvocationTargetException ex) {
           Debug.debug("LessonDatabasePanel.updateLessonDatabase() - invocation target exception. ");  
        }
        listChanged = true;
   }

   public void updateLessonStatus(String lessonNumber, int lessonStatus)
   {
      if (dbListModel.size() > 0)
      {
         if (dbItemList.containsKey(lessonNumber))
         {
            LessonDatabaseItem ldi = (LessonDatabaseItem) dbItemList.get(lessonNumber);
            ldi.setLessonStatus(lessonStatus);
         }
         else
         {
            Debug.debug("LessonDatabasePanel.updateLessonStatus() - lesson not found in database: ", lessonNumber);
            return;
         }
         
         //have to loop through the dblistmodel and find the relevant culprit! 

         for (int i = 0; i < dbListModel.size(); i++)
         {
            String disval = (String) dbListModel.get(i);
            String lnum = disval.substring(1, 5); 
            if (lessonNumber.equalsIgnoreCase(lnum))
            {
               //found the culprit
               dbListModel.setElementAt(disval, i); 
               listChanged = true;
            }
         }
      }       
   }
   
   private void updateLessonStatus(int lessonStatus)
   {
      if (dbListModel.size() > 0)
      {

         String disval = (String) dbListModel.get(index);
         String lnum = disval.substring(1, 5);
         if (dbItemList.containsKey(lnum))
         {
            LessonDatabaseItem ldi = (LessonDatabaseItem) dbItemList.get(lnum);
            ldi.setLessonStatus(lessonStatus);
         }
         else
         {
            Debug.debug("LessonDatabasePanel.updateLessonStatus() - lesson not found in database: ", disval);
         }
         
         dbListModel.setElementAt(disval, index);
         listChanged = true;

      }
   }

   public void saveLessonDatabase()
   {
      if (listChanged)
      {
          saveCPODDatabase();
      }
   }

    public HashMap getLessonList()
    {
        return dbItemList;
    }

    private void reloadDatabase()
    {
       dbListModel.removeAllElements();
       int size = dbStringList.size();
       for (int i = 0; i < size; i++)
       {
          String lesson = (String) dbStringList.get(i);
          if (cpodAllLessons.isSelected())
          {
              dbListModel.addElement(lesson);
              continue;
          }
          if (cpodNewbie.isSelected())
          {
              if (lesson.contains("Newbie -"))
              {
                  dbListModel.addElement(lesson);
                  continue;
              }
          }
          if (cpodElementary.isSelected())
          {
              if (lesson.contains("Elementary -"))
              {
                  dbListModel.addElement(lesson);
                  continue;
              }
          }
          if (cpodIntermediate.isSelected())
          {
              if (lesson.contains("Intermediate -"))
              {
                  dbListModel.addElement(lesson);
                  continue;
              }
          }
          if (cpodUpperIntermediate.isSelected())
          {
              if (lesson.contains("Upper-Intermediate -"))
              {
                  dbListModel.addElement(lesson);
                  continue;
              }
          }  
          if (cpodAdvanced.isSelected())
          {
              if (lesson.contains("Advanced -"))
              {
                  dbListModel.addElement(lesson);
                  continue;
              }
          }
          if (cpodMedia.isSelected())
          {
              if (lesson.contains("Media -"))
              {
                  dbListModel.addElement(lesson);
              }
          }          
       }
    }
    
   private void loadCPODDatabase()
   {
      SAXBuilder builder = new SAXBuilder();
      Document config;

      //dbItemList.removeAllElements();
      //dbListModel.removeAllElements();
      
      dbFile = new File(seePodConfig.getDatabaseDirectory() + File.separator + Config.SP_LESSON_DATABASE_FILE);

      try 
      {

         config = builder.build(dbFile);

         Element root = config.getRootElement();
 
         List nodeList = root.getChildren();

         Iterator iter = nodeList.iterator();
         while (iter.hasNext())
         {
            Element lessonItem = (Element) iter.next();
          
            if (lessonItem != null)
            {
                  Element lnum = lessonItem.getChild("lesson_number");
                  Element ldate = lessonItem.getChild("lesson_date");
                  Element llevel = lessonItem.getChild("lesson_level");
                  Element ltitle = lessonItem.getChild("lesson_title");
                  Element lstatus = lessonItem.getChild("lesson_status");

                  String lessonNumber = "NULL";
                  String lessonDate = "NULL";
                  String lessonLevel = "NULL";
                  String lessonTitle = "NULL";
                  int lessonStatus;
                  if (lnum != null)
                  {
                      lessonNumber = lnum.getText();
                      //Debug.debug("LessonDatabaseItem() = lesson: ", lessonNumber);
                  }
                  if (ldate != null)
                  {
                      lessonDate = ldate.getText();
                  }
                  if (llevel != null)
                  {
                      lessonLevel = llevel.getText();
                  }
                  if (ltitle != null)
                  {
                      lessonTitle = ltitle.getText();
                  }
                  if (lstatus != null)
                  {
                      lessonStatus = new Integer(lstatus.getText());
                      //Debug.debug("LessonDatabaseItem() = status: ", lessonStatus);
                  }
                  else
                  {
                      lessonStatus = SPConstants.LESSON_STATUS_NONE;
                  }
                  LessonDatabaseItem vi = new LessonDatabaseItem(lessonNumber, lessonDate, lessonLevel, lessonTitle, lessonStatus);
                  dbItemList.put(lessonNumber, vi);
                  dbStringList.addElement(vi.toString());
            }
         }
         
      } catch (JDOMException ex) {
         Debug.debug("RssFeed.loadCPODXML - JDOM exception.");
      } catch (IOException ex) {
         Debug.debug("RssFeed.loadCPODXML - IO exception.");
      }

      int size = dbStringList.size();

      if (size > 0)
      {
          Collections.sort(dbStringList, String.CASE_INSENSITIVE_ORDER);
      }
        //now loop through the string list add to a DatabaseItem vector and save the list
        
      for (int i = 0; i < size; i++)
      {
         dbListModel.addElement(dbStringList.get(i));
      }          
      return;       
   }

   private synchronized void saveCPODDatabase()
   {
      File lessonDBSaveFile = new File(seePodConfig.getDatabaseDirectory() + File.separator + Config.SP_LESSON_DATABASE_FILE);
      
      Element root = new Element("SeePodLessonDatabase");
      Document newLessonDB = new Document(root);
         
      for (int i = 0; i < dbStringList.size(); i++)
      {
          Element linfo = new Element("lesson_item");
          Element lnum = new Element("lesson_number");
          Element ldate = new Element("lesson_date");
          Element llevel = new Element("lesson_level");
          Element ltitle = new Element("lesson_title");
          Element lstatus = new Element("lesson_status");

          String disval = (String) dbStringList.get(i);
          String lnumber = disval.substring(1, 5);
        
          LessonDatabaseItem ldi;
          if (dbItemList.containsKey(lnumber))
          {
             ldi = (LessonDatabaseItem) dbItemList.get(lnumber);
          }
          else
          {
             Debug.debug("LessonDatabasePanel.saveCPODDatabase() - lesson not found in database: ", disval);
             continue;
          }

          lnum.setText(ldi.getLessonNumber());
          ldate.setText(ldi.getLessonDate());
          llevel.setText(ldi.getLessonLevel());
          ltitle.setText(ldi.getLessonTitle());
          lstatus.setText(String.valueOf(ldi.getLessonStatus()));

          linfo.addContent(lnum);
          linfo.addContent(ldate);
          linfo.addContent(ltitle);
          linfo.addContent(llevel);
          linfo.addContent(lstatus);

          root.addContent(linfo);
      }
      
      try 
      {
          PrintWriter wout = new PrintWriter(lessonDBSaveFile, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!!
          serializer.output(newLessonDB, wout);
          wout.close();
      }
      catch (IOException e) {
             Debug.debug("LessonSetPanel.saveLessonSets() - Could not write lesson DB file.");
      }
      statusMessages.printStatusLine("Saved lesson list...");
      listChanged = false;
      
      return;
   }

    private void createCPODDatabase()
    {
       dbFile = new File("cpod-newbie-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("new");
       }
       dbFile = new File("cpod-elementary-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("ele");
       }
       dbFile = new File("cpod-intermediate-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("int");
       }
       dbFile = new File("cpod-upper-intermediate-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("upp");
       }
       dbFile = new File("cpod-advanced-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("adv");
       }
       dbFile = new File("cpod-media-list.txt");
       if (dbFile.exists())
       {
           loadCPODList("med");
       }
               
       Collections.sort(dbStringList, String.CASE_INSENSITIVE_ORDER);
        //now loop through the string list add to a DatabaseItem vector and save the list
        int size = dbStringList.size();
        for (int i = 0; i < size; i++)
        {
            dbListModel.addElement(dbStringList.get(i));
        }
        
    }

    private void loadCPODList(String llevel)
    {
      try
      {
            try
            {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(dbFile), "UTF-8"));
            } catch (FileNotFoundException ex) {
                Debug.debug("VocabularyList.loadCSV() - file not found.", ex);
            }
      } catch (UnsupportedEncodingException ex) {
            Debug.debug("VocabularyList.loadCSV() - unsupported encoding.", ex);
        return;
      }

      //read in the lesson list if any
      String entry;
      try
      {
         while ((entry = inputStream.readLine()) != null)
         {
            //Debug.debug("VocabularyList.loadCSV() - read line: ", entry);
            String[] breakdown = entry.split("\t");
            //StringTokenizer st = new StringTokenizer(entry, ",");
            if (breakdown.length > 1)
            {
               String inumber = breakdown[0].trim();
               //Integer lnum = new Integer(inumber.trim());
               //if (lnum <= 500)
               //{
               //    continue;
               //}
               String idate = "2009";
               String ilevel = llevel;
               String ititle = breakdown[1];

               LessonDatabaseItem vi = new LessonDatabaseItem(inumber, idate, ilevel, ititle);
               dbItemList.put(inumber, vi);
               dbStringList.add(vi.toString());
               //dbListModel.addElement(vi.toString()); 
            }
         }
      } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCPDOList() - Could not read line from buffered file reader.", ex);
         return;
      }

      if (inputStream != null)
      {
         try
         {
            inputStream.close();
         } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCSV() - Counld not close buffered file reader.", ex);
         }
      }               
    }
    
   private void loadCSV()
   {

      try
      {
            try
            {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(dbFile), "UTF-8"));
            } catch (FileNotFoundException ex) {
                Debug.debug("VocabularyList.loadCSV() - file not found.", ex);
            }
      } catch (UnsupportedEncodingException ex) {
            Debug.debug("VocabularyList.loadCSV() - unsupported encoding.", ex);
        return;
      }

      //read in the lesson list if any
      String entry;
      try
      {
         while ((entry = inputStream.readLine()) != null)
         {
            //Debug.debug("VocabularyList.loadCSV() - read line: ", entry);
            String[] breakdown = entry.split(",");
            //StringTokenizer st = new StringTokenizer(entry, ",");
            if (breakdown.length > 3)
            {
               String inumber = breakdown[0];
               String idate = breakdown[1];
               String ilevel = breakdown[2];
               String ititle = breakdown[3];
               if (breakdown.length > 4) //stupid line format has commas in some titles
               {
                   ititle = entry.substring(20); //go twenty chars in and suck it up
               }
               
               LessonDatabaseItem vi = new LessonDatabaseItem(inumber, idate, ilevel, ititle);
               dbItemList.put(inumber, vi);
               dbListModel.addElement(vi.toString()); 
            }
         }
      } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCSV() - Could not read line from buffered file reader.", ex);
         return;
      }

      if (inputStream != null)
      {
         try
         {
            inputStream.close();
         } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCSV() - Counld not close buffered file reader.", ex);
         }
      }       
   }
   
   private void cpodAllLessonsActionPerformed()
   {
      if (cpodAllLessons.isSelected())
      {
         cpodNewbie.setSelected(true);
         cpodElementary.setSelected(true);
         cpodIntermediate.setSelected(true);
         cpodUpperIntermediate.setSelected(true);
         cpodAdvanced.setSelected(true);
         cpodMedia.setSelected(true);
         
         
         cpodNewbie.setEnabled(false);
         cpodElementary.setEnabled(false);
         cpodIntermediate.setEnabled(false);
         cpodUpperIntermediate.setEnabled(false);
         cpodAdvanced.setEnabled(false);
         cpodMedia.setEnabled(false);
         
      }
      else
      {
         cpodNewbie.setSelected(false);
         cpodElementary.setSelected(false);
         cpodIntermediate.setSelected(false);
         cpodUpperIntermediate.setSelected(false);
         cpodAdvanced.setSelected(false);
         cpodMedia.setSelected(false);
        
         
         cpodNewbie.setEnabled(true);
         cpodElementary.setEnabled(true);
         cpodIntermediate.setEnabled(true);
         cpodUpperIntermediate.setEnabled(true);
         cpodAdvanced.setEnabled(true);
         cpodMedia.setEnabled(true);
        

      }
   }

public class LessonListRenderer extends DefaultListCellRenderer
{
   //private ImageIcon defaultImage;
   private ImageIcon lessonBookmarkedIcon;
   private ImageIcon lessonReviewIcon;
   private ImageIcon lessonCompletedIcon;
   private ImageIcon lessonDefaultIcon;   
   private Font listFont;

   public LessonListRenderer()
   {
       super();

       java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/folder_music.png");
       lessonDefaultIcon = new ImageIcon(imageURL);
       imageURL = Splash.class.getResource("/seepodlessonmanager/images/bookmark_add.png");
       lessonBookmarkedIcon = new ImageIcon(imageURL);
       imageURL = Splash.class.getResource("/seepodlessonmanager/images/reload.png");
       lessonReviewIcon = new ImageIcon(imageURL);      
       imageURL = Splash.class.getResource("/seepodlessonmanager/images/action_success.png");
       lessonCompletedIcon = new ImageIcon(imageURL);
      
       listFont = new Font("Dialog", Font.BOLD, 16);

   }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Get icon to use for the list item value
        int status;
        //String disval = (String) value; DEPRECATED: returns dodgy values?
        String disval = (String) dbStringList.get(index);
        String lnum = disval.substring(1, 5);
        
        if (dbItemList.containsKey(lnum))
        {
           LessonDatabaseItem ldi = (LessonDatabaseItem) dbItemList.get(lnum);
           status = ldi.getLessonStatus();
        }
        else
        {
           status = SPConstants.LESSON_STATUS_NONE;
           Debug.debug("LessonDatabasePanel.getListCellRendererComponent() - lesson not found in database: ", disval);
        }

        switch (status)
        {
            case SPConstants.LESSON_STATUS_NONE: label.setIcon(lessonDefaultIcon); break;
            case SPConstants.LESSON_STATUS_BOOKMARKED: label.setIcon(lessonBookmarkedIcon); break;
            case SPConstants.LESSON_STATUS_REVIEW: label.setIcon(lessonReviewIcon); break;
            case SPConstants.LESSON_STATUS_COMPLETED: label.setIcon(lessonCompletedIcon); break;
        }

        label.setFont(listFont);

        return label;
    }
}   

private void startDatabaseUpdater()
{
   statusMessages.printStatusLine("Starting lesson list update...");
   String rss = seePodConfig.getRSSFeedURL();
   if ((rss != null) && (rss.startsWith("http://chinesepod.com/")))
   {
      DatabaseUpdater dbu = new DatabaseUpdater();
      Thread t = new Thread(dbu);
      t.start();
   }
   else
   {
      JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Could not download RSS Feed: " + seePodConfig.getRSSFeedURL(), "Download Lesson List Error", JOptionPane.INFORMATION_MESSAGE);
      statusMessages.printStatusLine("Could not start RSS feed, invalid URL: " + rss);
   }
}

private synchronized void connectionError()
{
        final Runnable connError = new Runnable()
        {
                @Override
                public void run()
                {
                   JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Could not download RSS Feed: " + seePodConfig.getRSSFeedURL(), "Download Lesson List Error", JOptionPane.INFORMATION_MESSAGE);
                }
        };
        try
        {
            SwingUtilities.invokeAndWait(connError);
        } catch (InterruptedException ex) {
           Debug.debug("LessonDatabasePanel.connectionError() - interrupted exception. ");
        } catch (InvocationTargetException ex) {
           Debug.debug("LessonDatabasePanel.connectionError() - invocation target exception. ");  
        }
   
}

private class DatabaseUpdater implements Runnable
{
   SAXBuilder saxBuilder;
   Document rssDoc;
    
   public DatabaseUpdater()
   {
      saxBuilder = new SAXBuilder();        
   }
    
   public void downloadRSSFeed(String aURL) 
   {
     DataInputStream di = null;
     Boolean conerror = false;
     statusMessages.printStatusLine("Downloading lesson list...");
     try 
     {
       URL url = new URL(aURL);
       URLConnection urlConnection = url.openConnection();
       urlConnection.connect();
       di = new DataInputStream(urlConnection.getInputStream());
       
       //File testFile = new File("database" + File.separator + "seepodRSSFeed.xml");
       
       rssDoc = saxBuilder.build(di);
       statusMessages.printStatusLine("Finished lesson list download...");
       di.close();
       updateDatabase();
       statusMessages.printStatusLine("Lesson list udpate completed.");
     }
     catch (Exception ex) {
        conerror = true;
        Debug.debug("DatabaseUpdater.downloadRSSFeed() - could not download feed: ", aURL);
     }

     if (conerror)
     {
        
     }

     if (cpodAutoSave.isSelected())
     {
        saveCPODDatabase();
     }
   }
   
   private void updateDatabase()
   {
         Element root = rssDoc.getRootElement();
         Element channel = root.getChild("channel");
         List nodeList = channel.getChildren();

         Iterator iter = nodeList.iterator();
         while (iter.hasNext())
         {
            Element rssItem = (Element) iter.next();
          
            if (rssItem != null)
            {
               if (!rssItem.getName().equalsIgnoreCase("item"))
               {
                   //Debug.debug("DatabaseUpdater.updateDatabase() = node: ", rssItem.getName());
                   continue;
               }

               Element ltitle = rssItem.getChild("title");
               Element llink = rssItem.getChild("link");
               Element lencoded = rssItem.getChild("encoded", rssItem.getNamespace("content")); // have to have the prefix for JDOM
               Element lguid = rssItem.getChild("guid");
               Element lsummary = rssItem.getChild("summary", rssItem.getNamespace("itunes"));
               Element lsubtitle = rssItem.getChild("subtitle", rssItem.getNamespace("itunes"));

               String lessonTitle = "NULL";
               String lessonLink = "NULL";
               String lessonEncoded = "NULL";
               String lessonGUID = "NULL";
               String lessonSummary = "NULL";
               String lessonSubtitle = "NULL";

               if (ltitle != null)
               {
                  lessonTitle = ltitle.getText();
                  //Debug.debug("DatabaseUpdater.updateDatabase() = lesson: ", lessonTitle);
               }
               if (llink != null)
               {
                  lessonLink = llink.getText();
               }
               if (lencoded != null)
               {
                  lessonEncoded = lencoded.getText();
                  //Debug.debug("DatabaseUpdater.updateDatabase() - encoded: ", lessonEncoded);
               }
               if (lguid != null)
               {
                  lessonGUID = lguid.getText();
               }
               if (lsummary != null)
               {
                  lessonSummary = lsummary.getText();
                  //Debug.debug("DatabaseUpdater.updateDatabase() - summary: ", lessonSummary);
               }
               if (lsubtitle != null)
               {
                  lessonSubtitle = lsubtitle.getText();
                  //Debug.debug("DatabaseUpdater.updateDatabase() - subtitle: ", lessonSubtitle);
               }

               if (lessonTitle.startsWith("Newbie -")
                       || lessonTitle.startsWith("Elementary -")
                       || lessonTitle.startsWith("Intermediate -")
                       || lessonTitle.startsWith("Upper Intermediate")
                       || lessonTitle.startsWith("Advanced -")
                       || lessonTitle.startsWith("Media -"))
               {
                  RSSLesson rssl = new RSSLesson(lessonTitle, lessonGUID, lessonEncoded, lessonSubtitle, lessonSummary);
                  updateLessonDatabase(rssl);
               }
            }
         }

      //TESTING ONLY
      //File rssSaveFile = new File("database" + File.separator + "seepodRSSFeed.xml");
      //try
      //{
      //    PrintWriter wout = new PrintWriter(rssSaveFile, "UTF-8"); //line output
      //    XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!!
      //    serializer.output(rssDoc, wout);
      //    wout.close();
      //}
      //catch (IOException e) {
      //       Debug.debug("LessonSetPanel.saveLessonSets() - Could not write lesson DB file.");
      //}
   }
   

    @Override
    public void run()
    {
        downloadRSSFeed(seePodConfig.getRSSFeedURL());
    }
   
}


}
