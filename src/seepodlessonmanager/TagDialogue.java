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
 * Class  : TagDialog
 * 
 * Description: JDialog to display all ID3 tags in a file.
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class TagDialogue extends JDialog
{
   private String tagSummary;
   private JEditorPane tagArea;
   private JPanel contentPanel;
   private JButton closeButton;
   private JButton saveButton;
   private Container cp;
   private String tags;

   public TagDialogue(Frame aFrame) 
   {
      super(aFrame, "ID3 Tag List", false);
      //setUndecorated(true);

      cp = getContentPane();
     
      setSize(800,600);
     
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(800,600));
      //setMinimumSize(new Dimension(700,600));
      contentPanel.setBorder(BorderFactory.createTitledBorder("ID3 Tag List"));
      
      JScrollPane scrollPane = new JScrollPane();
      
      tagArea = new JEditorPane();
      tagArea.setContentType("text/plain");
      //summaryArea.setLineWrap(true);
      scrollPane.getViewport().add(tagArea);
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
      
      saveButton = new JButton("Save");
      saveButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveTags();
         }
      });
      buttonBox.add(saveButton);
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));

      closeButton = new JButton("Close");
      closeButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            hideDialog();
         }
      });
      buttonBox.add(closeButton);
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
      
      
      contentPanel.add(scrollPane);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      contentPanel.add(buttonBox);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      cp.add(contentPanel);
   }

   public void setText(String newText)
   {
      tagArea.setText(newText);
   }
   
   public void showDialog(String tagText)
   {
       tags = tagText;
       tagArea.setText(tagText);
       tagArea.setCaretPosition(0);
       setVisible(true);
   }
   
   public void hideDialog()
   {
      setVisible(false);
   }

   private void saveTags()
   {
       try 
       {
          PrintWriter wout = new PrintWriter("temptags.txt", "UTF-8"); //line output
          wout.print(tags);
          wout.close();
       }
       catch (IOException e) {
          if (Config.DEBUG)
             Debug.debug("TagDialogue.saveTags() - Could not write tags file.");
       }
       hideDialog();
   }

}