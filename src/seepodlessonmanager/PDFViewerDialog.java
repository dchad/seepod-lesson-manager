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
 * Class  : PDFViewerDialog
 * 
 * Description: JFrame to display lesson pdf files.
 *              
 * Note: This was originally a JDialog, but the JScrollpane in PDFSimpleView would not work, apparently
 *       scrolling a component that does custom painting will not work in a JDialog!!! Only works
 *       in a JFrame or JWindow.
 */


package seepodlessonmanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PDFViewerDialog extends JFrame
{
   private JPanel contentPanel;
   private Container cp;
   private PDFSimpleView viewer;
   private int width;
   private int height;
   private Dimension screen;
   
   public PDFViewerDialog()  //do not need these parameters now, not a JDialog anymore
   {
      super("SeePod PDF Viewer");
      
      cp = getContentPane();
     
      width = 840;
      height = 800;
      
      screen = Toolkit.getDefaultToolkit().getScreenSize();
            
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      
      setBounds(x, y, width, height);
      
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(800,600));
      contentPanel.setBorder(BorderFactory.createTitledBorder("PDF Viewer"));
      
      viewer = new PDFSimpleView();
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
      buttonBox.add(Box.createHorizontalGlue());
      
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            hideDialog();
         }
      });
      buttonBox.add(closeButton);
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
       
      contentPanel.add(viewer, BorderLayout.CENTER);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      contentPanel.add(buttonBox);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      cp.add(contentPanel);
      
   }
   
   
   public void setText(String newText)
   {
      //tagArea.setText(newText);
   }
   
   public void showDialog(File pdf)
   {
      if (pdf == null)
      {
         Debug.debug("PDFViewDialog.showDialog() - invalid PDF file.");
         return;
      }

      viewer.open(pdf);
      
      setVisible(true);
   }
   
   public void hideDialog()
   {
      setVisible(false);
   }
   
   
   
}