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
 * Description: JPanel to display lesson pdf files.
 *              
 * Note: This was originally displayed in a JDialog, but the JScrollpane would not work, apparently
 *       scrolling a component that does custom painting will only work in a JFrame or JWindow.
 * 
 */


package seepodlessonmanager;

import com.sun.pdfview.PDFRenderer;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class PDFSimpleView extends JPanel
{
   private PDFFile pdffile;
   private RandomAccessFile raf;
   private FileChannel channel;
   private ByteBuffer buf;
   private PDFPage[] page;
   private Graphics2D g2;
   //private BufferedImage img;
   private int pages;
   private int currentPage;   
   private int width;
   private int height;
   private DrawingPane dp;
   private JTextField pageNo;
   
   public PDFSimpleView() 
   {
      super();   
      setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(500,600));
      
      dp = new DrawingPane();
      width = 800;
      height = 1000;
      
      JScrollPane scroller = new JScrollPane(dp);
      scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
      scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroller.setPreferredSize(new Dimension(400,500));
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createRigidArea(new Dimension(25,0)));
      
      JLabel pageNoLabel = new JLabel("Page Number: ");
      pageNo = new JTextField(3);
      pageNo.setEditable(false);
      pageNo.setMaximumSize(new Dimension(50,30));
      buttonBox.add(pageNoLabel);
      buttonBox.add(Box.createRigidArea(new Dimension(10,0)));
      buttonBox.add(pageNo);
      buttonBox.add(Box.createRigidArea(new Dimension(50,0)));
      
      java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/back.png");
      ImageIcon prevIcon = new ImageIcon(imageURL);
      
      JButton prevButton = new JButton(prevIcon);
      prevButton.setToolTipText("Previous page.");
      prevButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            prevPage();
         }
      });
      buttonBox.add(prevButton);
      //buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(25,0)));
      
      
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/forward.png");
      ImageIcon nextIcon = new ImageIcon(imageURL);
      
      JButton nextButton = new JButton(nextIcon);
      nextButton.setToolTipText("Next page.");
      nextButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            nextPage();
         }
      });
      buttonBox.add(nextButton);
      //buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(25,0)));
          
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/viewmagin.png");
      ImageIcon zoomInIcon = new ImageIcon(imageURL);
      
      JButton zoomInButton = new JButton(zoomInIcon);
      zoomInButton.setToolTipText("Zoom in.");
      zoomInButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            zoomIn();
         }
      });
      buttonBox.add(zoomInButton);
     // buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(25,0)));
          
      imageURL = Splash.class.getResource("/seepodlessonmanager/images/viewmagout.png");
      ImageIcon zoomOutIcon = new ImageIcon(imageURL);
      
      JButton zoomOutButton = new JButton(zoomOutIcon);
      zoomOutButton.setToolTipText("Zoom out.");
      zoomOutButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            zoomOut();
         }
      });
      buttonBox.add(zoomOutButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
    
      add(buttonBox);
      add(Box.createRigidArea(new Dimension(0,10)));
      add(scroller);
   }
   
   public void open(File pdf)
   {
            
      try
      {
         raf = new RandomAccessFile(pdf, "r");        
         channel = raf.getChannel();
         buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
         pdffile = new PDFFile(buf);
      } catch (FileNotFoundException ex) {
        Debug.debug("PDFSimpleView.open(): file not found error.");
      } catch (IOException ex) {
        Debug.debug("PDFSimpleView.open(): IO exception.");
      } 
  
      pages = pdffile.getNumPages();
      
      pageNo.setText(String.valueOf(1));
      
      page = new PDFPage[pages];
      for (int i = 0; i < pages; i++)
      {
         page[i] = pdffile.getPage(i+1); 
      }
          
      //img = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_ARGB);
      currentPage = 0;
   }

   public class DrawingPane extends JLabel
   {
      public void DrawingPane()
      {
         // nothing to do yet
      }
      
      @Override
      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);	   // erases background
         g2 = (Graphics2D)g;   //cast for java2

         renderPDF();
         revalidate();
      }
      private void renderPDF()
      {

         //g2.drawImage(img, null, 0, 0);
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         Rectangle rect = new Rectangle(0, 0, width, height);
         scrollRectToVisible(rect);
         PDFRenderer renderer = new PDFRenderer(page[currentPage], g2, rect, null, Color.WHITE);
         try 
         {
            page[currentPage].waitForFinish();
         } catch (InterruptedException ex) {
            Logger.getLogger(PDFSimpleView.class.getName()).log(Level.SEVERE, null, ex);
         }
         renderer.run();

         setPreferredSize(new Dimension(width, height));
      }
   }
   

   
   public void nextPage()
   {
      if (currentPage < (pages - 1))
      {
         currentPage++;
         //Debug.debug("Painting next page...", currentPage);
         pageNo.setText(String.valueOf(currentPage+1));
         //revalidate();
         repaint(); 
      }
   }
   
   public void prevPage()
   {
      if (currentPage > 0)
      {
         currentPage--;
         //Debug.debug("Painting previous page...", currentPage);
         pageNo.setText(String.valueOf(currentPage+1));
         //revalidate();
         repaint();
      }
   }
   
   public void zoomIn()
   {
      if (width < 1200)
      {
         width += 100;
         height += 100;
         repaint();
      }
   }
   
   public void zoomOut()
   {
      if (width > 400)
      {
         width -= 100;
         height -= 100;
         repaint();
      }
   }
   
}