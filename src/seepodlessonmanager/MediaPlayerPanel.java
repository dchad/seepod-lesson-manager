
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
 * Class  : WLCPPanel 
 * 
 * Description: Container for the lesson manager panels, lessonlist, lessonControl and lessonSetList, lessonMediaPlayer.
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

public class MediaPlayerPanel extends JPanel 
{
   private Config seePodConfig;
   private JList playList;
   private DefaultListModel playListModel;
   private LessonManagerControl controller;
   private JSlider mp3ProgressSlider;
   private BoundedRangeModel sliderModel;
   private MP3File mp3File;
   private ID3TagSet id3Tags;
   private MP3AudioHeader mp3Header;
   private MediaPlayer mp3Player;
   private int playListIndex;
   private Boolean stopped;
   private Boolean paused;
   private Boolean playing;
   private ImageIcon playIcon;
   private ImageIcon pauseIcon;
   private JButton playButton;
   private JFileChooser fc;
   private JPopupMenu popup;
   
   public MediaPlayerPanel(LessonManagerControl lmc, Config spc)
   {
      super(false);
      
      //MP3File.logger.setLevel(Level.OFF); 
      //Turn off vast deluge of status messages when reading/writing id3 tags!!!
      Logger logger = Logger.getLogger("org.jaudiotagger.audio");
      logger.setLevel(Level.OFF);
      Logger id3logger = Logger.getLogger("org.jaudiotagger.tag.id3");
      id3logger.setLevel(Level.OFF);
      Logger datalogger = Logger.getLogger("org.jaudiotagger.tag.datatype");
      datalogger.setLevel(Level.OFF);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("MediaPlayerPanel() - Config is null.");
         return;
      }
      
      if (lmc != null)
      {
         controller = lmc;
      }
      else
      {
         Debug.debug("MediaPlayerPanel() - Lesson manager controller is null.");
         return;
      }
      
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      //setBorder(BorderFactory.createTitledBorder("Media"));
      setPreferredSize(new Dimension(400, 700));
    
      JPanel mp3Panel = new JPanel();
      mp3Panel.setLayout(new BoxLayout(mp3Panel, BoxLayout.Y_AXIS));
      mp3Panel.setBorder(BorderFactory.createTitledBorder("MP3 Player"));
      //mp3Panel.setPreferredSize(new Dimension(400, 700));
      
      mp3ProgressSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
      mp3ProgressSlider.setMajorTickSpacing(60);
      mp3ProgressSlider.setMinorTickSpacing(10);
      mp3ProgressSlider.setPaintLabels(true);
      mp3ProgressSlider.setPaintTicks(true);
      sliderModel = mp3ProgressSlider.getModel();
         
      mp3Panel.add(mp3ProgressSlider);
         
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/Play16.gif");
      playIcon = new ImageIcon(imageURL);
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/Pause16.gif");
      pauseIcon = new ImageIcon(imageURL);
      
