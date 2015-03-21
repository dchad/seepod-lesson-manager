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
 * Class  : AboutDialog
 * 
 * Description: Dialogue to display SeePod info.
 * 
 * 
 */


package weepod;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDialog extends JDialog
{
   private Container cp;
   private int width;
   private int height;
   private Dimension screen;
   
   public AboutDialog(JFrame frame)
   {
      super(frame, "About", false);
      
      cp = getContentPane();
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      
      //now add an image 
      width = 800;
      height = 600;
      
      screen = Toolkit.getDefaultToolkit().getScreenSize();
            
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      
      setBounds(x, y, width, height);
      setSize(650,580);
      
      java.net.URL imageURL = Splash.class.getResource("/weepod/images/weepod-splash-logo-001.jpg");
      JLabel label = new JLabel(new ImageIcon(imageURL));
 
      Box contentPanel = Box.createHorizontalBox();
      contentPanel.addMouseListener(new MouseAdapter()
      {
           @Override
           public void mousePressed(MouseEvent e)
           {
               setVisible(false);
               dispose();
           }
      });
      
      contentPanel.add(Box.createHorizontalGlue());
      contentPanel.add(label);
      contentPanel.add(Box.createHorizontalGlue());

      Box versionBox = Box.createHorizontalBox();
      JLabel versionLabel = new JLabel("WeePod WeLoveChinesePod GUI Beta 0.7");
      versionBox.add(Box.createHorizontalGlue());
      versionBox.add(versionLabel);
      versionBox.add(Box.createHorizontalGlue());
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
  
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            setVisible(false);
            dispose();
         }
      });
      buttonBox.add(closeButton);
      buttonBox.add(Box.createRigidArea(new Dimension(30,0)));
      
      cp.add(contentPanel);
      cp.add(Box.createRigidArea(new Dimension(0,10)));
      cp.add(versionBox);
      cp.add(Box.createRigidArea(new Dimension(0,10)));
      cp.add(buttonBox);
      cp.add(Box.createRigidArea(new Dimension(0,10)));
      setVisible(true);
      
   }
   
}