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
 * Class  : LessonManager 
 * 
 * Description: Various control widgets and lesson information for managing lessons and lesson sets.
 * 
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LessonManagerControl extends JPanel 
{
   private Config seePodConfig;
   
   private ChinesePodLesson selectedLesson;
   private JButton lessonDirButton;
   protected File curMP3File;
   private String lessonPath;
   private JTextField lessonDirectory;
   private JFileChooser fc;
   
   private JCheckBox cpodAllLessons;
   private JCheckBox cpodNewbie;
   private JCheckBox cpodElementary;
   private JCheckBox cpodIntermediate;
   private JCheckBox cpodUpperIntermediate;
   private JCheckBox cpodAdvanced;
   private JCheckBox cpodMedia;
   private JCheckBox cpodOther;
   
   private JTextField trackNumberField;
   private JTextField trackTitleField;
   private JTextField albumNameField;
   private JTextField artistNameField;
   //private JTextField genreName;
   private JTextField yearField;
   private JTextField commentField;

   
   StatusPanel statusPanel;
   
   
   public LessonManagerControl(Config spc)
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(400,700));
      setMaximumSize(new Dimension(400,1000));
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("LessonManagerControl() - Config is null.");
      }

      JPanel dirChooserPanel = new JPanel(false);
      dirChooserPanel.setBorder(BorderFactory.createTitledBorder("Lesson Directory"));
      dirChooserPanel.setLayout(new BoxLayout(dirChooserPanel, BoxLayout.X_AXIS));
      dirChooserPanel.setPreferredSize(new Dimension(400,50));
      
      JLabel dirLabel = new JLabel("Lesson Directory: ");
      dirChooserPanel.add(dirLabel);
      dirChooserPanel.add(Box.createHorizontalGlue());
      
      lessonDirectory = new JTextField(30);
      //lessonDirectory.setMinimumSize(new Dimension(150,25));
      lessonDirectory.setMaximumSize(new Dimension(300,25));
      lessonDirectory.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
         lessonPath = lessonDirectory.getText();
         seePodConfig.setLessonDirectory(lessonPath);
         statusPanel.printStatusLine("Set lesson directory: " + lessonPath);
      }
      });
      dirChooserPanel.add(lessonDirectory);
      dirChooserPanel.add(Box.createHorizontalGlue());
      dirChooserPanel.add(Box.createRigidArea(new Dimension(5,0)));
      lessonDirectory.setText(seePodConfig.getLessonDirectory());
      
      lessonDirButton = new JButton();
      lessonDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      lessonDirButton.setToolTipText("Select Lesson Directory...");
      
      lessonDirButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            
            try 
            {
               lessonDirChooserButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(MP3Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         }
      });
      
      dirChooserPanel.add(lessonDirButton);
      add(dirChooserPanel);
      
      //now the wlcp download panel
      JPanel downloadPanel = new JPanel(false);
      downloadPanel.setBorder(BorderFactory.createTitledBorder("Lesson Options"));
      downloadPanel.setLayout(new BoxLayout(downloadPanel, BoxLayout.Y_AXIS));
      downloadPanel.setPreferredSize(new Dimension(350,500));
      
      Box checkBox = Box.createVerticalBox();
      
      
      Box cpodAllLessonsBox = Box.createHorizontalBox();
      cpodAllLessons = new JCheckBox("All lessons", true);
      cpodAllLessons.setToolTipText("Include all lessons in the ChinesePod cpod feed.");
      
      cpodAllLessons.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) 
      {
          cpodAllLessonsActionPerformed();
      }});
      
      cpodAllLessonsBox.add(cpodAllLessons);
      cpodAllLessonsBox.add(Box.createHorizontalGlue());
      checkBox.add(cpodAllLessonsBox);
      
      Box lessonBox = Box.createVerticalBox();
      
      Box cpodNewbieBox = Box.createHorizontalBox();
      cpodNewbie = new JCheckBox("Newbie lessons", false);
      cpodNewbie.setToolTipText("Include Newbie lessons in cpod feed.");
      cpodNewbieBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodNewbieBox.add(cpodNewbie);
      cpodNewbieBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodNewbieBox);
      
      Box cpodElementaryBox = Box.createHorizontalBox();
      cpodElementary = new JCheckBox("Elementary lessons", false);
      cpodElementary.setToolTipText("Include Elementary lessons in cpod feed.");
      cpodElementaryBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodElementaryBox.add(cpodElementary);
      cpodElementaryBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodElementaryBox);
     
      Box cpodIntermediateBox = Box.createHorizontalBox();
      cpodIntermediate = new JCheckBox("Intermediate lessons", false);
      cpodIntermediate.setToolTipText("Include Intermediate lessons in cpod feed.");
      cpodIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodIntermediateBox.add(cpodIntermediate);
      cpodIntermediateBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodIntermediateBox);
      
      Box cpodUpperIntermediateBox = Box.createHorizontalBox();
      cpodUpperIntermediate = new JCheckBox("Upper-Intermediate lessons", false);
      cpodUpperIntermediate.setToolTipText("Include Upper-Intermediate lessons in cpod feed.");
      cpodUpperIntermediateBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodUpperIntermediateBox.add(cpodUpperIntermediate);
      cpodUpperIntermediateBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodUpperIntermediateBox);
     
      Box cpodAdvancedBox = Box.createHorizontalBox();
      cpodAdvanced = new JCheckBox("Advanced lessons", false);
      cpodAdvanced.setToolTipText("Include Advanced lessons in cpod feed.");
      cpodAdvancedBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodAdvancedBox.add(cpodAdvanced);
      cpodAdvancedBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodAdvancedBox);
      
      Box cpodMediaBox = Box.createHorizontalBox();
      cpodMedia = new JCheckBox("Media lessons", false);
      cpodMedia.setToolTipText("Include Media lessons in cpod feed.");
      cpodMediaBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodMediaBox.add(cpodMedia);
      cpodMediaBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodMediaBox);
      
      Box cpodOtherBox = Box.createHorizontalBox();
      cpodOther = new JCheckBox("Other lessons", false);
      cpodOther.setToolTipText("Include Qing Wen, News & Features, etc.");
      cpodOtherBox.add(Box.createRigidArea(new Dimension(25,0)));
      cpodOtherBox.add(cpodOther);
      cpodOtherBox.add(Box.createHorizontalGlue());
      lessonBox.add(cpodOtherBox);
      
      //checkBox.add(lessonBox);
      checkBox.add(Box.createRigidArea(new Dimension(0,5)));
      lessonBox.add(Box.createRigidArea(new Dimension(0,5)));
      downloadPanel.add(checkBox);
      downloadPanel.add(lessonBox);
      
      JPanel tagPanel = new JPanel(false);
      tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.X_AXIS));
      tagPanel.setBorder(BorderFactory.createTitledBorder("MP3 Tags"));
      tagPanel.setPreferredSize(new Dimension(350, 350));
      
      Box fieldBox = Box.createVerticalBox();
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));

      trackNumberField = new JTextField(); //JTextField(30);
      trackNumberField.setEditable(false);
      trackNumberField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(trackNumberField);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));

      trackTitleField = new JTextField(); // JTextField(30);
      trackTitleField.setEditable(false);
      trackTitleField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(trackTitleField);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      

      // now the drop boxes and labels
      
      Box labelBox = Box.createVerticalBox();
      //labelBox.add(Box.createVerticalGlue());
      labelBox.add(Box.createRigidArea(new Dimension(0,10)));
      labelBox.add(new JLabel("Lesson Number:   "));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Lesson Title:")); 
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Album Name:   "));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Artist:"));
      labelBox.add(Box.createVerticalGlue());
      //bLabelBox.add(new JLabel("Genre:"));
      //bLabelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Year:"));
      labelBox.add(Box.createVerticalGlue());
      labelBox.add(new JLabel("Comment:"));
      //labelBox.add(Box.createVerticalGlue());
      labelBox.add(Box.createRigidArea(new Dimension(0,10)));

      albumNameField = new JTextField(50);
      albumNameField.setEditable(false);
      albumNameField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(albumNameField);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
     
      artistNameField = new JTextField(50);
      artistNameField.setEditable(false);
      artistNameField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(artistNameField);
    
      //cannot add user defined genres to to genre tag, only predefined music genres!!!!!
      //bFieldBox.add(Box.createRigidArea(new Dimension(0,5)));
     
      //String[] genreNames = { "Chinese", "Mandarin", "ChinesePod", "Language", "PuTongHua" };
      //genreName = new JTextField(genreNames);
      //genreName.setEditable(true);
      //genreName.setPreferredSize(new Dimension(225,25));
     // bFieldBox.add(genreName);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      yearField = new JTextField(50);
      yearField.setEditable(false);
      yearField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(yearField);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      commentField = new JTextField(50);
      commentField.setEditable(false);
      commentField.setPreferredSize(new Dimension(225,25));
      fieldBox.add(commentField);
      
      fieldBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      tagPanel.add(labelBox);
      tagPanel.add(fieldBox);
  
      tagPanel.add(Box.createRigidArea(new Dimension(0,25)));
      tagPanel.add(Box.createVerticalGlue());
      
      downloadPanel.add(tagPanel);
      downloadPanel.add(Box.createRigidArea(new Dimension(0,5)));
      downloadPanel.add(Box.createVerticalGlue());
      
      add(downloadPanel);

      statusPanel = new StatusPanel();
      add(statusPanel);
      
      cpodAllLessonsActionPerformed();
   }
   
   public void setSelectedLesson(File lessonFile)
   {
      //get the ID3 tags and add to tag text fields
      selectedLesson = new ChinesePodLesson(lessonFile);
      curMP3File = lessonFile;
      
      if (selectedLesson.hasMP3Tags())
      {
         //display the tags in the Jtextfields
         displayMP3Tags();
      }
   }
   
   private void displayMP3Tags() //throws IOException
   {
      //Debug.debug("MP3Editor.setMP3File() - Display mp3 tags: ");
      
      ID3TagSet tagSet = selectedLesson.getID3Tags(); 
      
      if (tagSet == null)
      {
	 Debug.debug("LessonManagerControl.displayMP3Tags() - No id3 metadata found.");
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
         
         Debug.debug("LessonManagerControl.displayMP3Tags() - ID3v2 tags found in file: ", comments);
         //statusPanel.printStatusLine("Reading: " + curMP3File.getName() + " - " + songTitle);
      }
      else
      {
         Debug.debug("LessonManagerControl.displayMP3Tags() - No ID3 tags found in file: ", curMP3File);
      }
      
      /* DEPRECATED BY ME!!!
      else if (tagSet.hasID3v1Tags())
      {
         
         //trackno = tags.getFirstTrack(); //no track numbers in id3v1
         artist = tagSet.getArtist();
	   album = tagSet.getAlbumName();
	   songTitle = tagSet.getSongTitle();
	   year = tagSet.getYear();
         //genre = tagSet.getGenre();
         comments = tagSet.getComment();
 
         statusPanel.printStatusLine("Reading: " + curMP3File.getName() + " - " + songTitle);
         
         Debug.debug("LessonManagerControl.displayMP3Tags() - ID3v1 tags found in file: ", comments);
      }
      */
       
     
      
      //fileNameField.setSelectedItem(curMP3File.getName());
      
      if (trackno != null)
         trackNumberField.setText(trackno);
      else
         trackNumberField.setText(null);
 
      if (songTitle != null)
         trackTitleField.setText(songTitle);
      else
         trackTitleField.setText(null);
 
      if (album != null)
         albumNameField.setText(album);
      else
         albumNameField.setText(null);
      
      if (artist != null)
         artistNameField.setText(artist);
      else
         artistNameField.setText(null);
      
      if (year != null)
         yearField.setText(year.toString());
      else
         yearField.setText(null);
      
      //if (genre != null)
      //   id3GenreField.setSelectedItem(genre);
      //else
      //   id3GenreField.setSelectedItem(null);
      
      if (comments != null)
         commentField.setText(comments);
      else
         commentField.setText(null);
       
      return;
   }

   public void printStatusMessage(String sm)
   {
      statusPanel.printStatusLine(sm);
      return;
   }
   
   private void lessonDirChooserButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
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
            lessonPath = selectedFolder.getCanonicalPath();
            lessonDirectory.setText(lessonPath); 
            seePodConfig.setLessonDirectory(lessonPath);
            Debug.debug("LessonManagerControl.lessonDirChooserButtonActionPerformed(): ", lessonPath);
            
        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("LessonManagerControl.lessonDirChooserButtonActionPerformed(): cancelled file selection.");
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
         cpodOther.setSelected(true);
         
         cpodNewbie.setEnabled(false);
         cpodElementary.setEnabled(false);
         cpodIntermediate.setEnabled(false);
         cpodUpperIntermediate.setEnabled(false);
         cpodAdvanced.setEnabled(false);
         cpodMedia.setEnabled(false);
         cpodOther.setEnabled(false);
      }
      else
      {
         cpodNewbie.setSelected(false);
         cpodElementary.setSelected(false);
         cpodIntermediate.setSelected(false);
         cpodUpperIntermediate.setSelected(false);
         cpodAdvanced.setSelected(false);
         cpodMedia.setSelected(false);
         cpodOther.setSelected(false);
         
         cpodNewbie.setEnabled(true);
         cpodElementary.setEnabled(true);
         cpodIntermediate.setEnabled(true);
         cpodUpperIntermediate.setEnabled(true);
         cpodAdvanced.setEnabled(true);
         cpodMedia.setEnabled(true);
         cpodOther.setEnabled(true);

      }
   } 
   public Boolean getAllSelected()
   {
      return cpodAllLessons.isSelected();
   }
   public Boolean getNewbieSelected()
   {
      return cpodNewbie.isSelected();
   }
   public Boolean getElementarySelected()
   {
      return cpodElementary.isSelected();
   }
   public Boolean getIntermediateSelected()
   {
      return cpodIntermediate.isSelected();
   }
   public Boolean getUpperIntermediateSelected()
   {
      return cpodUpperIntermediate.isSelected();
   }
   public Boolean getAdvancedSelected()
   {
      return cpodAdvanced.isSelected();
   }
   public Boolean getMediaSelected()
   {
      return cpodMedia.isSelected();
   }
   public Boolean getOtherSelected()
   {
      return cpodOther.isSelected();
   }

   public void resetUI()
   {
      fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   }
}