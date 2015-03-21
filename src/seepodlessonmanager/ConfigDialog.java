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
 * Class  : ConfigDialog
 * 
 * Description: JDialog to display and set configuration items.
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class ConfigDialog extends JDialog
{
   
   JPanel contentPanel;
   JTextField wlcpDirectoryField;
   JTextField wlcpScriptField;
   JTextField lessonDirectoryField;
   JTextField pythonPathField;
   JTextField rssFeedURLField;
   JList lessonLevelsList;
   JTextField lessonTextField;
   Vector lessonLevels;
   DefaultListModel lessonListModel;
   JScrollPane lessonScroller;
   JList artistList;
   JTextField artistTextField;
   Vector artists;
   DefaultListModel artistListModel;
   JScrollPane artistScroller;
   JLabel wlcpDirLabel;
   JLabel wlcpScriptLabel;
   JLabel lessonDirLabel;
   JLabel pythonLabel;
   JLabel rssFeedLabel;
   JLabel lessonListLabel;
   JLabel artistListLabel;
   
   JButton closeButton;
   JButton saveButton;
   Container cp;
   
   Config seePodConfig;

   JFileChooser fc;

   MP3EditorPanel mp3panel;

   public ConfigDialog(Frame aFrame, Config spConfig, MP3EditorPanel mp3ed)
   {
      super(aFrame, "SeePod Configuration", false);

      seePodConfig = spConfig;
      mp3panel = mp3ed;
      
      cp = getContentPane();
     
      setSize(800,500);
     
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(800,600));
     
      JPanel dirPanel = new JPanel();
      dirPanel.setBorder(BorderFactory.createTitledBorder("Directory Settings"));
      dirPanel.setLayout(new BoxLayout(dirPanel, BoxLayout.Y_AXIS));
      
      wlcpDirLabel = new JLabel("WLCP Directory: ");
      wlcpDirLabel.setPreferredSize(new Dimension(125,30));
      wlcpDirLabel.setMinimumSize(new Dimension(125,30));
      wlcpDirLabel.setMaximumSize(new Dimension(125,30));

      wlcpScriptLabel = new JLabel("WLCP Script: ");
      wlcpScriptLabel.setPreferredSize(new Dimension(125,30));
      wlcpScriptLabel.setMinimumSize(new Dimension(125,30));
      wlcpScriptLabel.setMaximumSize(new Dimension(125,30));
      
      lessonDirLabel = new JLabel("Lesson Directory: ");
      lessonDirLabel.setPreferredSize(new Dimension(125,30));
      lessonDirLabel.setMinimumSize(new Dimension(125,30));
      lessonDirLabel.setMaximumSize(new Dimension(125,30));
      
      pythonLabel = new JLabel("Python File: ");
      pythonLabel.setPreferredSize(new Dimension(125,30));
      pythonLabel.setMinimumSize(new Dimension(125,30));
      pythonLabel.setMaximumSize(new Dimension(125,30));
      
      rssFeedLabel = new JLabel("RSS Feed URL: ");
      rssFeedLabel.setPreferredSize(new Dimension(125,30));
      rssFeedLabel.setMinimumSize(new Dimension(125,30));
      rssFeedLabel.setMaximumSize(new Dimension(125,30));
      
      Box dirBox = Box.createVerticalBox();

      wlcpDirectoryField = new JTextField(100);
      wlcpDirectoryField.setMaximumSize(new Dimension(400,30));
      wlcpDirectoryField.setMinimumSize(new Dimension(400,30));
      wlcpDirectoryField.setPreferredSize(new Dimension(400,30));
      //dirBox.add(Box.createRigidArea(new Dimension(25,0)));
      Box wlcpBox = Box.createHorizontalBox();
      JButton wlcpDirButton = new JButton();
      wlcpDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      wlcpDirButton.setToolTipText("Select WLCP Directory");
      wlcpDirButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            wlcpDirChooserButtonActionPerformed();
         }
      });
      wlcpBox.add(wlcpDirLabel);
      wlcpBox.add(Box.createRigidArea(new Dimension(5,0)));
      wlcpBox.add(Box.createHorizontalGlue());
      wlcpBox.add(wlcpDirectoryField);
      wlcpBox.add(Box.createRigidArea(new Dimension(5,0)));
      wlcpBox.add(wlcpDirButton);

      dirBox.add(wlcpBox);
      dirBox.add(Box.createRigidArea(new Dimension(0,5)));
      dirBox.add(Box.createVerticalGlue());
      
      wlcpScriptField = new JTextField(100);
      wlcpScriptField.setMaximumSize(new Dimension(400,30));
      wlcpScriptField.setMinimumSize(new Dimension(400,30));
      wlcpScriptField.setPreferredSize(new Dimension(400,30));
      //dirBox.add(Box.createRigidArea(new Dimension(25,0)));
      Box scriptBox = Box.createHorizontalBox();
      JButton scriptDirButton = new JButton();
      scriptDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      scriptDirButton.setToolTipText("Select WLCP script");
      scriptDirButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            scriptDirChooserButtonActionPerformed();
         }
      });
      scriptBox.add(wlcpScriptLabel);
      scriptBox.add(Box.createRigidArea(new Dimension(5,0)));
      scriptBox.add(Box.createHorizontalGlue());
      scriptBox.add(wlcpScriptField);
      scriptBox.add(Box.createRigidArea(new Dimension(5,0)));
      scriptBox.add(scriptDirButton);

      dirBox.add(scriptBox);
      dirBox.add(Box.createRigidArea(new Dimension(0,5)));
      dirBox.add(Box.createVerticalGlue());
      
      lessonDirectoryField = new JTextField(100);
      lessonDirectoryField.setMaximumSize(new Dimension(400,30));
      lessonDirectoryField.setMinimumSize(new Dimension(400,30));
      lessonDirectoryField.setPreferredSize(new Dimension(400,30));
      Box lessonBox = Box.createHorizontalBox();
      JButton lessonDirButton = new JButton();
      lessonDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      lessonDirButton.setToolTipText("Select lesson directory");
      lessonDirButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            lessonDirChooserButtonActionPerformed();
         }
      });
      lessonBox.add(lessonDirLabel);
      lessonBox.add(Box.createRigidArea(new Dimension(5,0)));
      lessonBox.add(Box.createHorizontalGlue());
      lessonBox.add(lessonDirectoryField);
      lessonBox.add(Box.createRigidArea(new Dimension(5,0)));
      lessonBox.add(lessonDirButton);

      dirBox.add(lessonBox);
      dirBox.add(Box.createRigidArea(new Dimension(0,5)));
      dirBox.add(Box.createVerticalGlue());
      
      pythonPathField = new JTextField(100);
      pythonPathField.setMaximumSize(new Dimension(400,30));
      pythonPathField.setMinimumSize(new Dimension(400,30));
      pythonPathField.setPreferredSize(new Dimension(400,30));
      Box pythonBox = Box.createHorizontalBox();
      JButton pythonDirButton = new JButton();
      pythonDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      pythonDirButton.setToolTipText("Select python directory");
      pythonDirButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            pythonDirChooserButtonActionPerformed();
         }
      });
      pythonBox.add(pythonLabel);
      pythonBox.add(Box.createRigidArea(new Dimension(5,0)));
      pythonBox.add(Box.createHorizontalGlue());
      pythonBox.add(pythonPathField);
      pythonBox.add(Box.createRigidArea(new Dimension(5,0)));
      pythonBox.add(pythonDirButton);

      dirBox.add(pythonBox);
      dirBox.add(Box.createRigidArea(new Dimension(0,5)));
      dirBox.add(Box.createVerticalGlue());

      Box rssBox = Box.createHorizontalBox();
      rssFeedURLField = new JTextField(100);
      rssFeedURLField.setMaximumSize(new Dimension(400,30));
      rssFeedURLField.setMinimumSize(new Dimension(400,30));
      rssFeedURLField.setPreferredSize(new Dimension(400,30));
      rssBox.add(rssFeedLabel);
      rssBox.add(Box.createHorizontalGlue());
      rssBox.add(Box.createRigidArea(new Dimension(5,0)));
      rssBox.add(rssFeedURLField);
      
      dirPanel.add(dirBox);
      dirPanel.add(rssBox);

      JPanel listPanel = new JPanel();
      listPanel.setBorder(BorderFactory.createTitledBorder("ID3 Tag Customisation"));
      listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
           
      Box levelBox = Box.createVerticalBox();
      Box lessonLabelBox = Box.createHorizontalBox();
      lessonListLabel = new JLabel("Lesson (Album) Tags");
      lessonLabelBox.add(lessonListLabel);
      lessonLabelBox.add(Box.createHorizontalGlue());
      lessonListModel = new DefaultListModel();
      lessonLevelsList = new JList(lessonListModel);
      lessonLevelsList.setPreferredSize(new Dimension(350,350));
      //lessonLevels = new Vector();
      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
            {
               if (lessonListModel.size() > 0)
               {
                  int index = lessonLevelsList.locationToIndex(e.getPoint());
                  String level = (String) lessonListModel.get(index);
                  lessonTextField.setText(level);
               }
            }
          }
      };
      lessonLevelsList.addMouseListener(mouseListener);
      lessonScroller = new JScrollPane(lessonLevelsList);
      lessonTextField = new JTextField(30);
      lessonTextField.setMaximumSize(new Dimension(300,30));
      lessonTextField.setMinimumSize(new Dimension(250,30));
      levelBox.add(lessonLabelBox);
      levelBox.add(lessonScroller);
      levelBox.add(Box.createVerticalGlue());
     
      Box levelEntryBox = Box.createHorizontalBox();
      
      levelEntryBox.add(lessonTextField);
      levelEntryBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/edit_add.png");
      ImageIcon addLevelIcon = new ImageIcon(imageURL);
      
      JButton levelAddButton = new JButton(addLevelIcon);
      levelAddButton.setMaximumSize(new Dimension(50,25));
      levelAddButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            String newLevel = lessonTextField.getText();
            if (newLevel != null)
            {
               lessonListModel.addElement(newLevel);
            }
         }
      });
      levelEntryBox.add(levelAddButton);
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/edit_remove.png");
      ImageIcon delLevelIcon = new ImageIcon(imageURL);
      
      JButton levelDeleteButton = new JButton(delLevelIcon);
      levelDeleteButton.setMaximumSize(new Dimension(50,25));
      levelDeleteButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            int index = lessonLevelsList.getSelectedIndex();
            if (index > -1)
            {
               lessonListModel.remove(index);
            }
         }
      });
      levelEntryBox.add(levelDeleteButton);
      //levelEntryBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      levelBox.add(Box.createRigidArea(new Dimension(0,10)));
      levelBox.add(levelEntryBox);
      
      Box artistBox = Box.createVerticalBox();
      Box artistLabelBox = Box.createHorizontalBox();
      artistListLabel = new JLabel("Artist Tags");
      artistLabelBox.add(artistListLabel);
      artistLabelBox.add(Box.createHorizontalGlue());
      //artistListLabel.setAlignmentX((float) 1.0);
      //artistListLabel.setAlignmentY(LEFT_ALIGNMENT);
      artistListModel = new DefaultListModel();
      artistList = new JList(artistListModel);
      artistList.setPreferredSize(new Dimension(350,350));
      //artists = new Vector();
      MouseListener artistMouseListener = new MouseAdapter()
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
            {
               if (artistListModel.size() > 0)
               {
                  int index = artistList.locationToIndex(e.getPoint());
                  String artistName = (String) artistListModel.get(index);
                  artistTextField.setText(artistName);
               }
            }
          }
      };
      artistList.addMouseListener(artistMouseListener);
      artistScroller = new JScrollPane(artistList);
      artistTextField = new JTextField(30);
      artistTextField.setMaximumSize(new Dimension(350,30));
      artistTextField.setMinimumSize(new Dimension(250,30));
      artistBox.add(artistLabelBox);
      artistBox.add(artistScroller);
      artistBox.add(Box.createVerticalGlue());
      
      Box artistEntryBox = Box.createHorizontalBox();
      
      artistEntryBox.add(artistTextField);
      artistEntryBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/edit_add.png");
      ImageIcon addIcon = new ImageIcon(imageURL);
      
      JButton artistAddButton = new JButton(addIcon);
      artistAddButton.setMaximumSize(new Dimension(50,25));
      artistAddButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            String newArtist = artistTextField.getText();
            if (newArtist != null)
            {
               artistListModel.addElement(newArtist);
            }
         }
      });
      artistEntryBox.add(artistAddButton);
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/edit_remove.png");
      ImageIcon delIcon = new ImageIcon(imageURL);
      JButton artistDeleteButton = new JButton(delIcon);
      artistDeleteButton.setMaximumSize(new Dimension(50,25));
      artistDeleteButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            int index = artistList.getSelectedIndex();
            if (index > -1)
            {
               artistListModel.remove(index);
            }
         }
      });
      artistEntryBox.add(artistDeleteButton);
      //artistEntryBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      artistBox.add(Box.createRigidArea(new Dimension(0,10)));
      artistBox.add(artistEntryBox);
      
   
      listPanel.add(levelBox);
      listPanel.add(Box.createRigidArea(new Dimension(10,0)));
      listPanel.add(Box.createHorizontalGlue());
      listPanel.add(artistBox);
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
      
      saveButton = new JButton("Save");
      saveButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveConfig();
         }
      });
      buttonBox.add(saveButton);
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      closeButton = new JButton("Cancel");
      closeButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            hideDialog();
         }
      });
      buttonBox.add(closeButton);
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
      
     
      contentPanel.add(dirPanel);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      contentPanel.add(listPanel);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      contentPanel.add(buttonBox);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      cp.add(contentPanel);
      
      //loadConfig();
   }

   public void setText(String newText)
   {
     
   }
   
   public void showDialog()
   {
       loadConfig();
       setVisible(true);
   }
    
   
   public void hideDialog()
   {
      setVisible(false);
   }
   
   private void saveConfig()
   {
      //update config and write new config file
      seePodConfig.setWLCPDirectory(wlcpDirectoryField.getText());
      seePodConfig.setWLCPScript(wlcpScriptField.getText());
      seePodConfig.setLessonDirectory(lessonDirectoryField.getText());
      seePodConfig.setPythonPath(pythonPathField.getText());
      seePodConfig.setRSSFeedURL(rssFeedURLField.getText());
      
      Vector lessons = new Vector();
     
      for (int i = 0; i < lessonListModel.getSize(); i++)
      {
         lessons.add((String) lessonListModel.get(i));
         //Debug.debug("ConfigDialog.saveConfig() - setting lesson levels: ", lessons.get(i));
      }
      seePodConfig.setTagContent(lessons, Config.ID3_ALBUM_NAME_KEY);
      
      Vector artistsStr = new Vector();
     
      for (int i = 0; i < artistListModel.getSize(); i++)
      {
         artistsStr.add((String) artistListModel.get(i));
         //Debug.debug("ConfigDialog.saveConfig() - setting artists: ", artistsStr.get(i));
      }
      seePodConfig.setTagContent(artistsStr, Config.ID3_ARTIST_NAME_KEY);
      mp3panel.resetComboBoxItems();
      seePodConfig.saveConfigFile();
      
      setVisible(false);
   }

   private void loadConfig()
   {
      //first the directory stuff
      wlcpDirectoryField.setText(seePodConfig.getWLCPDirectory());
      wlcpScriptField.setText(seePodConfig.getWLCPScript());
      lessonDirectoryField.setText(seePodConfig.getLessonDirectory());
      pythonPathField.setText(seePodConfig.getPythonPath());
      rssFeedURLField.setText(seePodConfig.getRSSFeedURL());
      //now the id3 tags
      lessonLevels = seePodConfig.getTagContent(Config.ID3_ALBUM_NAME_KEY);
      lessonListModel.removeAllElements();
      if (lessonLevels != null)
      {
         for (int i = 0; i < lessonLevels.size(); i++)
         {
            lessonListModel.addElement(lessonLevels.get(i));
            //Debug.debug("ConfigDialog.loadConfig() - setting lesson level: ", lessonLevels.get(i));
         }
      }
      artists = seePodConfig.getTagContent(Config.ID3_ARTIST_NAME_KEY);
      artistListModel.removeAllElements();
      if (artists != null)
      {
         for (int i = 0; i < artists.size(); i++)
         {
            artistListModel.addElement(artists.get(i));
            //Debug.debug("ConfigDialog.loadConfig() - setting artist name: ", artists.get(i));
         }
      }      
   }

   private void wlcpDirChooserButtonActionPerformed()
   {
        fc = new JFileChooser("WLCP Directory");

        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //Show it.
        int returnVal = fc.showOpenDialog(this);

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File selectedFolder = fc.getSelectedFile();
            String wlcpPath = selectedFolder.getPath();
            wlcpDirectoryField.setText(wlcpPath);
            seePodConfig.setWLCPDirectory(wlcpPath);
        }
        else
        {
           if (Config.DEBUG)
              Debug.debug("ConfigDialog.wlcpDirChooserButtonActionPerformed(): cancelled file selection.");
        }

   }

   private void scriptDirChooserButtonActionPerformed()
   {
        fc = new JFileChooser("WLCP Script");

        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //Show it.
        int returnVal = fc.showOpenDialog(this);

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File selectedFolder = fc.getSelectedFile();
            String wlcpPath = selectedFolder.getPath();
            wlcpScriptField.setText(wlcpPath);
            seePodConfig.setWLCPScript(wlcpPath);
        }
        else
        {
           if (Config.DEBUG)
              Debug.debug("ConfigDialog.scriptDirChooserButtonActionPerformed(): cancelled file selection.");
        }
   }

   private void lessonDirChooserButtonActionPerformed()
   {

        fc = new JFileChooser("Lesson Directory");

        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //Show it.
        int returnVal = fc.showOpenDialog(this);

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File selectedFolder = fc.getSelectedFile();
            String lessonPath = selectedFolder.getPath();
            lessonDirectoryField.setText(lessonPath);
            seePodConfig.setLessonDirectory(lessonPath);
        }
        else
        {
           if (Config.DEBUG)
              Debug.debug("ConfigDialog.lessonDirChooserButtonActionPerformed(): cancelled file selection.");
        }
   }

   private void pythonDirChooserButtonActionPerformed()
   {
        fc = new JFileChooser("Python Directory");

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //Show it.
        int returnVal = fc.showOpenDialog(this);

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File selectedFolder = fc.getSelectedFile();
            String pythonPath = selectedFolder.getPath();
            pythonPathField.setText(pythonPath);
            seePodConfig.setPythonPath(pythonPath);
        }
        else
        {
           if (Config.DEBUG)
              Debug.debug("ConfigDialog.pythonDirChooserButtonActionPerformed(): cancelled file selection.");
        }
   }

   public void resetUI()
   {
      fc = new JFileChooser();
   }
}