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
 * Class  : RSSDialog
 * 
 * Description: JDialog to display the ChinesePod.com RSS feed lesson item summary.
 * 
 * 
 */


package seepodlessonmanager;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class RSSDialog extends JDialog
{
   String lessonSummary;
   JEditorPane summaryArea;
   JPanel contentPanel;
   JButton closeButton;
   Container cp;
   Font dispFont;
   
   public RSSDialog(Frame aFrame, JPanel parent) 
   {
      super(aFrame, "ChinesePod.com RSS Feed", false);
      //setUndecorated(true);

      cp = getContentPane();
     
      setSize(800,600);
     
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(800,600));
      contentPanel.setBorder(BorderFactory.createTitledBorder("RSS Item Summary"));
      
      JScrollPane scrollPane = new JScrollPane();

      dispFont = new Font("Dialog", Font.BOLD, 16);
      summaryArea = new JEditorPane();
      summaryArea.setContentType("text/html");
      summaryArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      summaryArea.setFont(dispFont);

      scrollPane.getViewport().add(summaryArea);
      //scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
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
      summaryArea.setText(newText);
   }
   
    public void showDialog(String displayText)
   {
       summaryArea.setText(displayText);
       setVisible(true);
   }
    
   public void showDialog(RSSLesson feedItem)
   {
      String htmlText = feedItem.getLessonSummary();
      
      if (htmlText != null)
      {

         String summary = "<html><b><p>Lesson Summary: " + feedItem.getLessonNumber() + " - " + feedItem.getLessonName() + "</p></b>" + feedItem.getLessonSubtitle() + htmlText + "</html>";
         //int fontHeight = getFontMetrics(dispFont).getHeight();
         //int stringWidth = getFontMetrics(dispFont).stringWidth(summary);
         //int linesCount = (int) Math.floor(stringWidth / 750);
         //linesCount = Math.max(1, linesCount + 3);
         //summaryArea.setPreferredSize(new Dimension(750, (fontHeight+2)*linesCount*2));

         summaryArea.setText(summary);
         summaryArea.setCaretPosition(0);
         
      }
      else
      {
         Debug.debug("RSSDialog.showDialog() - Lesson summary is null.");
      }
      setVisible(true);
   }
   
   public void hideDialog()
   {
      setVisible(false);
   }
   

}