      playButton = new JButton(playIcon);
      playButton.setMinimumSize(new Dimension(25,50));
      playButton.setToolTipText("Play/Pause lesson MP3 file.");
      playButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            //set a state variable "playing" and change to "pause" if clicked while "playing" 
            if (playListModel.size() > 0)
            {
               if (stopped)
               {
                  playPlayList();
                  playButton.setIcon(playIcon);
                  stopped = false;
                  paused = false;
                  playing = true;
               }
               else if (playing)
               {
                  playButton.setIcon(pauseIcon);
                  paused = true;
                  playing = false;
                  stopped = false;
               }
               else if (paused)
               {
                  playButton.setIcon(playIcon);
                  paused = false;
                  playing = true;
                  stopped = false;
               }
            }
         }
      });
      buttonBox.add(playButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/Stop16.gif");
      
      JButton stopButton = new JButton(new ImageIcon(imageURL));
      stopButton.setMinimumSize(new Dimension(25,50));
      stopButton.setToolTipText("Stop lesson playback.");
      stopButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            playButton.setIcon(playIcon);
            stopped = true;
            sliderModel.setValue(0);
         }
      });
      buttonBox.add(stopButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/StepBack16.gif");
      
      JButton skipBackButton = new JButton(new ImageIcon(imageURL));
      skipBackButton.setMinimumSize(new Dimension(25,50));
      skipBackButton.setToolTipText("Play previous MP3 file.");
      skipBackButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            playPrevious();
         }
      });
      buttonBox.add(skipBackButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/StepForward16.gif");
      
      JButton skipNextButton = new JButton(new ImageIcon(imageURL));
      skipNextButton.setMinimumSize(new Dimension(25,50));
      skipNextButton.setToolTipText("Play next MP3 file.");
      skipNextButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            playNext();
         }
      });
      buttonBox.add(skipNextButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/folder_yellow.png");
      
      JButton fileButton = new JButton(new ImageIcon(imageURL));
      fileButton.setMinimumSize(new Dimension(25,50));
      fileButton.setToolTipText("Open an MP3 file.");
      fileButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            mp3FileChooser();
         }
      });
      buttonBox.add(fileButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      mp3Panel.add(buttonBox);
      
      
      JPanel playListPanel = new JPanel();
      playListPanel.setLayout(new BoxLayout(playListPanel, BoxLayout.Y_AXIS));
      playListPanel.setBorder(BorderFactory.createTitledBorder("Playlist"));

      createPopupMenu();

      playListModel = new DefaultListModel();
      playList = new JList(playListModel);
      MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) 
      {
         if (e.getClickCount() == 2) 
         {
            playListIndex = playList.locationToIndex(e.getPoint());
            if (playListModel.size() > 0)
            {
               File f = (File) playListModel.get(playListIndex);
               if (mp3Player != null)
               {
                  mp3Player.close();
               }
               play(f);
            }
         }
         else
         {
            playListIndex = playList.locationToIndex(e.getPoint());
            if (playListModel.size() > 0)
            {
               File f = (File) playListModel.get(playListIndex);
               controller.setSelectedLesson(f);
            }
         }
      }
      @Override
      public void mousePressed(MouseEvent e)
      {
          if (playList.getVisibleRowCount() == 0)
          {
             return;
          }
          if (e.isPopupTrigger())
          {
             playListIndex = playList.locationToIndex(e.getPoint());
             playList.setSelectedIndex(playListIndex);
             popup.show(e.getComponent(), e.getX(), e.getY());
          }
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
          if (playList.getVisibleRowCount() == 0)
          {
             return;
          }
          if (e.isPopupTrigger())
          {
             playListIndex = playList.locationToIndex(e.getPoint());
             playList.setSelectedIndex(playListIndex);
             popup.show(e.getComponent(), e.getX(), e.getY());
          }
      }
      };
      playList.addMouseListener(mouseListener);
      
      JScrollPane playListScroller = new JScrollPane(playList);
      
      playListPanel.add(playListScroller);
                    
      add(mp3Panel);
      add(playListPanel);
      
      stopped = true; //yet another state machine :<
      playing = false;
      paused = false;
      
      return;
   }
   
   private void createPopupMenu()
   {
     JMenuItem menuItem;
     popup = new JPopupMenu();

     menuItem = new JMenuItem("Remove file from list");
     menuItem.addActionListener(new java.awt.event.ActionListener()
     {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (playListModel.size() > 0)
            {
               if (playListIndex < playListModel.size())
               {
                  playListModel.remove(playListIndex);
                  //playList.remove(playListIndex);
               }
            }
         }
     });
     popup.add(menuItem);
     menuItem = new JMenuItem("Clear play list");
     menuItem.addActionListener(new java.awt.event.ActionListener()
     {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if (playListModel.size() > 0)
            {
               playListModel.removeAllElements();
            }
            //playList.removeAll();
         }
     });
     popup.add(menuItem);
   }
   
   public synchronized void updateProgressBar(int pos)
   {
      //move slider to current position
      sliderModel.setValue(pos);
      //Debug.debug("MediaPlayerPanel.updateProgressBar() - Received position: ", pos);
   }
   
   public void playNext()
   {

      if (playListIndex < (playListModel.size() - 1))
      {
         if (mp3Player != null)
         {
            mp3Player.close();
         }
         playListIndex++;
         if (Config.DEBUG)
            Debug.debug("MediaPlayerPanel.playNext() - playing index: ", playListIndex);
         playList.setSelectedIndex(playListIndex);
         File f = (File) playListModel.get(playListIndex);
         play(f);
      }
   }
   
   public void playPrevious()
   {

      if (playListIndex > 0)
      {
         if (mp3Player != null)
         {
            mp3Player.close();
         }
         playListIndex--;
         if (Config.DEBUG)
            Debug.debug("MediaPlayerPanel.playPrevious() - playing index: ", playListIndex);
         playList.setSelectedIndex(playListIndex);
         File f = (File) playListModel.get(playListIndex);
         play(f);               
      }
      else
      {
         stopped = true;
      }
   }
   
   public synchronized void playBackComplete()
   {
      //play next file in playlist
      sliderModel.setValue(0);
      if (stopped)
      {
         return;
      }
      if (playListIndex < (playListModel.size() - 1))
      {
         playListIndex++;
         playList.setSelectedIndex(playListIndex);
         File f = (File) playListModel.get(playListIndex);
         play(f);
      }
      else
      {
         stopped = true;
      }

   }
   
   public synchronized void addMP3File(ChinesePodLesson cpodLesson)
   { 
      playListModel.addElement(cpodLesson.getCPODLessonFile());
   }
   
   public synchronized void addLessonFiles(ChinesePodLesson cpodLesson)
   {
   
      playListModel.addElement(cpodLesson.getCPODLessonFile());
      //now add dialogue and review files
      String tmp = cpodLesson.getLessonItem(Config.LESSON_REV_FILE_KEY);
      if (tmp != null)
      {
         File f = new File(tmp);
         if (f.exists())
         {
            playListModel.addElement(f);
         }
         else
         {
            //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
            if (Config.DEBUG)
               Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
         }
      }
      tmp = cpodLesson.getLessonItem(Config.LESSON_DIALOG_FILE_KEY);
      if (tmp != null)
      {
         File f = new File(tmp);
         if (f.exists())
         {
            playListModel.addElement(f);
         }
         else
         {
            //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
            if (Config.DEBUG)
               Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
         }
      }  
     
      if (cpodLesson.hasWLCPFiles())
      {
         //now add expansion and vocab files generated by WLCP 
         tmp = cpodLesson.getLessonItem(Config.LESSON_WLCP_EXPANSION_FILE_KEY);
         if (tmp != null)
         {
            File f = new File(tmp);
            if (f.exists())
            {
               playListModel.addElement(f);
            }
            else
            {
               //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
               if (Config.DEBUG)
                  Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
            }
         }   
         tmp = cpodLesson.getLessonItem(Config.LESSON_WLCP_VOCAB_FILE_KEY);
         if (tmp != null)
         {
            File f = new File(tmp);
            if (f.exists())
            {
               playListModel.addElement(f);
            }
            else
            {
               //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
               if (Config.DEBUG)
                  Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
            }
         }       
      }
   }
   
   public synchronized void addExpansionMP3File(ChinesePodLesson cpodLesson)
   {
      if (cpodLesson.hasWLCPFiles())
      {
         //now add expansion and vocab files generated by WLCP 
         String tmp = cpodLesson.getLessonItem(Config.LESSON_WLCP_EXPANSION_FILE_KEY);
         if (tmp != null)
         {
            File f = new File(tmp);
            if (f.exists())
            {
               playListModel.addElement(f);
            }
            else
            {
               //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
               Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
            }            
         }   
      }
   }
      
   public synchronized void addVocabMP3File(ChinesePodLesson cpodLesson)
   {
      if (cpodLesson.hasWLCPFiles())
      {
         //now add expansion and vocab files generated by WLCP 
         String tmp = cpodLesson.getLessonItem(Config.LESSON_WLCP_VOCAB_FILE_KEY);
         if (tmp != null)
         {
            File f = new File(tmp);
            if (f.exists())
            {
               playListModel.addElement(f);
            }
            else
            {
               //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
               Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
            }
         }   
      }
   }
   
   public synchronized void addDialogMP3File(ChinesePodLesson cpodLesson)
   {
      String tmp = cpodLesson.getLessonItem(Config.LESSON_DIALOG_FILE_KEY);
      if (tmp != null)
      {
         File f = new File(tmp);
         if (f.exists())
         {
            playListModel.addElement(f);
         }
         else
         {
            //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
            Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
         }
      }   
   }
      
   public synchronized void addReviewMP3File(ChinesePodLesson cpodLesson)
   {
      String tmp = cpodLesson.getLessonItem(Config.LESSON_REV_FILE_KEY);
      if (tmp != null)
      {
         File f = new File(tmp);
         if (f.exists())
         {
            playListModel.addElement(f);
         }
         else
         {
            //controller.printStatusMessage("MP3 file does not exist: " + f.getName());
            Debug.debug("MediaPlayerPanel.addLessonFiles() - File does not exist: ", f.getAbsolutePath());
         }
      }   
   }
      
   public void playPlayList() 
   {
      if (playListModel.size() > 0)
      {
         playListIndex = 0;
         playList.setSelectedIndex(playListIndex);
         File f = (File) playListModel.get(playListIndex);
         play(f);
      }
   }
   
   private void play(File mp3Play)
   {
      Boolean id3Error = false;

      if (mp3Play.exists())
      {
         try 
         {
            mp3File = new MP3File(mp3Play);
         } catch (IOException ex) {
            id3Error = true;
         } catch (TagException ex) {
            id3Error = true;
         } catch (ReadOnlyFileException ex) {
            id3Error = true;
         } catch (InvalidAudioFrameException ex) {
            id3Error = true;
         }

         if (id3Error)
         {
            JOptionPane.showMessageDialog(null, "Invalid MP3 file data, audio data may be corrupted.", "Warning", JOptionPane.ERROR_MESSAGE);
            stopped = true;
            playNext();
            return;
         }
         
         controller.setSelectedLesson(mp3Play);
         controller.printStatusMessage("Playing MP3 file: " + mp3Play.getName());
         mp3Header = mp3File.getMP3AudioHeader();

         //Debug.debug("Number of frames: ", mp3Header.getNumberOfFrames());
         //Debug.debug("Number of frames estimate: ", mp3Header.getNumberOfFramesEstimate());
         //Debug.debug("Precise track length: ", mp3Header.getPreciseTrackLength());
         //Debug.debug("Track length: ", mp3Header.getTrackLength());
         //Debug.debug("Bit Rate: ", mp3Header.getBitRate());

         long trackLengthSecs = 0;
         String fileName = mp3Play.getName();
         if (fileName.endsWith(Config.WLCP_EXPANSION_SUFFIX) || fileName.endsWith(Config.WLCP_VOCAB_SUFFIX))
         {
            //make a cludge to fix the track length
            long fileSize = mp3Play.length();
            long bitRate = mp3Header.getBitRateAsNumber();
            //int sampleRate = mp3Header.getSampleRateAsNumber();
            //long frameSize = 144000 * bitRate / (sampleRate + 1);
            long falseLength = (((8 * fileSize) / 1000) / bitRate);
            //Debug.debug("False track length: ", falseLength);
            trackLengthSecs =  falseLength - 60;
         }
         else
         {
            trackLengthSecs = (long) mp3Header.getTrackLength();
         }
         mp3ProgressSlider.setMaximum((int) trackLengthSecs);
         Hashtable labelTable = new Hashtable();
         labelTable.put(new Integer(0), new JLabel("0:0"));
         Integer secs = (int) trackLengthSecs % 60;
         Integer mins = ((int)trackLengthSecs - secs) / 60;
         String trackTime = mins.toString() + ":" + secs.toString();
         labelTable.put(new Integer((int)trackLengthSecs), new JLabel(trackTime));
         mp3ProgressSlider.setLabelTable(labelTable);
       
         stopped = false;
         
         mp3Player = new MediaPlayer(this, mp3Play);
         Thread t = new Thread(mp3Player);
         t.start();
         
      }
   }
   
   public synchronized Boolean stop()
   {
      return stopped;
   }
   
   public synchronized Boolean pause()
   {
      return paused;
   }
   
   private void mp3FileChooser()
   {
      
        if (fc == null) 
        {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
            FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 files","mp3");
            fc.setFileFilter(filter);
        }
        
        int returnVal = fc.showOpenDialog(this);
        
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File selectedFile = (File) fc.getSelectedFile();
            playListModel.addElement(selectedFile);
            if (Config.DEBUG)
               Debug.debug("MediaPlayerPanel.fileChooserButtonActionPerformed(): ", selectedFile.getAbsolutePath());
            
        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("MediaPlayerPanel.fileChooserButtonActionPerformed(): cancelled file selection.");
        }
        
   }

   public void resetUI()
   {
      fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 files","mp3");
      fc.setFileFilter(filter);

      createPopupMenu();
   }
}