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
 * Class  : MP3Editor 
 * 
 * Description: Uses the MyID3 library to read and write mp3 tags in files selected by the user.
 * 
 * 
 */


package seepodlessonmanager;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class MP3Editor extends JPanel implements GenericController
{
   protected File curMP3File;
   private File currentDirectory;
   private FileList mp3List;
   private DirectoryTree dirTree;
   private String curDirectory;
   private Config seePodConfig;
   
   private JTextField lessonDirectory;
   
   private JComboBox id3TrackNumberField;
   private JComboBox id3TrackTitleField;
   private JComboBox id3TrackAlbumField;
   private JComboBox id3AlbumArtistField;
   private JComboBox id3YearField;
   private JComboBox id3CommentField;
   private JCheckBox id3TrackNumberCheckBox;
   private JCheckBox id3TrackTitleCheckBox;
   private JCheckBox id3TrackAlbumCheckBox;
   private JCheckBox id3AlbumArtistCheckBox;
   private JCheckBox id3YearCheckBox;
   private JCheckBox id3CommentCheckBox; 
   
   private JCheckBox autoTag;
   private JCheckBox trackSearch;
   private JCheckBox titleSearch;
   private JRadioButton ignoreSentenceFiles;
   private JRadioButton deleteSentenceFiles;
   private JRadioButton archiveSentenceFiles;
   
   private JButton lessonDirButton;
   private JFileChooser fc;
   
   private StatusPanel statusPanel;
           
   public MP3Editor(Config spc, DirectoryTree dTree, StatusPanel es) 
   {
      super();
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setMaximumSize(new Dimension(400,1000));
      
      if (dTree != null)
      {
         dirTree = dTree;
      }
      else
      {
         Debug.debug("MP3Editor.MP3Editor() - Directory tree is null.");
         return;
      }
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("MP3Editor.MP3Editor() - Config is null.");
         return;
      }

      if (es != null)
      {
         statusPanel = es;
      }
      else
      {
         Debug.debug("MP3Editor.MP3Editor() - Config is null.");
         return;
      }

      //set up directory selection widgets
      JPanel dirChooserPanel = new JPanel(false);
      dirChooserPanel.setBorder(BorderFactory.createTitledBorder("Lesson Folder"));
      dirChooserPanel.setLayout(new BoxLayout(dirChooserPanel, BoxLayout.X_AXIS));
      dirChooserPanel.setPreferredSize(new Dimension(400,50));
      
      JLabel dirLabel = new JLabel("Directory: ");
      dirChooserPanel.add(dirLabel);
      dirChooserPanel.add(Box.createHorizontalGlue());
      
      lessonDirectory = new JTextField(30);
      lessonDirectory.setPreferredSize(new Dimension(325,25));
      lessonDirectory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
         curDirectory = lessonDirectory.getText();
         dirTree.setTopDirectory(curDirectory);
         seePodConfig.setLessonDirectory(curDirectory);
      }
      });
      dirChooserPanel.add(lessonDirectory);
      dirChooserPanel.add(Box.createHorizontalGlue());
      dirChooserPanel.add(Box.createRigidArea(new Dimension(5,0)));
      lessonDirectory.setText(seePodConfig.getLessonDirectory());
      
      //add an actionlisterner here for the lesson directory text field to update
      //the tree enter is pressed and clear the field on first mouse click
      
      lessonDirButton = new JButton();
      lessonDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      lessonDirButton.setToolTipText("Select Lesson Directory");
      
      lessonDirButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {      
            try 
            {
               dirChooserButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      dirChooserPanel.add(lessonDirButton);
      
      add(dirChooserPanel);
      
      // set up the text label panel and text field panels horizontally
      JPanel id3Panel = new JPanel();
      id3Panel.setLayout(new BoxLayout(id3Panel, BoxLayout.X_AXIS));
      id3Panel.setBorder(BorderFactory.createTitledBorder("MP3 Tags"));
      id3Panel.setPreferredSize(new Dimension(400,250));
      
      // set up the text label panel vertically
      Box id3LabelPanel = Box.createVerticalBox();

      id3LabelPanel.add(Box.createRigidArea(new Dimension(0,10)));
      
      JLabel fileTrackNumberLabel = new JLabel("Track Number: ");
      id3LabelPanel.add(fileTrackNumberLabel);
      
      id3LabelPanel.add(Box.createVerticalGlue());
      
      JLabel fileTrackTitleLabel = new JLabel("Track Title: ");
      id3LabelPanel.add(fileTrackTitleLabel);
      
      id3LabelPanel.add(Box.createVerticalGlue());
      
      JLabel fileTrackAlbumLabel = new JLabel("Track Album: ");
      id3LabelPanel.add(fileTrackAlbumLabel);
      
      id3LabelPanel.add(Box.createVerticalGlue());
      
      JLabel fileAlbumArtistLabel = new JLabel("Album Artist: ");
      id3LabelPanel.add(fileAlbumArtistLabel);
      
      id3LabelPanel.add(Box.createVerticalGlue());
      
      JLabel fileYearLabel = new JLabel("Year: ");
      id3LabelPanel.add(fileYearLabel);
      
      id3LabelPanel.add(Box.createVerticalGlue());
      
      JLabel fileCommentLabel = new JLabel("Comment: ");
      id3LabelPanel.add(fileCommentLabel);
      
      id3LabelPanel.add(Box.createRigidArea(new Dimension(0,5)));
     
      id3Panel.add(id3LabelPanel);
      
      //set up the id3 text field panel vertically
      
      Box id3TagPanel = Box.createVerticalBox();

      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3TrackNumberField = new JComboBox(seePodConfig.getTagContent(Config.ID3_TRACK_NUMBER_KEY));
      id3TrackNumberField.setPreferredSize(new Dimension(270,25));
      id3TrackNumberField.setMaximumSize(new Dimension(270,25));
      id3TrackNumberField.setMinimumSize(new Dimension(270,25));
      id3TrackNumberField.setEditable(true);
      id3TrackNumberCheckBox = new JCheckBox();
      id3TrackNumberCheckBox.setToolTipText("Enable update lesson number tag.");
      id3TrackNumberCheckBox.setSelected(false);
      id3TrackNumberCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3TrackNumberCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update track numbers enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update track numbers disabled.");
         }
          //Debug.debug("MP3Editor.MP3Editor() - Changed track number checkbox.");
      }});
      Box trackNumberBox = Box.createHorizontalBox();
      trackNumberBox.add(id3TrackNumberField);
      trackNumberBox.add(Box.createHorizontalGlue());
      trackNumberBox.add(id3TrackNumberCheckBox);
      trackNumberBox.add(Box.createHorizontalGlue());
      id3TagPanel.add(trackNumberBox);
      
      id3TagPanel.add(Box.createVerticalGlue());
      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3TrackTitleField = new JComboBox(seePodConfig.getTagContent(Config.ID3_TRACK_NAME_KEY));
      id3TrackTitleField.setPreferredSize(new Dimension(270,25));
      id3TrackTitleField.setMaximumSize(new Dimension(270,25));
      id3TrackTitleField.setMinimumSize(new Dimension(270,25));
      id3TrackTitleField.setEditable(true);
      //id3TrackTitleField.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      id3TrackTitleCheckBox = new JCheckBox();
      id3TrackTitleCheckBox.setToolTipText("Enable update lesson title tag.");
      id3TrackTitleCheckBox.setSelected(false);
      id3TrackTitleCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3TrackTitleCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update track titles enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update track titles disabled.");
         }
      }});
      Box trackTitleBox = Box.createHorizontalBox();
      trackTitleBox.add(id3TrackTitleField);
      trackTitleBox.add(Box.createHorizontalGlue());
      trackTitleBox.add(id3TrackTitleCheckBox);
      trackTitleBox.add(Box.createHorizontalGlue());
      id3TagPanel.add(trackTitleBox);
      
      id3TagPanel.add(Box.createVerticalGlue());
      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3TrackAlbumField = new JComboBox(seePodConfig.getTagContent(Config.ID3_ALBUM_NAME_KEY));
      id3TrackAlbumField.setPreferredSize(new Dimension(270,25));
      id3TrackAlbumField.setMaximumSize(new Dimension(270,25));
      id3TrackAlbumField.setMinimumSize(new Dimension(270,25));
      id3TrackAlbumField.setEditable(true);
      //id3TrackAlbumField.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      id3TrackAlbumCheckBox = new JCheckBox();
      id3TrackAlbumCheckBox.setToolTipText("Enable update lesson level tag.");
      id3TrackAlbumCheckBox.setSelected(false);
      id3TrackAlbumCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3TrackAlbumCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update album enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update album disabled.");
         }
      }});
      Box trackAlbumBox = Box.createHorizontalBox();
      trackAlbumBox.add(id3TrackAlbumField);
      trackAlbumBox.add(Box.createHorizontalGlue());
      trackAlbumBox.add(id3TrackAlbumCheckBox);
      trackAlbumBox.add(Box.createHorizontalGlue());
      id3TagPanel.add(trackAlbumBox);
      
      id3TagPanel.add(Box.createVerticalGlue());
      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3AlbumArtistField = new JComboBox(seePodConfig.getTagContent(Config.ID3_ARTIST_NAME_KEY));
      id3AlbumArtistField.setPreferredSize(new Dimension(270,25));
      id3AlbumArtistField.setMaximumSize(new Dimension(270,25));
      id3AlbumArtistField.setMinimumSize(new Dimension(270,25));
      id3AlbumArtistField.setEditable(true);
      //id3AlbumArtistField.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      id3AlbumArtistCheckBox = new JCheckBox();
      id3AlbumArtistCheckBox.setToolTipText("Enable update lesson artist tag.");
      id3AlbumArtistCheckBox.setSelected(false);
      id3AlbumArtistCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3AlbumArtistCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update artist enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update artist disabled.");
         }
      }});
      Box albumArtistBox = Box.createHorizontalBox();
      albumArtistBox.add(id3AlbumArtistField);
      albumArtistBox.add(Box.createHorizontalGlue());
      albumArtistBox.add(id3AlbumArtistCheckBox);
      albumArtistBox.add(Box.createHorizontalGlue());
      id3TagPanel.add(albumArtistBox);
      
      id3TagPanel.add(Box.createVerticalGlue());
      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3YearField = new JComboBox(seePodConfig.getTagContent(Config.ID3_YEAR_KEY));
      id3YearField.setPreferredSize(new Dimension(270,25));
      id3YearField.setMaximumSize(new Dimension(270,25));
      id3YearField.setMinimumSize(new Dimension(270,25));
      id3YearField.setEditable(true);
      //id3YearField.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      id3YearCheckBox = new JCheckBox();
      id3YearCheckBox.setToolTipText("Enable update lesson year tag.");
      id3YearCheckBox.setSelected(false);
      id3YearCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3YearCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update year enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update year disabled.");
         }
      }});
      Box yearBox = Box.createHorizontalBox();
      yearBox.add(id3YearField);
      yearBox.add(Box.createHorizontalGlue());
      yearBox.add(id3YearCheckBox);
      yearBox.add(Box.createHorizontalGlue());
      id3TagPanel.add(yearBox);
      
      id3TagPanel.add(Box.createVerticalGlue());
      
      id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3CommentField = new JComboBox(seePodConfig.getTagContent(Config.ID3_COMMENT_KEY)); 
      id3CommentField.setPreferredSize(new Dimension(270,25));
      id3CommentField.setMaximumSize(new Dimension(270,25));
      id3CommentField.setMinimumSize(new Dimension(270,25));
      id3CommentField.setEditable(true);
      //id3CommentField.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
      id3CommentCheckBox = new JCheckBox();
      id3CommentCheckBox.setToolTipText("Edit and update lesson comment tag.");
      id3CommentCheckBox.setSelected(false);
      id3CommentCheckBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
         if (id3CommentCheckBox.isSelected())
         {
            statusPanel.printStatusLine("Update comment enabled.");
         }
         else
         {
            statusPanel.printStatusLine("Update comment disabled.");
         }
      }});
      Box commentBox = Box.createHorizontalBox();
      commentBox.add(id3CommentField);
      commentBox.add(Box.createHorizontalGlue());
      commentBox.add(id3CommentCheckBox);
      commentBox.add(Box.createHorizontalGlue());
      //id3TagPanel.add(Box.createRigidArea(new Dimension(0,5)));
      id3TagPanel.add(commentBox);
      
      id3Panel.add(id3TagPanel);

      add(id3Panel); //now add to the top panel
      
      JPanel id3ScanPanel = new JPanel();
      id3ScanPanel.setLayout(new BoxLayout(id3ScanPanel, BoxLayout.Y_AXIS));
      
      Box checkBox = Box.createVerticalBox();
      
      Box autoTagBox = Box.createHorizontalBox();
      autoTag = new JCheckBox("Include Subdirectories", false);
      autoTag.setToolTipText("Add mp3 tags to files in all subdirectories.");
      autoTag.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          seePodConfig.setIncludeSubdirectories(autoTag.isSelected());
      }});
      autoTagBox.add(autoTag);
      autoTagBox.add(Box.createHorizontalGlue());
      checkBox.add(autoTagBox);
      
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
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
      deleteSentenceFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               radioButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
      archiveSentenceFiles.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               radioButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
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
      
      //checkBox.add(Box.createRigidArea(new Dimension(0,5)));
      radioButtonBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      id3ScanPanel.add(checkBox);
      id3ScanPanel.add(radioButtonBox);
      id3ScanPanel.setBorder(BorderFactory.createTitledBorder("Scan Options"));
      
      add(id3ScanPanel);
      
      //now the button panel under the tag panels
      JPanel id3ButtonPanel = new JPanel();
      id3ButtonPanel.setLayout(new BoxLayout(id3ButtonPanel, BoxLayout.X_AXIS));
      id3ButtonPanel.setBorder(BorderFactory.createTitledBorder("File Update"));
      
      //now the buttons of joy (Update id3 tags, show all id3 tags, clear all id3 tags).
      JButton updateTagsButton = new JButton("Update");
      updateTagsButton.setToolTipText("Update ID3 tags in selected file/s.");
      updateTagsButton.setPreferredSize(new Dimension(100,50));
      updateTagsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               updateTagsButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      id3ButtonPanel.add(updateTagsButton);
       
      id3ButtonPanel.add(Box.createHorizontalGlue());

      JButton showAllTagsButton = new JButton("Update All");
      showAllTagsButton.setToolTipText("Update ID3 tags for all files in selected directory/subdirectories.");
      showAllTagsButton.setPreferredSize(new Dimension(100,50));
      showAllTagsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               updateAllTagsButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      id3ButtonPanel.add(showAllTagsButton);

      id3ButtonPanel.add(Box.createHorizontalGlue());
      
      JButton updateWLCPTagsButton = new JButton("WLCP");
      updateWLCPTagsButton.setToolTipText("Update ID3 tags for all WLCP generated expansion and vocab files.");
      updateWLCPTagsButton.setPreferredSize(new Dimension(100,50));
      updateWLCPTagsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
            try 
            {
               updateWLCPTagsButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });

      id3ButtonPanel.add(updateWLCPTagsButton);
      
      add(id3ButtonPanel); //now add to the top panel
      
      add(statusPanel);
      
      add(Box.createVerticalGlue());
      
      getConfigItems();
   }
   
   private void getConfigItems()
   {
       autoTag.setSelected(seePodConfig.getIncludeSubdirectories());          
       ignoreSentenceFiles.setSelected(seePodConfig.getIgnoreSentenceFiles());
       deleteSentenceFiles.setSelected(seePodConfig.getDeleteSentenceFiles());
       archiveSentenceFiles.setSelected(seePodConfig.getArchiveSentenceFiles());     
   }
      
   private void dirChooserButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
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
         curDirectory = selectedFolder.getPath();
         currentDirectory = selectedFolder;
         lessonDirectory.setText(curDirectory); 
         dirTree.setTopDirectory(curDirectory);
         seePodConfig.setLessonDirectory(curDirectory);
     } 

     //Reset the file chooser for the next time it's shown.
     //fc.setSelectedFile(null);

     if (Config.DEBUG)
        Debug.debug("MP3Editor.dirChooserButtonActionPerformed() - selected: ", curDirectory);
   }
   
   private void updateWLCPTagsButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      Thread t = new Thread(new WLCPTagUpdater(this, seePodConfig));
      t.start();
      //Debug.debug("MP3Editor.updateWLCPTagsButtonActionPerformed() - updating WLCP tags.");
   }
   
   private void updateTagsButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      // put in the update code for selected files here

      Vector selectedFiles = mp3List.getSelectedFiles();
      for (int i = 0; i < selectedFiles.size(); i++)
      {
         File targetFile = new File((String)selectedFiles.get(i));
         //Debug.debug("MP3Editor.updateTagsButtonActionPerformed - on file: ",(String)selectedFiles.get(i));
         if (targetFile.exists())
         {
            writeMP3Tags(targetFile);
         }
         else
         {
            Debug.debug("MP3Editor.updateTagsButtonActionPerformed - file not found: ",(String)selectedFiles.get(i));
         }
      }
      
   }
   
   public void setFileList(FileList fl)
   {
      if (fl != null)
      {
         mp3List = fl;
      }
   }
   
   private void writeMP3Tags(File of)
   {
      
      ID3TagSet tagger = new ID3TagSet(of);

      //get the text field values and update selected file
      String fieldValue;
      if (id3TrackAlbumCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3TrackAlbumField.getSelectedItem()) != null)
         {
            tagger.setAlbumName(fieldValue);
         }
      }
      if (id3AlbumArtistCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3AlbumArtistField.getSelectedItem()) != null)
         {
            tagger.setArtist(fieldValue);
         }
      }
      if (id3TrackTitleCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3TrackTitleField.getSelectedItem()) != null)
         {
            tagger.setSongTitle(fieldValue);
         }
      }
      if (id3TrackNumberCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3TrackNumberField.getSelectedItem()) != null)
         {
            tagger.setTrackNumber(fieldValue);
         }
      }
      if (id3YearCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3YearField.getSelectedItem()) != null)
         {
            tagger.setYear(fieldValue);
         }
      }
      if (id3CommentCheckBox.isSelected())
      {
         if ((fieldValue = (String) id3CommentField.getSelectedItem()) != null)
         {
            tagger.setComment(fieldValue);
         }
      }
      
      tagger.writeID3Tags();
      
      statusPanel.printStatusLine("Updated tags in file: " + of.getName());
      
      return;   
   }   
   
   
   private void updateAllTagsButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      // do as above for selected file, but iterate through the directory tree updating all files.
      File sDir = dirTree.getSelectedNode();
      if (sDir == null)
      {
         sDir = new File(seePodConfig.getLessonDirectory());
         
      }
      lessonDirectory.setText(sDir.getAbsolutePath());
      directoryUpdate(sDir);

   }
   
   private void directoryUpdate(File startDir)
   {
      //update files in entire directory tree
      Thread t = new Thread(new MP3TagUpdater(startDir, this, seePodConfig));
      t.start();
      //Debug.debug("MP3Editor.recursiveFileUpdate() - Updating MP3 tags.");
   }
   
   private void radioButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      String action = evt.getActionCommand();
      if (action.startsWith("Ignore"))
      {
         //update config, only need to react to a set action not the deselect action for a button group, Config does the rest.
         if (ignoreSentenceFiles.isSelected())
         {
            seePodConfig.setIgnoreSentenceFiles(true);
            seePodConfig.setDeleteSentenceFiles(false);
            seePodConfig.setArchiveSentenceFiles(false);
            //Debug.debug("MP3Editor.radioButtonActionPerformed() - selected: ", action);
         }
      }
      else if (action.startsWith("Delete"))
      {
         if (deleteSentenceFiles.isSelected())
         {
            seePodConfig.setIgnoreSentenceFiles(false);
            seePodConfig.setDeleteSentenceFiles(true);
            seePodConfig.setArchiveSentenceFiles(false);
            //Debug.debug("MP3Editor.radioButtonActionPerformed() - selected: ", action);
         }
      }
      else
      {
         if (archiveSentenceFiles.isSelected())
         {
            seePodConfig.setIgnoreSentenceFiles(false);
            seePodConfig.setDeleteSentenceFiles(false);
            seePodConfig.setArchiveSentenceFiles(true);
            //Debug.debug("MP3Editor.radioButtonActionPerformed() - selected: ", action);
         }
      }
   }
   
   private void displayMP3Tags() throws IOException
   {
      //Debug.debug("MP3Editor.setMP3File() - Display mp3 tags: ");
      
      ID3TagSet tagSet = null; 
      
      tagSet = new ID3TagSet(curMP3File);
          
      if (tagSet == null)
      {
	 //Debug.debug("MP3Editor.displayMP3Tags() - No id3 metadata found.");
         statusPanel.printStatusLine("No ID3 tags found in: " + curMP3File.getName());
         return;
      }
      
      String trackno = null;
      String artist = null;
      String album = null;
      String songTitle = null;
      String year = null;
      //String genre = null;
      String comments = null;
      
     
      if (tagSet.hasID3v2Tags())
      {
         trackno = tagSet.getTrackNumber();
         artist = tagSet.getArtist();
         album = tagSet.getAlbumName();
         songTitle = tagSet.getSongTitle();
         year = tagSet.getYear();
         //genre = tagSet.getGenre();
         comments = tagSet.getComment();
         
         //Debug.debug("MP3Editor.displayMP3Tags() - ID3v2 tags found in file: ", comments);
         statusPanel.printStatusLine("Reading: " + curMP3File.getName() + " - " + songTitle);
      }
      else
      {
         Debug.debug("MP3Editor.displayMP3Tags() - No ID3 tags found in file: ", curMP3File);
      }
      
      if (trackno != null)
      {
         id3TrackNumberField.setSelectedItem(trackno);
         addComboBoxItem(id3TrackNumberField, trackno);
      }
      else
      {
         id3TrackNumberField.setSelectedItem(null);
      }
 
      if (songTitle != null)
      {
         id3TrackTitleField.setSelectedItem(songTitle);
         addComboBoxItem(id3TrackTitleField, songTitle);
      }
      else
      {
         id3TrackTitleField.setSelectedItem(null);
      }
 
      if (album != null)
      {
         id3TrackAlbumField.setSelectedItem(album);
         addComboBoxItem(id3TrackAlbumField, album);
      }
      else
      {
         id3TrackAlbumField.setSelectedItem(null);
      }
      
      if (artist != null)
      {
         id3AlbumArtistField.setSelectedItem(artist);
         addComboBoxItem(id3AlbumArtistField, artist);
      }
      else
      {
         id3AlbumArtistField.setSelectedItem(null);
      }
      
      if (year != null)
      {
         id3YearField.setSelectedItem(year.toString());
         addComboBoxItem(id3YearField, year.toString());
      }
      else
      {
         id3YearField.setSelectedItem(null);
      }
      
      //if (genre != null)
      //   id3GenreField.setSelectedItem(genre);
      //else
      //   id3GenreField.setSelectedItem(null);
      
      if (comments != null)
      {
         id3CommentField.setSelectedItem(comments);
         addComboBoxItem(id3CommentField, comments);
      }
      else
      {
         id3CommentField.setSelectedItem(null);
      }
       
      return;
   }

   
   public void resetUI()
   {
      fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   }

   public void resetComboBoxItems()
   {
     
      id3TrackAlbumField.removeAllItems();
      Vector albumNames = seePodConfig.getTagContent(Config.ID3_ALBUM_NAME_KEY);
      for (int i = 0; i < albumNames.size(); i++)
      {
         id3TrackAlbumField.addItem(albumNames.get(i));
      }
      id3AlbumArtistField.removeAllItems();
      Vector artistNames = seePodConfig.getTagContent(Config.ID3_ARTIST_NAME_KEY);
      for (int i = 0; i < artistNames.size(); i++)
      {
         id3AlbumArtistField.addItem(artistNames.get(i));
      }      
      
   }
   
   public void setMP3File(String filePath) 
   {
      File srcFile = new File(filePath);
       
      if (srcFile.getName().toLowerCase().endsWith(".mp3"))
      {
         curMP3File = srcFile;
         try {
            displayMP3Tags();
         } catch (IOException ex) {
            Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      else
      {
         Debug.debug("MP3Editor.setMP3File() - File name does not end with .mp3.");
      }
     
      
   }
   
   public void setTree(DirectoryTree treePanel)
   {
      if (treePanel != null)
      {
         dirTree = treePanel;
      }
   }
   
   public synchronized void printStatusMessage(String statusMsg)
   {
      statusPanel.printStatusLine(statusMsg);
   }
    
   public synchronized String getAlbumTag()
   {  
      return((String) id3TrackAlbumField.getSelectedItem());
   }
   
   public synchronized Boolean getUpdateAlbumTag()
   {
      return id3TrackAlbumCheckBox.isSelected();
   }
   
   public synchronized String getArtistTag()
   {
      return((String) id3AlbumArtistField.getSelectedItem());
   
   }
   
   public synchronized Boolean getUpdateArtistTag()
   {
      return id3AlbumArtistCheckBox.isSelected();
   }
      
   public synchronized String getTitleTag()
   {
      return((String) id3TrackTitleField.getSelectedItem());
   }
   
   public synchronized Boolean getUpdateTitleTag()
   {
      return id3TrackTitleCheckBox.isSelected();
   }
      
   public synchronized String getTrackNumberTag()
   {
      return((String) id3TrackNumberField.getSelectedItem());
   }
   
   public synchronized Boolean getUpdateTrackNumberTag()
   {
      return id3TrackNumberCheckBox.isSelected();
   }
   
   public synchronized String getYearTag()
   {
      return((String) id3YearField.getSelectedItem());
   }
   
   public synchronized Boolean getUpdateYearTag()
   {
      return id3YearCheckBox.isSelected();
   }
      
   public synchronized String getCommentTag()
   {
      return((String) id3CommentField.getSelectedItem());
   }
   
   public Boolean getUpdateCommentTag()
   {
      return id3CommentCheckBox.isSelected();
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

   public synchronized void setCurrentDirectory(File cdir)
   {
      lessonDirectory.setText(cdir.getAbsolutePath());
   }
   
   public synchronized Boolean getTrackSearchOn()
   {
      return trackSearch.isSelected();
   }
      
   public synchronized Boolean getTitleSearchOn()
   {
       return titleSearch.isSelected();
   }

   private void addComboBoxItem(JComboBox jcBox, String item)
   {
       if (!item.equals(""))
       {
          int a = 0;
          for (int i = 0; i < jcBox.getItemCount(); i++)
          {
            if (jcBox.getItemAt(i).equals(item))
            {
              a = 1;
              break;
            }
          }
          if (a == 0)
          {
            DefaultComboBoxModel cbm = (DefaultComboBoxModel) jcBox.getModel();
            cbm.insertElementAt(item, 0); //so recently added values are first in the list
          }
       }
   }

   public void saveComboBoxItems()
   {
       // go through the combobox lists and add to config, call from exit()
       if (id3TrackNumberField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3TrackNumberField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_TRACK_NUMBER_KEY);
       }
       if (id3TrackTitleField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3TrackTitleField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_TRACK_NAME_KEY);
       }
       if (id3TrackAlbumField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3TrackAlbumField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_ALBUM_NAME_KEY);
       }
       if (id3AlbumArtistField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3AlbumArtistField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_ARTIST_NAME_KEY);
       }
       if (id3YearField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3YearField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_YEAR_KEY);
       }
       if (id3CommentField.getItemCount() > 0)
       {
           ComboBoxModel cbm = id3CommentField.getModel();
           Vector itemList = new Vector();
           int size = cbm.getSize();
           for (int i = 0; i < size; i++)
           {
               itemList.add(cbm.getElementAt(i));
           }
           seePodConfig.setTagContent(itemList, Config.ID3_COMMENT_KEY);
       }
   }

}