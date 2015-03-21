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
 * Class  : LessonListPanel
 * 
 * Description: Displays a lesson list stored in XML format.
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class LessonListPanel extends JPanel
{
   private LessonManagerControl lessonControllerPanel;
   private LessonSetPanel lessonSetPanel;
   private DefaultListModel fileListModel;
   private JList lessonList;
   private Vector cpodLessonList;
   private Vector lessonStatusList;
   private JFileChooser fc;
   private Config seePodConfig;
   private static JPopupMenu popup;
   private MediaPlayerPanel mp3PlayerPanel;
   private File selectedFile;
   private File lessonSetFile;
   private ChinesePodLesson cpodLesson;
   private int index;
   private PDFViewerDialog viewer;
   private Boolean listChanged;
   private JLabel lessonSetName;
   private static LessonDatabasePanel dbPanel;
   
   public LessonListPanel(LessonManagerControl lmc, Config spc)
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(400,700));
      setBorder(BorderFactory.createTitledBorder("ChinesePod Lessons"));

      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("LessonListPanel() - Config is null.");
      }
      if (lmc != null)
      {
         lessonControllerPanel = lmc;
      }
      else
      {
         Debug.debug("LessonListPanel() - Lesson controller panel is null.");
      }

      listChanged = false;
      lessonSetFile = null;

      viewer = new PDFViewerDialog();
      seePodConfig.setPDFFrame(viewer);
      
      fileListModel = new DefaultListModel();
      
      cpodLessonList = new Vector();
      lessonStatusList = new Vector();
      
      lessonList = new JList(fileListModel); 
      LessonListRenderer listRend = new LessonListRenderer();

      lessonList.setCellRenderer(listRend);      
      
      createPopupMenu();

      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) //add double click to play lesson audio, and right click for popup menu with play, read pdf, delete etc...
         {
      
               if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
               {
                  index = lessonList.locationToIndex(e.getPoint());
                  if (cpodLessonList.size() > 0)
                  {
                     String cpodLesson = (String) cpodLessonList.get(index);
                     selectedFile = new File(cpodLesson);
                     if (selectedFile.exists())
                     {
                        lessonControllerPanel.setSelectedLesson(selectedFile);
                     }
                  }
             
               }
               else if (e.getClickCount() == 2)
               {
                  index = lessonList.locationToIndex(e.getPoint());
                  if (cpodLessonList.size() > 0)
                  {
                     String cpodLesson = (String) cpodLessonList.get(index);
                     selectedFile = new File(cpodLesson);
                     if (selectedFile.exists())
                     {
                        lessonControllerPanel.setSelectedLesson(selectedFile);
                        addAllLessonFilesToPlayList();
                     }
                  }
               }
  
         }
         
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = lessonList.locationToIndex(e.getPoint());
               if (cpodLessonList.size() > 0)
               {
                     String cpodLesson = (String) cpodLessonList.get(index);
                     lessonList.setSelectedIndex(index);
                     selectedFile = new File(cpodLesson);
                     if (selectedFile.exists())
                     {
                        lessonControllerPanel.setSelectedLesson(selectedFile);
                     }
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }               
            }
        }
      
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               index = lessonList.locationToIndex(e.getPoint());
               if (cpodLessonList.size() > 0)
               {
                     String cpodLesson = (String) cpodLessonList.get(index);
                     lessonList.setSelectedIndex(index);
                     selectedFile = new File(cpodLesson);
                     if (selectedFile.exists())
                     {
                        lessonControllerPanel.setSelectedLesson(selectedFile);
                     }
                     popup.show(e.getComponent(), e.getX(), e.getY());
               }             
            }
        }
     
      };
      lessonList.addMouseListener(mouseListener);
      
      // Lastly, put the JList into a JScrollPane.
      JScrollPane scrollpane = new JScrollPane();
      scrollpane.getViewport().add(lessonList);
     
      
      JButton updateLessonsButton = new JButton("Scan");
      updateLessonsButton.setToolTipText("Scan directory tree for lessons to add to a lesson set.");
      updateLessonsButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            updateLessonDB();
         }
      });
      
      JButton loadLessonsButton = new JButton("Open");
      loadLessonsButton.setToolTipText("Load a lesson set from a file.");
      loadLessonsButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            openLessonSet();
         }
      });
   
      JButton saveLessonsButton = new JButton("Save");
      saveLessonsButton.setToolTipText("Save the lesson set to a file.");
      saveLessonsButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveLessonSet();
         }
      });
      
      JButton clearLessonsButton = new JButton("Clear");
      clearLessonsButton.setToolTipText("Remove all lessons from this lesson set.");
      clearLessonsButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            clearLessonSet();
         }
      });
      
      Box rssButtonBox = Box.createHorizontalBox();
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(updateLessonsButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(loadLessonsButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(saveLessonsButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(clearLessonsButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      
      Box lessonNameBox = Box.createHorizontalBox();
      lessonSetName = new JLabel("Lesson Set: (None)");
      lessonNameBox.add(lessonSetName);
      lessonNameBox.add(Box.createHorizontalGlue());
      
      //add(Box.createRigidArea(new Dimension(0,10)));
      add(lessonNameBox);
      add(Box.createRigidArea(new Dimension(0,10)));
      add(BorderLayout.CENTER, scrollpane);
      add(Box.createRigidArea(new Dimension(0,10)));
      add(rssButtonBox);
      add(Box.createRigidArea(new Dimension(0,5)));

   }

   public void resetUI()
   {
      createPopupMenu();
      fc = new JFileChooser(".");
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files","xml");
      fc.setFileFilter(filter);

      viewer = new PDFViewerDialog();
      seePodConfig.setPDFFrame(viewer);
   }

   public void setDatabasePanel(LessonDatabasePanel ldp)
   {
      dbPanel = ldp; 
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
            if (cpodLessonList.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_BOOKMARKED);
            }
            //lessonControllerPanel.printStatusMessage("Added all lesson MP3 files to playlist...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Mark Lesson For Review");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_REVIEW);
            }
            //lessonControllerPanel.printStatusMessage("Added all lesson MP3 files to playlist...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Mark Lesson As Completed");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_COMPLETED);
            }
            //lessonControllerPanel.printStatusMessage("Added lesson audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Un-Mark Lesson");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                updateLessonStatus(SPConstants.LESSON_STATUS_NONE);
            }
            //lessonControllerPanel.printStatusMessage("Added lesson audio...");
         }
      });
      popup.add(menuItem);
      popup.addSeparator();
      menuItem = new JMenuItem("Add All Lesson Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                addAllLessonFilesToPlayList();
            }
            //lessonControllerPanel.printStatusMessage("Added expansion audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Add Lesson Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                addLessonFileToPlaylist();
            }
            //lessonControllerPanel.printStatusMessage("Added lesson audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Add Expansion Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                addExpansionFileToPlaylist();
            }
            //lessonControllerPanel.printStatusMessage("Added expansion audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Add Dialogue Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
                addDialogFileToPlaylist();
            }
            //lessonControllerPanel.printStatusMessage("Added dialog audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Add Review Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
               addReviewFileToPlaylist();
            }
            //lessonControllerPanel.printStatusMessage("Added review audio...");
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Add Vocab Audio to playlist");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
               addVocabFileToPlaylist();
            }
            //lessonControllerPanel.printStatusMessage("Added vocab audio...");
         }
      });
      popup.add(menuItem);
      popup.addSeparator();
      menuItem = new JMenuItem("Open Simplified PDF");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() == 0)
            {
               return;
            }
            if (selectedFile.exists())
            {
               ChinesePodLesson cpl = new ChinesePodLesson(selectedFile);
               if (cpl != null)
               {
                  String pdfFileName = cpl.getLessonItem(Config.LESSON_SIMP_PDF_FILE_KEY);

                  File pdfFile = new File(pdfFileName);
                  if (pdfFile.exists())
                  {
                     lessonControllerPanel.printStatusMessage("Opening simplified PDF...");
                     viewer.showDialog(pdfFile);
                     //JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Test Message!!!", "Test", JOptionPane.ERROR_MESSAGE);
                  }
                  else
                  {
                     lessonControllerPanel.printStatusMessage("Simplified PDF does not exist.");
                     Debug.debug("LessonListPanel.actionPerformed() - PDF file does not exist: ", pdfFileName);
                  }
               }
            }
            else
            {
               Debug.debug("LessonListPanel.actionPerformed() - ChinesePod lesson = null.");
            }
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Open Traditional PDF");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() == 0)
            {
               return;
            }
            if (selectedFile.exists())
            {
               ChinesePodLesson cpl = new ChinesePodLesson(selectedFile);
               if (cpl != null)
               {
                  String pdfFileName = cpl.getLessonItem(Config.LESSON_TRAD_PDF_FILE_KEY);

                  File pdfFile = new File(pdfFileName);
                  if (pdfFile.exists())
                  {
                     lessonControllerPanel.printStatusMessage("Opening traditional PDF...");
                     viewer.showDialog(pdfFile);
                  }
                  else
                  {
                     lessonControllerPanel.printStatusMessage("Traditional PDF does not exist.");
                     Debug.debug("LessonListPanel.actionPerformed() - PDF file does not exist: ", pdfFileName);
                  }
               }
            }
            else
            {
               Debug.debug("LessonListPanel.actionPerformed() - ChinesePod lesson = null.");
            }
         }
      });
      popup.add(menuItem);
      popup.addSeparator();
      menuItem = new JMenuItem("Remove Lesson From Set");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (cpodLessonList.size() > 0)
            {
               fileListModel.remove(index);
               cpodLessonList.remove(index);
               lessonStatusList.remove(index);
               listChanged = true;
               String tmp = lessonSetName.getText();
               if (!tmp.contains("Modified"))
               {
                  lessonSetName.setText(tmp + " (Modified)");
               }
               lessonControllerPanel.printStatusMessage("Removed lesson from set...");
            }
         }
      });
      popup.add(menuItem);

   }

   public synchronized void addLesson(final String spl, final String fp)
   {
      
      Boolean addLesson = false;
      
      if (spl != null)
      {

         if (lessonControllerPanel.getAllSelected())
         {
            addLesson = true;
         }
         else
         {
            if (spl.contains("- Newbie -"))
            {
               if (lessonControllerPanel.getNewbieSelected())
                  addLesson = true;
            }
            else if (spl.contains("- Elementary -"))
            {
               if (lessonControllerPanel.getElementarySelected())
                  addLesson = true;
            }
            else if (spl.contains("- Intermediate -") )
            {
               if (lessonControllerPanel.getIntermediateSelected())
                  addLesson = true;
            }
            else if (spl.contains("- Upper-Intermediate -"))
            {
               if (lessonControllerPanel.getUpperIntermediateSelected())
                  addLesson = true;
            }
            else if (spl.contains("- Advanced -"))
            {
               if (lessonControllerPanel.getAdvancedSelected())
                  addLesson = true;
            }
            else if (spl.contains("- Media -") )
            {
               if (lessonControllerPanel.getMediaSelected())
                  addLesson = true;
            }
            else
            {
               if (lessonControllerPanel.getOtherSelected())
               {
                  addLesson = true;
               }
            }
         }
         if (addLesson)
         {
             
            // need to do this on the EDT to prevent listUI array out of bounds exception
           final Runnable listUpdater = new Runnable()
           {
                    @Override
                    public void run()
                    {
                            fileListModel.addElement(spl);
                            cpodLessonList.add(fp);
                            lessonStatusList.add(new Integer(SPConstants.LESSON_STATUS_NONE));
                    }
            };
            try
            {
                SwingUtilities.invokeAndWait(listUpdater);
            } catch (InterruptedException ex) {
                Logger.getLogger(LessonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(LessonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            }
          }
          else
          {
             if (Config.DEBUG)
                Debug.debug("LessonListPanel.addLesson() - Lesson object is null.");
          }


   }
   
   private void updateLessonStatus(int lessonStatus)
   {
      if (lessonStatusList.size() > 0)
      {
         lessonStatusList.setElementAt(new Integer(lessonStatus), index);
         String lesson = (String) fileListModel.get(index); 
         fileListModel.setElementAt(lesson, index);
         listChanged = true;
         String tmp = lessonSetName.getText();
         if (!tmp.contains("Modified"))
         {
            lessonSetName.setText(tmp + " (Modified)");
         }
         if (dbPanel != null)
         {
            String lnum = lesson.substring(1, 5);
            dbPanel.updateLessonStatus(lnum, lessonStatus); 
         }
         //TODO: update the lesson status in the database panel
      }
   }
   
   private void addAllLessonFilesToPlayList()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addLessonFiles(cpodLesson);
   }
   
   private void addLessonFileToPlaylist()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addMP3File(cpodLesson);
   }   

   private void addExpansionFileToPlaylist()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addExpansionMP3File(cpodLesson);
   }

   private void addDialogFileToPlaylist()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addDialogMP3File(cpodLesson);
   }
   
   private void addReviewFileToPlaylist()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addReviewMP3File(cpodLesson);
   }
   
   private void addVocabFileToPlaylist()
   {
      cpodLesson = new ChinesePodLesson(selectedFile);
      mp3PlayerPanel.addVocabMP3File(cpodLesson);
   }
      
   private void updateLessonDB()
   {
      clearLessonSet(); //first clear any lesson already in the list
      
      LessonScanner lessonScan = new LessonScanner(this, seePodConfig, lessonControllerPanel);
      Thread t = new Thread(lessonScan);
      t.start();
   }
   
   public void clearLessonSet()
   {
      cpodLessonList.removeAllElements();  //clear the lists
      fileListModel.removeAllElements();
      lessonStatusList.removeAllElements();
      lessonSetFile = null;
      listChanged = true;
      lessonSetName.setText("Lesson Set: (None)");
      //lessonControllerPanel.printStatusMessage("Cleared lesson set.");
   }
   
   private void saveLessonSet(File lessonFile)
   {
      //write out the config file with JDOM
      lessonSetFile = lessonFile;
      
      Element root = new Element("SeePodLessonSet");
      Document newLessonSet = new Document(root); 
         
      for (int i = 0; i < cpodLessonList.size(); i++)
      {
          //ChinesePodLesson cpl = new ChinesePodLesson((new File((String)cpodLessonList.get(i))));
          Element tmpLeaf = new Element("Lesson"+i);
          Element lessonName = new Element("LessonName");
          String lesson = fileListModel.get(i).toString();
          lessonName.setText(lesson);
          Element filePath = new Element("FilePath");
          filePath.setText(cpodLessonList.get(i).toString());
          Element lessonStatus = new Element("LessonStatus");
          lessonStatus.setText(Integer.toString((Integer)lessonStatusList.get(i)));
          tmpLeaf.addContent(lessonName);
          tmpLeaf.addContent(filePath);
          tmpLeaf.addContent(lessonStatus);
          root.addContent(tmpLeaf);
          //lessonControllerPanel.printStatusMessage("Added: " + lesson);
      }
      
      try 
      {
          PrintWriter wout = new PrintWriter(lessonSetFile, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!
          serializer.output(newLessonSet, wout);
          wout.close();
          lessonSetPanel.addLessonSet(lessonSetFile);
          lessonControllerPanel.printStatusMessage("Saved file: " + lessonSetFile.getName());
      }
      catch (IOException e) {
          if (Config.DEBUG)
             Debug.debug("LessonListPanel.saveLessonSet() - Could not write configuration file.");
      }
      listChanged = false;
      lessonSetName.setText("Lesson Set: " + lessonSetFile.getName());
   }
 
   private File lessonFileChooser(int chooserType)
   {
        File chosenFile = null;
        
        if (fc == null) 
        {
            fc = new JFileChooser(".");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);   
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files","xml");
            fc.setFileFilter(filter);
            fc.setDialogTitle("Save Lesson Set?");
        }

        int returnVal = 0;
        if (chooserType == Config.LESSON_SAVE)
        {
           returnVal = fc.showSaveDialog(this);
        }
        else
        {
           returnVal = fc.showOpenDialog(this);
        }
        
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            chosenFile = fc.getSelectedFile();
            String fileName = chosenFile.getAbsolutePath();
            if (!fileName.endsWith("xml"))
            {
               fileName = fileName.concat(".xml");
               chosenFile = new File(fileName);
               //Debug.debug("LessonListPanel.lessonFileChooser() - Selected file: ", fileName);
            }

            if (chooserType == Config.LESSON_SAVE)
            {
               lessonControllerPanel.printStatusMessage("Saved file: " + chosenFile.getName());
            }
            else
            {
               lessonControllerPanel.printStatusMessage("Opened file: " + chosenFile.getName());
            }
            
        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("LessonListPanel.lessonFileChooser(): cancelled file selection.");
        }
        return chosenFile;
   }
   
   public void loadLessonSet(File lessonFile)
   {
      SAXBuilder builder = new SAXBuilder();
      Document config;

      // Get the root element and iterate through the tree setting the lesson name and file path lists

      clearLessonSet();
      lessonSetFile = lessonFile;
      listChanged = false;
      lessonSetName.setText("Lesson Set: " + lessonSetFile.getName());
      
      try 
      {
         config = builder.build(lessonSetFile);
         Element root = config.getRootElement();
         
         List nodeList = root.getChildren();
         int len = nodeList.size();
 
         for (int i = 0; i < len; i++)
         {
            Element tmpNode = root.getChild("Lesson"+i);
            if (tmpNode != null)
            {
               Element filePathNode = tmpNode.getChild("FilePath");
               Element lessonNameNode = tmpNode.getChild("LessonName");
               Element lessonStatusNode = tmpNode.getChild("LessonStatus");
               String filePath = filePathNode.getText();
               String lessonName = lessonNameNode.getText();
               if (lessonStatusNode == null)
               {
                   lessonStatusList.add(new Integer(SPConstants.LESSON_STATUS_NONE));
               }
               else
               {
                   lessonStatusList.add(new Integer(lessonStatusNode.getText()));
               }
               cpodLessonList.add(filePath);           //file path
               fileListModel.addElement(lessonName);   //lesson item to display
            }
         }
         
         
      } catch (JDOMException ex) {
         Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
      }
          
      return;
   }
   
   public void setLessonSetPanel(LessonSetPanel lsl)
   {
      if (lsl != null)
      {
         lessonSetPanel = lsl;
      }
      else
      {
         Debug.debug("LessonListPanel() - Lesson set panel is null.");
      }      
   }
   
   public void setMediaPlayerPanel(MediaPlayerPanel mp3pp)
   {
      if (mp3pp != null)
      {
         mp3PlayerPanel = mp3pp;
      }
      else
      {
         Debug.debug("LessonListPanel() - Media player panel is null.");
      }            
   }
   
   public void openLessonSet()
   {
      File f = lessonFileChooser(Config.LESSON_OPEN);
      if (f != null)
      {
         loadLessonSet(f);
      }      
   }
   
   public void saveLessonSet()
   {
      if (!listChanged)
      {
         return; //bail out, nothing to do
      }

      if (lessonSetFile == null)
      {
         File f = lessonFileChooser(Config.LESSON_SAVE);
         if (f != null)
         {
            if (f.exists())
            {
               int n = JOptionPane.showConfirmDialog(this, "Replace: " + f.getName(), "Lesson Set Save", JOptionPane.YES_NO_OPTION);
               if (n == JOptionPane.YES_OPTION)
               {
                   saveLessonSet(f);
               }
            }
            else
            {
                saveLessonSet(f);
            }
         }
      }
      else
      {
          saveLessonSet(lessonSetFile);
      }
   }
   
   public void addLessonSetToPlayList()
   {
      for (int i = 0; i < cpodLessonList.size(); i++)
      {
         File cplFile = new File((String)cpodLessonList.get(i));
         if (cplFile.exists())
         {
            ChinesePodLesson cpl = new ChinesePodLesson(cplFile);
            mp3PlayerPanel.addLessonFiles(cpl);
         }
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
      
       listFont = new Font("Dialog", Font.BOLD, 14);

   }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Get icon to use for the list item value
        int status;
        if (lessonStatusList.size() > 0)
        {
           status = (Integer) lessonStatusList.get(index);
        }
        else
        {
           status = SPConstants.LESSON_STATUS_NONE;
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

    public void addRSSItem(RSSLesson rssItem)
    {
        //rssItems.add(rssItem);
        //may have to insert at 0 when db implemented
    }
}

}