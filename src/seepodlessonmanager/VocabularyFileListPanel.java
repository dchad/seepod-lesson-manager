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
 * Date   : 7/May/2009
 * Class  : VocabularyListPanel
 * 
 * Description: Vocabulary file list panel.
 * 
 */
package seepodlessonmanager;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class VocabularyFileListPanel extends JPanel 
{
   private Config seePodConfig;
   private JList vocabFileList;
   private DefaultListModel vocabListModel;
   private Vector vocabFiles;
   private int index;
   private VocabularyListPanel vocabListPanel;
   private JFileChooser fc;

   public VocabularyFileListPanel(Config spc, VocabularyListPanel vlp)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("VocabularyListPanel() - Config is null.");
      }

      vocabListPanel = vlp;

      setBorder(BorderFactory.createTitledBorder("Vocabulary Lists"));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(300,900));
      
      vocabListModel = new DefaultListModel();
      vocabFileList = new JList(vocabListModel);
      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1)
            {
               if (vocabListModel.size() > 0)
               {
                  index = vocabFileList.locationToIndex(e.getPoint());
                  //now tell the vocab panel to show the list
                  vocabListPanel.loadVocabList((File)vocabFiles.get(index));
               }
            }
          }
         
      };
      vocabFileList.addMouseListener(mouseListener);
      
      index = 0;
      vocabFiles = new Vector();

      JScrollPane listScroller = new JScrollPane();
      listScroller.getViewport().add(vocabFileList);

      JButton importVocab = new JButton("Import");
      importVocab.setSize(40, 30);
      importVocab.setToolTipText("Import vocabulary file...");
      importVocab.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            vocabFileChooserButtonActionPerformed(evt);
         }
      });
      
      JButton removeList = new JButton("Delete");
      removeList.setSize(40, 30);
      removeList.setToolTipText("Delete vocabulary file...");
      removeList.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            deleteVocabFileButtonActionPerformed(evt);
         }
      });
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(importVocab);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(removeList);
      buttonBox.add(Box.createHorizontalGlue());
      
      add(Box.createRigidArea(new Dimension(0,5)));
      add(listScroller);
      add(Box.createRigidArea(new Dimension(0,5)));
      add(buttonBox);
      add(Box.createRigidArea(new Dimension(0,5)));

      loadVocabLists();
   }

   private void loadVocabLists()
   {
      vocabListModel.removeAllElements();
      vocabFiles.removeAllElements();

      File dir = new File(seePodConfig.getDatabaseDirectory());
      if (dir.exists() && dir.isDirectory())
      {
         VocabFileFilter ff = new VocabFileFilter();
         File[] fileList = dir.listFiles(ff);
         for (int i = 0; i < fileList.length; i++)
         {
            //Debug.debug("VocabularyListPanel.loadVocabLists() - file: ", fileList[i].getAbsoluteFile());
            String fname = fileList[i].getName();
            if (!(fname.equalsIgnoreCase(Config.SP_RSS_FEED_LIST) || fname.equalsIgnoreCase(Config.SP_LESSON_DATABASE_FILE)))
            {
               vocabListModel.addElement(fileList[i].getName());
               vocabFiles.add(fileList[i]);
               //Debug.debug("VocabularyListPanel.loadVocabLists() - added file: ", fileList[i].getAbsoluteFile());
            }
         }
      }
      else
      {
         Debug.debug("VocabularyListPanel.loadVocabLists() - Database directory does not exist: ", dir.getAbsoluteFile());
      }
   }
   
  private class VocabFileFilter implements java.io.FileFilter
  {
     @Override
     public boolean accept(File f)
     {
        String filename = f.getName().toLowerCase();
        return (filename.endsWith(".txt") || filename.endsWith(".xml") || filename.endsWith(".csv"));

     }
  }
  
   private void vocabFileChooserButtonActionPerformed(java.awt.event.ActionEvent evt)
   {
        BufferedReader inputStream = null;
        PrintWriter pw = null;
        File outputFile = null;
        
        if (fc == null) 
        {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);   
        }

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File selectedFile = fc.getSelectedFile();
            
            //open and copy file to database directory
            if (selectedFile.exists())
            {
                outputFile = new File(seePodConfig.getDatabaseDirectory() + File.separator + selectedFile.getName());
                if (!outputFile.exists())
                {
                    Boolean ioerror = false;
                    try
                    {
                        outputFile.createNewFile();
                    } catch (IOException ex) {
                       ioerror = true;
                       Debug.debug("VocabularyFileListPanel.vocabFileChooserButtonActionPerformed() - io exception.", ex); 
                    }
                    if (ioerror)
                    {
                       JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Could not import vocabulary file: " + outputFile.getAbsolutePath());
                       return;
                    }
                }
                try
                {
                        try
                        {
                            inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "UTF-8"));
                            pw = new PrintWriter(outputFile, "UTF-8");
                        } catch (FileNotFoundException ex) {
                            Debug.debug("VocabularyFileListPanel.vocabFileChooserButtonActionPerformed() - file not found.", ex);
                            return;
                        }
                } catch (UnsupportedEncodingException ex) {
                     Debug.debug("VocabularyFileListPanel.vocabFileChooserButtonActionPerformed() - unsupported encoding.", ex);
                     return;
                }

                  //read in the lesson list if any
                  String entry;
                  try
                  {
                     while ((entry = inputStream.readLine()) != null)
                     {
                        pw.println(entry);
                     }
                  } catch (IOException ex) {
                     Debug.debug("VocabularyFileListPanel.vocabFileChooserButtonActionPerformed() - Could not read line from buffered file reader.", ex);
                     JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Could not import vocabulary file: " + outputFile.getAbsolutePath());
                     return;
                  }

                  if (pw != null)
                  {
                     pw.close();
                  }

                  if (inputStream != null)
                  {
                     try
                     {
                        inputStream.close();
                     } catch (IOException ex) {
                        Debug.debug("VocabularyList.loadTXT() - Counld not close buffered file reader.", ex);
                     }
                  }   
                loadVocabLists();
            }
            else
            {
                JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Vocabulary list file not found!");
            }

        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("VocabularyFileListPanel.vocabFileChooserButtonActionPerformed(): cancelled file selection.");
        }
        
   }
   
   private void deleteVocabFileButtonActionPerformed(java.awt.event.ActionEvent evt)
   {
      //popup a confirm dialog with the selected file, if ok then delete file and reload file list
       if (vocabFiles.size() > 0)
       {
          File deleteFile = (File) vocabFiles.get(index);
          int result = JOptionPane.showConfirmDialog(seePodConfig.getAppFrame(), "Delete vocabulary file: " + deleteFile.getName(), "File Delete", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION)
          {
             deleteFile.delete();
             loadVocabLists();
          }
       }
   }

   public void resetUI()
   {
       fc = new JFileChooser();
       fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
   }
}
