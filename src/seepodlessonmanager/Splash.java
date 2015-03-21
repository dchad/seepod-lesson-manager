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
 * Class  : SPlash
 * 
 * Description: Display splash screen on start up.
 * 
 */

package seepodlessonmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class Splash extends JWindow
{
   private JPanel content;
   private int width;
   private int height;
   private Dimension screen;
   
   public Splash()
   {
      super();
      content = (JPanel) getContentPane();
      
      width = 640;
      height = 480;
      
      screen = Toolkit.getDefaultToolkit().getScreenSize();
            
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      
      setBounds(x, y, width, height);
      
      java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/seepod-splash-002.jpg");
      JLabel label = new JLabel(new ImageIcon(imageURL));
  
      content.add(label, BorderLayout.CENTER);
 
      content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      
      addMouseListener(new MouseAdapter()
      {
           @Override
           public void mousePressed(MouseEvent e)
           {
               setVisible(false);
               dispose();
           }
      });
      //setAlwaysOnTop(true);
      showWindow(5000);
   }
   
   public void showWindow(int duration)
   {
     
       final int pause = duration;
       final Runnable closerRunner = new Runnable()
       {
                public void run()
                {
                    setVisible(false);
                    dispose();
                }
        };
        Runnable waitRunner = new Runnable()
        {
                public void run()
                {
                    try
                    {
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                    }
                    catch(Exception e)
                    {
                            e.printStackTrace();
                            // can catch InvocationTargetException
                            // can catch InterruptedException
                    }
                }
        };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();

   }
}