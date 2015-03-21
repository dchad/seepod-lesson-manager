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
 * Class  : WLCPController 
 * 
 * Description: Runs the WeLoveChinesePod python script to download lessons from ChinesePod.com
 * 
 * 
 */


package weepod;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class WLCPController extends JPanel implements GenericController
{
   
   private JButton wlcpDirButton;
   
   protected File curMP3File;
   
   private JTextField wlcpDirectory;
   
   private JFileChooser fc;
   
   private JCheckBox autoTag;
   private JCheckBox rssFeed;
   private JCheckBox rssSaveFeed;
   private JCheckBox rssAllLessons;
   private JCheckBox rssNewbie;
   private JCheckBox rssElementary;
   private JCheckBox rssIntermediate;
   private JCheckBox rssUpperIntermediate;
   private JCheckBox rssAdvanced;
   private JCheckBox rssMedia;
   private JCheckBox rssOther;
   
   private JRadioButton ignoreSentenceFiles;
   private JRadioButton deleteSentenceFiles;
   private JRadioButton archiveSentenceFiles;
   
   private JTextField trackNumber;
   private JTextField trackTitle;
   private JTextField albumName;
   private JTextField artistName;
   private JTextField year;
   private JTextField comment;
   
   private String wlcpPath;
   
   DownloadStatus dlStatus;
   Config seePodConfig;
   
   public WLCPController(Config spc, DownloadStatus dls) 
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(375,600));
      
      if (dls != null)
      {
         dlStatus = dls;
      }
      if (spc != null)
      {
         seePodConfig = spc;
         wlcpPath = seePodConfig.getWLCPDirectory();
      }
 
      //add WLCP directory selection field and button
      
      JPanel dirChooserPanel = new JPanel(false);
      dirChooserPanel.setBorder(BorderFactory.createTitledBorder("WLCP"));
      dirChooserPanel.setLayout(new BoxLayout(dirChooserPanel, BoxLayout.X_AXIS));
      //dirChooserPanel.setPreferredSize(new Dimension(300,40));
      
      JLabel dirLabel = new JLabel("WLCP Directory: ");
      dirChooserPanel.add(dirLabel);
      dirChooserPanel.add(Box.createHorizontalGlue());
      
      wlcpDirectory = new JTextField(30);
      wlcpDirectory.setMinimumSize(new Dimension(150,25));
      wlcpDirectory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
         wlcpPath = wlcpDirectory.getText();
         seePodConfig.setLessonDirectory(wlcpPath);
         dlStatus.printStatusLine("Set lesson directory: " + wlcpPath);
      }
      });
      dirChooserPanel.add(wlcpDirectory);
      dirChooserPanel.add(Box.createHorizontalGlue());
      dirChooserPanel.add(Box.createRigidArea(new Dimension(5,0)));
      wlcpDirectory.setText(seePodConfig.getWLCPDirectory());
      
      wlcpDirButton = new JButton();
      wlcpDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/weepod/images/folder_yellow.png"))); // NOI18N
      wlcpDirButton.setToolTipText("Select WLCP Directory");
      
      wlcpDirButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            
            try 
            {
               wlcpDirChooserButtonActionPerformed(evt);
            } catch (IOException ex) {
               Debug.debug("WLCPController: Error opening file dialog.");
            }
         }
      });
      
      dirChooserPanel.add(wlcpDirButton);
      add(dirChooserPanel);
      
      //now the wlcp download panel
      JPanel downloadPanel = new JPanel(false);
      //downloadPanel.setBorder(BorderFactory.createTitledBorder("Download Options"));
      downloadPanel.setLayout(new BoxLayout(downloadPanel, BoxLayout.Y_AXIS));
      downloadPanel.setPreferredSize(new Dimension(350,700));
      
      Box optionBox = Box.createHorizontalBox();
      optionBox.setBorder(BorderFactory.createTitledBorder("Download Options"));
      
      Box checkBox = Box.createVerticalBox();
      
      Box autoTagBox = Box.createHorizontalBox();
      autoTag = new JCheckBox("Auto tag WLCP files", false);
      autoTag.setToolTipText("Automatically add mp3 tags to expansion MP3 files after downloading.");
      autoTag.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setAutoTagWLCPFiles(autoTag.isSelected());
          //Debug.debug("WLCPController.WLCPController() - Set auto tag Wlcp files.");
      }});     
      autoTagBox.add(autoTag);
      autoTagBox.add(Box.createHorizontalGlue());
      checkBox.add(autoTagBox);
      
      Box rssFeedBox = Box.createHorizontalBox();
      rssFeed = new JCheckBox("Auto update RSS feed", false);
      rssFeed.setToolTipText("Automatically download your ChinesePod RSS lesson feed at startup.");
      rssFeed.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setAutoRSSFeed(rssFeed.isSelected());
          //Debug.debug("WLCPController.WLCPController() - Set auto rss feed.");
      }});
      rssFeedBox.add(rssFeed);
      rssFeedBox.add(Box.createHorizontalGlue());
      checkBox.add(rssFeedBox);
      
      Box rssSaveBox = Box.createHorizontalBox();
      rssSaveFeed = new JCheckBox("Auto save RSS feed", false);
      rssSaveFeed.setToolTipText("Automatically save your ChinesePod RSS lesson feed  as HTML a page.");
      rssSaveFeed.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setAutoSaveRSSFeed(rssSaveFeed.isSelected());
          //Debug.debug("WLCPController.WLCPController() - Set auto save rss feed.");
      }});
      rssSaveBox.add(rssSaveFeed);
      rssSaveBox.add(Box.createHorizontalGlue());
      checkBox.add(rssSaveBox);
     
      optionBox.add(checkBox);
      
      //now the radio buttons 
      
      ignoreSentenceFiles = new JRadioButton("Ignore sentence files");
      ignoreSentenceFiles.setActionCommand("Ignore");
      ignoreSentenceFiles.setToolTipText("Ignore all sentence files (rec-*.mp3) in lesson directories.");
      ignoreSentenceFiles.setSelected(true);
      
      deleteSentenceFiles = new JRadioButton("Delete sentence files");
      deleteSentenceFiles.setActionCommand("Delete");
      deleteSentenceFiles.setToolTipText("Delete all sentence files (rec-*.mp3).");
      
      archiveSentenceFiles = new JRadioButton("Archive sentence files");
      archiveSentenceFiles.setActionCommand("Archive");
      archiveSentenceFiles.setToolTipText("Place all sentence files (rec-*.mp3) in a ZIP archive.");
      
      ButtonGroup bGroup = new ButtonGroup();
      bGroup.add(ignoreSentenceFiles);
      bGroup.add(deleteSentenceFiles);
      bGroup.add(archiveSentenceFiles);
     
      
      //add in the callbacks here
      ignoreSentenceFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               radioButtonActionPerformed(evt);
            } catch (IOException ex) {
               Debug.debug("WLCPController: Error opening file dialog.");
            }
         }
      });
      deleteSentenceFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               radioButtonActionPerformed(evt);
            } catch (IOException ex) {
               Debug.debug("WLCPController: Error opening file dialog.");
            }
         }
      });
      archiveSentenceFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               radioButtonActionPerformed(evt);
            } catch (IOException ex) {
               Debug.debug("WLCPController: Error opening file dialog.");
            }
         }
      });
      
      Box radioButtonBox = Box.createVerticalBox();
      Box ignoreBox = Box.createHorizontalBox();
      ignoreBox.add(ignoreSentenceFiles);
      ignoreBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(ignoreBox);
      Box deleteBox = Box.createHorizontalBox();
      deleteBox.add(deleteSentenceFiles);
      deleteBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(deleteBox);
      Box archiveBox = Box.createHorizontalBox();
      archiveBox.add(archiveSentenceFiles);
      archiveBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(archiveBox);
      
      optionBox.add(radioButtonBox);
      
      Box lessonBox = Box.createVerticalBox();
      lessonBox.setBorder(BorderFactory.createTitledBorder("Lesson Options"));
      lessonBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      Box rssAllLessonsBox = Box.createHorizontalBox();
      rssAllLessons = new JCheckBox("Download all RSS items", true);
      rssAllLessons.setToolTipText("Include all lessons in the ChinesePod rss feed.");     
      rssAllLessons.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          rssAllLessonsActionPerformed();
      }});
      rssAllLessonsBox.add(rssAllLessons);
      rssAllLessonsBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssAllLessonsBox);
     
      Box rssNewbieBox = Box.createHorizontalBox();
      rssNewbie = new JCheckBox("Newbie lessons", false);
      rssNewbie.setToolTipText("Include Newbie lessons in rss feed.");
      rssNewbieBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssNewbieBox.add(rssNewbie);
      rssNewbieBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssNewbieBox);
      
      Box rssElementaryBox = Box.createHorizontalBox();
      rssElementary = new JCheckBox("Elementary lessons", false);
      rssElementary.setToolTipText("Include Elementary lessons in rss feed.");
      rssElementaryBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssElementaryBox.add(rssElementary);
      rssElementaryBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssElementaryBox);
     
      Box rssIntermediateBox = Box.createHorizontalBox();
      rssIntermediate = new JCheckBox("Intermediate lessons", false);
      rssIntermediate.setToolTipText("Include Intermediate lessons in rss feed.");
      rssIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssIntermediateBox.add(rssIntermediate);
      rssIntermediateBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssIntermediateBox);
      
      Box rssUpperIntermediateBox = Box.createHorizontalBox();
      rssUpperIntermediate = new JCheckBox("Upper-Intermediate lessons", false);
      rssUpperIntermediate.setToolTipText("Include Upper-Intermediate lessons in rss feed.");
      rssUpperIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssUpperIntermediateBox.add(rssUpperIntermediate);
      rssUpperIntermediateBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssUpperIntermediateBox);
     
      Box rssAdvancedBox = Box.createHorizontalBox();
      rssAdvanced = new JCheckBox("Advanced lessons", false);
      rssAdvanced.setToolTipText("Include Advanced lessons in rss feed.");
      rssAdvancedBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssAdvancedBox.add(rssAdvanced);
      rssAdvancedBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssAdvancedBox);
      
      Box rssMediaBox = Box.createHorizontalBox();
      rssMedia = new JCheckBox("Media lessons", false);
      rssMedia.setToolTipText("Include Media lessons in rss feed.");
      rssMediaBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssMediaBox.add(rssMedia);
      rssMediaBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssMediaBox);
      
      Box rssOtherBox = Box.createHorizontalBox();
      rssOther = new JCheckBox("Other lessons", false);
      rssOther.setToolTipText("Include Qing Wen, News & Features, etc.");
      rssOtherBox.add(Box.createRigidArea(new Dimension(25,0)));
      rssOtherBox.add(rssOther);
      rssOtherBox.add(Box.createHorizontalGlue());
      lessonBox.add(rssOtherBox);
      
      //checkBox.add(lessonBox);
      optionBox.add(Box.createRigidArea(new Dimension(0,5)));
      lessonBox.add(Box.createRigidArea(new Dimension(0,5)));
      downloadPanel.add(optionBox);
      downloadPanel.add(lessonBox);
      
      // the download configuration settings
      
      JPanel configPanel = new JPanel(false);
      configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
      configPanel.setPreferredSize(new Dimension(350, 500));
      configPanel.setBorder(BorderFactory.createTitledBorder("RSS Item Info"));
      
      
      Box labelBox = Box.createVerticalBox();
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Lesson Number:   "));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Lesson Title:"));
      labelBox.add(Box.createVerticalGlue());
      
      Box fieldBox = Box.createVerticalBox();
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));

      trackNumber = new JTextField(50); //JTextField(30);
      trackNumber.setEditable(true);
      trackNumber.setPreferredSize(new Dimension(225,25));
      fieldBox.add(trackNumber);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));

      trackTitle = new JTextField(); // JTextField(30);
      trackTitle.setEditable(true);
      trackTitle.setPreferredSize(new Dimension(225,25));
      fieldBox.add(trackTitle);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      
      // now the drop boxes and labels
      
      //labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Lesson Level:   "));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("URL:"));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Subtitle:"));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Summary:"));
      labelBox.add(Box.createVerticalGlue());
      
      configPanel.add(labelBox);
      
      albumName = new JTextField(50);
      albumName.setEditable(true);
      albumName.setPreferredSize(new Dimension(225,25));
      fieldBox.add(albumName);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
    
      artistName = new JTextField(50);
      artistName.setEditable(true);
      artistName.setPreferredSize(new Dimension(225,25));
      fieldBox.add(artistName);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      year = new JTextField(50);
      year.setEditable(true);
      year.setPreferredSize(new Dimension(225,25));
      fieldBox.add(year);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      comment = new JTextField(50);
      comment.setEditable(true);
      comment.setPreferredSize(new Dimension(225,25));
      fieldBox.add(comment);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      configPanel.add(fieldBox);
      configPanel.setPreferredSize(new Dimension(350, 300));
      configPanel.add(Box.createRigidArea(new Dimension(0,5)));
    
      
      downloadPanel.add(configPanel);
      //downloadPanel.add(Box.createRigidArea(new Dimension(0,5)));
      downloadPanel.add(Box.createVerticalGlue());
      
      add(downloadPanel);
      
      rssAllLessonsActionPerformed();
      getConfigItems();
   }
   
   private void getConfigItems()
   {
       rssFeed.setSelected(seePodConfig.getAutoRSSFeed());
       rssSaveFeed.setSelected(seePodConfig.getAutoSaveRSSFeed());
       autoTag.setSelected(seePodConfig.getAutoTagWLCPFiles());
              
       ignoreSentenceFiles.setSelected(seePodConfig.getIgnoreSentenceFiles());
       deleteSentenceFiles.setSelected(seePodConfig.getDeleteSentenceFiles());
       archiveSentenceFiles.setSelected(seePodConfig.getArchiveSentenceFiles());
   }
   
   private void rssAllLessonsActionPerformed()
   {
      if (rssAllLessons.isSelected())
      {
         rssNewbie.setSelected(true);
         rssElementary.setSelected(true);
         rssIntermediate.setSelected(true);
         rssUpperIntermediate.setSelected(true);
         rssAdvanced.setSelected(true);
         rssMedia.setSelected(true);
         rssOther.setSelected(true);
         
         rssNewbie.setEnabled(false);
         rssElementary.setEnabled(false);
         rssIntermediate.setEnabled(false);
         rssUpperIntermediate.setEnabled(false);
         rssAdvanced.setEnabled(false);
         rssMedia.setEnabled(false);
         rssOther.setEnabled(false);
      }
      else
      {
         rssNewbie.setSelected(false);
         rssElementary.setSelected(false);
         rssIntermediate.setSelected(false);
         rssUpperIntermediate.setSelected(false);
         rssAdvanced.setSelected(false);
         rssMedia.setSelected(false);
         rssOther.setSelected(false);
         
         rssNewbie.setEnabled(true);
         rssElementary.setEnabled(true);
         rssIntermediate.setEnabled(true);
         rssUpperIntermediate.setEnabled(true);
         rssAdvanced.setEnabled(true);
         rssMedia.setEnabled(true);
         rssOther.setEnabled(true);

      }
   }

   public void resetUI()
   {
      fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   }

   private void wlcpDirChooserButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      
        if (fc == null) 
        {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);   
        }

        //Show it.
        int returnVal = fc.showOpenDialog(this);
        
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File selectedFolder = fc.getSelectedFile();
            wlcpPath = selectedFolder.getPath();
            wlcpDirectory.setText(wlcpPath); 
            seePodConfig.setWLCPDirectory(wlcpPath);
        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("WLCPController.wlcpDirChooserButtonActionPerformed(): cancelled file selection.");
        }
        
   }
   
   private void radioButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      // not used
      String action = evt.getActionCommand();
      if (action.startsWith("Ignore"))
      {
         //update config, only need to react to a set action not the deselect action for a button group, Config does the rest.
         if (ignoreSentenceFiles.isSelected())
         {
            seePodConfig.setIgnoreSentenceFiles(true);
            seePodConfig.setDeleteSentenceFiles(false);
            seePodConfig.setArchiveSentenceFiles(false);
            //Debug.debug("WLCPController.radioButtonActionPerformed() - selected: ", action);
         }
      }
      else if (action.startsWith("Delete"))
      {
         if (deleteSentenceFiles.isSelected())
         {
            seePodConfig.setDeleteSentenceFiles(true);
            seePodConfig.setIgnoreSentenceFiles(false);
            seePodConfig.setArchiveSentenceFiles(false);
            //Debug.debug("WLCPController.radioButtonActionPerformed() - selected: ", action);
         }
      }
      else
      {
         if (archiveSentenceFiles.isSelected()) 
         {
            seePodConfig.setIgnoreSentenceFiles(false);
            seePodConfig.setDeleteSentenceFiles(false);
            seePodConfig.setArchiveSentenceFiles(true);
            //Debug.debug("WlCPController.radioButtonActionPerformed() - selected: ", action);
         }
      }
   }
   
   public synchronized Boolean getAllSelected()
   {
      return rssAllLessons.isSelected();
   }
   public synchronized Boolean getNewbieSelected()
   {
      return rssNewbie.isSelected();
   }
   public synchronized Boolean getElementarySelected()
   {
      return rssElementary.isSelected();
   }
   public synchronized Boolean getIntermediateSelected()
   {
      return rssIntermediate.isSelected();
   }
   public synchronized Boolean getUpperIntermediateSelected()
   {
      return rssUpperIntermediate.isSelected();
   }
   public synchronized Boolean getAdvancedSelected()
   {
      return rssAdvanced.isSelected();
   }
   public synchronized Boolean getMediaSelected()
   {
      return rssMedia.isSelected();
   }
   public synchronized Boolean getOtherSelected()
   {
      return rssOther.isSelected();
   }        

   public synchronized Boolean getIgnoreSentenceFilesSelected()
   {
      return ignoreSentenceFiles.isSelected();
   }        
      
   public synchronized Boolean getDeleteSentenceFilesSelected()
   {
      return deleteSentenceFiles.isSelected();
   } 
      
   public synchronized Boolean getArchiveSentenceFilesSelected()
   {
      return archiveSentenceFiles.isSelected();
   } 
      
   public synchronized Boolean getAutoTag()
   {
      return autoTag.isSelected();   
   }
   
   public synchronized Boolean getAutoStartRssFeed()
   {
      return rssFeed.isSelected();
   }
   
   public synchronized Boolean getAutoSaveRssFeed()
   {
      return rssSaveFeed.isSelected();
   }
   
   public void setRSSItemInfo(RSSLesson rssl)
   {
      //extract the data and display in relevant text fields
      if (rssl != null)
      {
         trackNumber.setText(rssl.getLessonNumber());
         trackTitle.setText(rssl.getLessonName());
         albumName.setText(rssl.getLessonLevel());
         artistName.setText(rssl.getLessonURL());
         year.setText(rssl.getLessonSubtitle());
         comment.setText(rssl.getLessonSummary());   
      }
   }

   public synchronized void printStatusMessage(String msg) 
   {
      dlStatus.printStatusLine(msg);
   }

 
  public synchronized void wlcpDownloadFinished()
  {
     if (Config.DEBUG)
        Debug.debug("WLCPController.wlcpDownloadFinished() - starting autotag...");

     WLCPXmlPanel xp = seePodConfig.getXMLPanel();
     xp.resetList();

     if (autoTag.isSelected())
     {
         //start the wlcp tag updater thread
         seePodConfig.setIncludeSubdirectories(true);
         Thread t = new Thread(new WLCPTagUpdater(this, seePodConfig));
         t.start();
     }
  }
 
  
}