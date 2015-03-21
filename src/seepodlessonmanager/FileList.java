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
 * Class  : FileList 
 * 
 * Description: Uses a JList to view mp3 files in a directory selected by the user.
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class FileList extends JPanel 
{
  private static JList fileList;   // To display the directory contents
  private static DefaultListModel fileListModel;
  private static File currentDir;  // Absolute path of the directory currently listed
  private static FilenameFilter filter; // An optional filter for the directory
  private static String[] files;   // The directory contents
  private DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
  private MP3Editor mp3;
  private JPopupMenu popup;
  private ID3TagSet id3Tags;
  private ID3TagSet copyTags;
  private TagDialogue tagDialog;
  private Config seePodConfig;
  
  public FileList(MP3Editor mp3Panel, Config spc)
  {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("MP3 Files"));

    if (spc != null)
    {
       seePodConfig = spc;
    }
    else
    {
       Debug.debug("FileList() - Config is null.");
       return;
    }

    final String suffix = ".mp3";
    filter = new FilenameFilter() 
    {
          @Override
          public boolean accept(File dir, String name) 
          {
            if (name.endsWith(suffix))
              return true;
            else
              return false;
          }
    };
    
    tagDialog = new TagDialogue(seePodConfig.getAppFrame());
    
    mp3 = mp3Panel;
    fileListModel = new DefaultListModel();
    fileList = new JList(fileListModel);
    
    createPopupMenu();

    MouseListener mouseListener = new MouseAdapter() 
    {
     @Override
     public void mouseClicked(MouseEvent e) 
     {
         if (fileList.getVisibleRowCount() == 0)
         {
             return;
         }
         if (e.getClickCount() == 1)
         {
             int index = fileList.locationToIndex(e.getPoint());
             //Debug.debug("FileList.mouseClicked() - Clicked on file: " + index);
             File selectedFile = new File(currentDir, (String)fileList.getSelectedValue());
             
             if (selectedFile.isFile())
             {
                mp3.setMP3File(selectedFile.getAbsolutePath());
             }
             else
             {
                Debug.debug("Selected file does not exist: " + selectedFile.getAbsolutePath());
             }
          }
      }
     
      @Override
      public void mousePressed(MouseEvent e) 
      {
          if (fileList.getVisibleRowCount() == 0)
          {
             return;
          }
          if (e.isPopupTrigger())
          {
             int index = fileList.locationToIndex(e.getPoint());
             //Debug.debug("FileList.mousePressed() - Clicked on file: " + index);
             fileList.setSelectedIndex(index);
             File selectedFile = new File(currentDir, (String)fileList.getSelectedValue()); 
             if (selectedFile.isFile())
             {
                id3Tags = new ID3TagSet(selectedFile);
             }
             else
             {
                Debug.debug("FileList.mousePressed() - Selected file does not exist: " + selectedFile.getAbsolutePath());
             }
             popup.show(e.getComponent(), e.getX(), e.getY());
          }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) 
      {
          if (fileList.getVisibleRowCount() == 0)
          {
             return;
          }
          if (e.isPopupTrigger())
          {
             int index = fileList.locationToIndex(e.getPoint());
             //Debug.debug("FileList.mouseReleased() - Clicked on file: " + index);
             fileList.setSelectedIndex(index);
             File selectedFile = new File(currentDir, (String)fileList.getSelectedValue()); 
             if (selectedFile.isFile())
             {
                id3Tags = new ID3TagSet(selectedFile);
             }
             else
             {
                Debug.debug("FileList.mouseReleased() - Selected file does not exist: " + selectedFile.getAbsolutePath());
             }
             popup.show(e.getComponent(), e.getX(), e.getY());
          }
      }
     };
     fileList.addMouseListener(mouseListener);
     
    // Lastly, put the JList into a JScrollPane.
    JScrollPane scrollpane = new JScrollPane();
    scrollpane.getViewport().add(fileList);
    add(BorderLayout.CENTER, scrollpane);
 }

 public Vector getSelectedFiles() throws IOException
 {  //this is a multiple interval selection mode list
    
    Vector selectedFiles = new Vector();

    int startIndex = fileList.getMinSelectionIndex();
    int endIndex = fileList.getMaxSelectionIndex();

    if (startIndex == endIndex)    //only one item selected
    {
       String filePath = currentDir.getCanonicalPath() + File.separator + fileList.getSelectedValue();
       selectedFiles.add(filePath);
       //Debug.debug("FileList.getSelectedFiles() - selected: ", filePath);
    }
    else
    {
       Object[] selectedValues = fileList.getSelectedValues();
       for (int i = 0; i < selectedValues.length; i++)
       {
          String filePath = currentDir.getCanonicalPath() + File.separator + selectedValues[i];
          selectedFiles.add(filePath);
          //Debug.debug("FileList.getSelectedFiles() - selected: ", filePath);
       }
    }
    
    return selectedFiles;
 }

 private void createPopupMenu()
 {
     JMenuItem menuItem;
     popup = new JPopupMenu();

     menuItem = new JMenuItem("Show All ID3 Tags");
     menuItem.addActionListener(new java.awt.event.ActionListener()
     {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            if ((id3Tags != null) && id3Tags.hasID3v2Tags())
            {
               String allTags = id3Tags.showAllTags();
               //popup a dialog to display the tags
               tagDialog.showDialog(allTags);
            }
            else
            {
               JOptionPane.showMessageDialog(null, "No ID3 tags found in file.", "Warning", JOptionPane.ERROR_MESSAGE);
            }
         }
     });
     popup.add(menuItem);
     menuItem = new JMenuItem("Copy ID3 Tags");
     menuItem.addActionListener(new java.awt.event.ActionListener()
     {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {

            File selectedFile = new File(currentDir, (String)fileList.getSelectedValue());
            if (selectedFile.isFile())
            {
               copyTags = new ID3TagSet(selectedFile);
            }
            else
            {
               Debug.debug("FileList.copyID3Tags() - Selected file does not exist: " + selectedFile.getAbsolutePath());
            }


         }
     });
     popup.add(menuItem);
     menuItem = new JMenuItem("Paste ID3 Tags");
     menuItem.addActionListener(new java.awt.event.ActionListener()
     {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {

            if ((copyTags != null) && copyTags.hasID3v2Tags())
            {
               File selectedFile = new File(currentDir, (String)fileList.getSelectedValue());
               if (selectedFile.isFile())
               {

                   ID3TagSet newTags = new ID3TagSet(selectedFile);
                   newTags.copyID3Tags(copyTags.getID3Tags());
                   newTags.writeID3Tags();
                   mp3.setMP3File(selectedFile.getAbsolutePath());
                   mp3.printStatusMessage("Updated tags in file: " + selectedFile.getName());

               }
               else
               {
                  JOptionPane.showMessageDialog(null, "No ID3 tags found in file.", "Warning", JOptionPane.ERROR_MESSAGE);
                  Debug.debug("FileList.pasteID3Tags() - Selected file does not exist: " + selectedFile.getAbsolutePath());
               }

            }
         }
     });
     popup.add(menuItem);
 }

 public static void listDirectory(String directory) 
 {
    // Convert the string to a File object, and check that the dir exists
    File dir = new File(directory);
    if (!dir.isDirectory())
    {
       Debug.debug("FileList.listDirectory() - not a directory: ", directory);
       return;
    }

    // Get the (filtered) directory entries
    files = dir.list(filter);

    // Sort the list of filenames.
    if (files.length > 0)
    {
       java.util.Arrays.sort(files);
    }
    
    fileList.setListData(files);
    
    currentDir = dir;
  }
  
  public static void clearList() 
  {
     fileListModel.removeAllElements();
     if (Config.DEBUG)
        Debug.debug("FileList.clearList() - Cleared file list.");
  }

  public void resetUI()
  {
     createPopupMenu();
     tagDialog = new TagDialogue(seePodConfig.getAppFrame());
     if (Config.DEBUG)
        Debug.debug("FileList.resetUI() - created new popup menus.");
  }

   @Override
  public Dimension getMinimumSize() 
   {
    return new Dimension(200, 400);
  }

   @Override
  public Dimension getPreferredSize() 
   {
    return new Dimension(400, 800);
  }

  
}

