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
 * Class  : RssFeed
 * 
 * Description: JPanel to display the ChinesePod.com RSS feed.
 * 
 * 
 */


package seepodlessonmanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class RssFeed extends JPanel 
{   
   private JList feedList;
   private DownloadStatus dlStatus;
   private DefaultListModel fileListModel;
   private Thread rssThread;
   private RSSPod rssFeed;
   private WLCPLessonDownloadList wlcpLessonList;
   private Vector cpodLessonList;
   private WLCPController wlcpControl;
   private JPopupMenu popup;
   private RSSDialog summaryDialog;
   private Config seePodConfig;
   private int index;   
   private JFileChooser fc;
   private File rssFeedList;
   private Font rssFont;
   private HashMap rssMap;
   private String databaseDirectory;
   
   public RssFeed(WLCPLessonDownloadList wll, WLCPController wlcpc, Config spc, DownloadStatus dls) 
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(600,600));
      setBorder(BorderFactory.createTitledBorder("ChinesePod RSS Feed"));
      
      if (spc != null)
      {
         seePodConfig = spc;
         summaryDialog = new RSSDialog(seePodConfig.getAppFrame(), this);
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("RssFeed() - Config object is null.");
         return;
      }
      if (dls != null)
      {
         dlStatus = dls;
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("RssFeed() - Download status object is null.");
         return;
      }
      if (wll != null)
      {
         wlcpLessonList = wll;
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("RssFeed() - WLCP lesson list object is null.");
         return;
      }        
      if (wlcpc != null)
      {
         wlcpControl = wlcpc;
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("RssFeed() - WLCP control object is null.");
         return;
      }
  
      rssFont = new Font("Dialog", Font.BOLD, 16);
      
      fileListModel = new DefaultListModel();
      
      cpodLessonList = new Vector();

      rssMap = new HashMap<String,String>();
      
      databaseDirectory = seePodConfig.getDatabaseDirectory();
      rssFeedList = new File(databaseDirectory + File.separator + Config.SP_RSS_FEED_LIST);
      if (!rssFeedList.exists())
      {
            try 
            {
                rssFeedList.createNewFile();
            } catch (IOException ex) {
                Debug.debug("RssFeed() - Could not create feed list file: ", rssFeedList.getAbsoluteFile());
            }
      }
      else
      {
          loadRSSFeedList();
      }

      feedList = new JList(fileListModel); //(rows,columns)

      RSSListRenderer listRend = new RSSListRenderer();

      feedList.setCellRenderer(listRend);
      
      createPopupMenu();
      
      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1)
            {
               if (fileListModel.size() > 0)
               {
                  index = feedList.locationToIndex(e.getPoint());
                  RSSLesson cpodLesson = (RSSLesson) cpodLessonList.get(index);
                  wlcpControl.setRSSItemInfo(cpodLesson);  
               }
            }
            if (e.getClickCount() == 2) 
            {
               if (fileListModel.size() > 0)
               {
                  index = feedList.locationToIndex(e.getPoint());
                  RSSLesson cpodLesson = (RSSLesson) cpodLessonList.get(index);
                  wlcpLessonList.addRSSLesson(cpodLesson.getLessonName());
               }
            }
          }
         
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               if (fileListModel.size() > 0)
               {
                   index = feedList.locationToIndex(e.getPoint());
                   //RSSLesson cpodLesson = (RSSLesson) cpodLessonList.get(index);
                   popup.show(e.getComponent(), e.getX(), e.getY());
               }
            }
        }
      
        @Override
        public void mouseReleased(MouseEvent e) 
        {
            if (e.isPopupTrigger())
            {
               if (fileListModel.size() > 0)
               {
                  index = feedList.locationToIndex(e.getPoint());
                  //RSSLesson cpodLesson = (RSSLesson) cpodLessonList.get(index);
                  popup.show(e.getComponent(), e.getX(), e.getY());
               }
            }
        }
      
      };
      feedList.addMouseListener(mouseListener);

      // Lastly, put the JList into a JScrollPane.
      JScrollPane scrollpane = new JScrollPane();
      scrollpane.getViewport().add(feedList);
      
      JButton startRSSFeedButton = new JButton("Start");
      startRSSFeedButton.setToolTipText("Start your ChinesePod RSS feed download.");
      startRSSFeedButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            startRSSFeed();
         }
      });
      
      JButton stopRSSFeedButton = new JButton("Stop");
      stopRSSFeedButton.setToolTipText("Stop your ChinesePod RSS feed download.");
      stopRSSFeedButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            stopRSSFeed();
         }
      });
      
      JButton saveRSSFeedButton = new JButton("Save");
      saveRSSFeedButton.setToolTipText("Save your ChinesePod RSS feed items to a file.");
      saveRSSFeedButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveRSSFeed();
         }
      });
      
      Box rssButtonBox = Box.createHorizontalBox();
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(startRSSFeedButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(stopRSSFeedButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      rssButtonBox.add(saveRSSFeedButton);
      rssButtonBox.add(Box.createHorizontalGlue());
      
      add(BorderLayout.CENTER, scrollpane);
      add(Box.createRigidArea(new Dimension(0,10)));
      add(rssButtonBox);
      add(Box.createRigidArea(new Dimension(0,5)));
      
   }
   
   public synchronized void addLessonTitle(RSSLesson cpodLesson, Boolean updating, int position)
   {
      Boolean addLesson = false;
      
      if (cpodLesson != null)
      {

         if (wlcpControl.getAllSelected())
         {
            addLesson = true;
         }
         else
         {
            if (cpodLesson.getLessonLevel().startsWith("Newbie"))
            {
               if (wlcpControl.getNewbieSelected())
                  addLesson = true;
            }
            else if (cpodLesson.getLessonLevel().startsWith("Elementary"))
            {
               if (wlcpControl.getElementarySelected())
                  addLesson = true;
            }
            else if (cpodLesson.getLessonLevel().startsWith("Intermediate"))
            {
               if (wlcpControl.getIntermediateSelected())
                  addLesson = true;
            }
            else if (cpodLesson.getLessonLevel().startsWith("Upper"))
            {
               if (wlcpControl.getUpperIntermediateSelected())
                  addLesson = true;
            }
            else if (cpodLesson.getLessonLevel().startsWith("Advanced"))
            {
               if (wlcpControl.getAdvancedSelected())
                  addLesson = true;
            }
            else if (cpodLesson.getLessonLevel().startsWith("Media"))
            {
               if (wlcpControl.getMediaSelected())
                  addLesson = true;
            }
            else
            {
               if (wlcpControl.getOtherSelected())
               {
                  addLesson = true;
               }
            }
         }
         if (addLesson)
         {
             
            Boolean newItem = true;
            if (fileListModel.size() > 0)
            {
               //String firstItemTitle = (String) fileListModel.get(0);
               if (rssMap.containsKey(cpodLesson.getRSSTitle()))
               {
                   //already got it so stop the rss feed update
                   stopRSSFeed();
                   newItem = false;
               }

            }
            if (newItem)
            {
                
               String rssTitle = cpodLesson.getRSSTitle();
               rssMap.put(rssTitle, rssTitle);
               if (updating)
               {
                  //cpodLessonList.insertElementAt(cpodLesson, position);
                  //fileListModel.add(position, rssTitle);  
                  addLesson(cpodLesson, rssTitle, position); 
               }
               else
               {                  
                  cpodLessonList.add(cpodLesson);
                  fileListModel.addElement(rssTitle);
               }
               //if (Config.DEBUG)
               //   Debug.debug("RssFeed.addLessonTitle - received lesson: ", cpodLesson.getRSSTitle());

            }
         }
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("RssFeed.addLessonTitle - RSSLesson object is null.");
      }
      
   }
   
   public synchronized void addLesson(final RSSLesson cpodLesson, final String rssTitle, final int position)
   {
       //have to do this on the EDT to avoid listUI array out of bounds exception when updating from a thread
       final Runnable listUpdater = new Runnable()
       {
                @Override
                public void run()
                {
                  cpodLessonList.insertElementAt(cpodLesson, position);
                  fileListModel.add(position, rssTitle);     
                }
        };
        try
        {
            SwingUtilities.invokeAndWait(listUpdater);
        } catch (InterruptedException ex) {
            Logger.getLogger(LessonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(LessonManagerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

   }
   
   private void createPopupMenu()
   {
      JMenuItem menuItem;
      popup = new JPopupMenu();

      menuItem = new JMenuItem("View lesson summary");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            //show a dialog box with the lesson summary text
            if (fileListModel.size() > 0)
            {
                  //index = feedList.getSelectedIndex();
                  if (index == -1)
                  {
                     index = 0;
                  }
                  RSSLesson cpodLesson = (RSSLesson) cpodLessonList.get(index);
                  summaryDialog.showDialog(cpodLesson);
            }

            //dlStatus.printStatusLine("RSSFeed.popup() - index clicked: " + index);
         }
      });
      popup.add(menuItem);
      menuItem = new JMenuItem("Remove lesson from list");
      menuItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt)
         {
            cpodLessonList.remove(index);
            fileListModel.remove(index);
            //lessonControllerPanel.printStatusMessage("Removed RSS item from list...");
         }
      });
      popup.add(menuItem);

   }

   public void resetUI()
   {
      createPopupMenu();
      summaryDialog = new RSSDialog(seePodConfig.getAppFrame(), this);
      fc = new JFileChooser(".");
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);   
      FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML files","html");
      fc.setFileFilter(filter);
   }
   
   private void saveRSSFeed()
   {
      PrintWriter pw = null;
      // popup a file dialog and save feed list to a file, using html
      File rssFile = rssFileChooser();
      if (rssFile != null)
      {
         if (!rssFile.exists())
         {
            try 
            {
               rssFile.createNewFile();
            } catch (IOException ex) {
               Debug.debug("RssFeed.saveRSSFeed() - IO exception saving RSS Feed.");
               return;
            }
            try 
            {
               pw = new PrintWriter(rssFile, "UTF-8");
               pw.println("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> ");
            } catch (FileNotFoundException ex) {
               Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
               Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < cpodLessonList.size(); i++)
            {
               RSSLesson rssl = (RSSLesson) cpodLessonList.get(i);
               String tmp = rssl.getRSSTitle();
               pw.print("<b>");
               pw.println(tmp);
               pw.print("</b><br />");
               tmp = rssl.getLessonURL();
               pw.println(tmp);
               tmp = rssl.getLessonEncoded();
               pw.println(tmp);
               tmp = rssl.getLessonSubtitle();
               pw.println(tmp);
               tmp = rssl.getLessonSummary();
               pw.println(tmp);               
            }
            pw.println("</html>");
            pw.close();
            dlStatus.printStatusLine("Saved rss feed file: " + rssFile.getName());
         }
      }
     
   }
 
   private void stopRSSFeed()
   {
      if (rssFeed != null)
      {
         rssFeed.stopFeed();
      }
      return;
   }
   
   private void startRSSFeed()
   {

      /*
       * NOTE: using Runnable causes the GUI to freeze while the XML parser is running!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
       * MUST USE Thread instead of Runnable!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
       */
      String rss = seePodConfig.getRSSFeedURL();
      if ((rss != null) && (rss.startsWith("http://chinesepod.com/")))
      {
         rssFeed = new RSSPod(this, dlStatus, rss, databaseDirectory);
         rssThread = new Thread(rssFeed);
         rssThread.start();
      }
      else
      {
         dlStatus.printStatusLine("Could not start RSS feed, invalid URL: " + rss);
      }

   }
   
   private File rssFileChooser()
   {
        File chosenFile = null;
        
        if (fc == null) 
        {
            fc = new JFileChooser(".");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);   
            FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML files","html");
            fc.setFileFilter(filter);

        }

        int returnVal = fc.showSaveDialog(this);
       
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            chosenFile = fc.getSelectedFile();
            String fileName = chosenFile.getAbsolutePath();
            if (!fileName.endsWith(".html"))
            {
               fileName = fileName.concat(".html");
               chosenFile = new File(fileName);
               //Debug.debug("LessonManagerList.lessonFileChooser() - Selected file: ", fileName);
            }
            
        } 
        else 
        {
           if (Config.DEBUG)
              Debug.debug("RssFeed.rssFileChooser(): cancelled file selection.");
        }
        return chosenFile;
   } 
   
   public void checkRSSAutoStart()
   {
      if (wlcpControl.getAutoStartRssFeed()) //called after startup by parent
      {
         startRSSFeed();
      }
   }
   
   public void checkRSSAutoSave()
   {
      saveRSSFeedList(); //save the list database

      if (wlcpControl.getAutoSaveRssFeed()) //called by RSSPod on download completion
      {
         saveRSSFeed(); //save the html file to a user selected file.
      }
      
   }

public class RSSListRenderer extends DefaultListCellRenderer
{
   private ImageIcon defaultImage;
   //private Vector rssItems = null;

   public RSSListRenderer()
   {
       super();

       java.net.URL imageURL = Splash.class.getResource("/seepodlessonmanager/images/seepod-rss-default-image.jpg");
       defaultImage = new ImageIcon(imageURL);
   }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {

        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Get icon to use for the list item value
        RSSLesson rss = (RSSLesson) cpodLessonList.get(index);

        String iconFilename = rss.getIconFile();
        //java.net.URL imageURL = null;
        File iconFile = new File(databaseDirectory + File.separator + iconFilename);
        if (iconFile.exists())
        {        
           ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
           ImageIcon thumbNail = scaleImage(icon.getImage(), 0.5);
           if (thumbNail == null)
           {
              thumbNail = defaultImage;
           }
           label.setIcon(thumbNail);
        }
        else
        {
           label.setIcon(defaultImage);
        }
        label.setFont(rssFont);

        return label;
    }

    public void addRSSItem(RSSLesson rssItem)
    {
        //rssItems.add(rssItem);
        //may have to insert at 0 when db implemented
    }
}

private ImageIcon scaleImage(Image src, double scale)
{
    int w = (int)(scale*src.getWidth(this));
    int h = (int)(scale*src.getHeight(this));
    if ((w == 0) || (h == 0))
    {
        return null;
    }
    int type = BufferedImage.TYPE_INT_RGB;
    BufferedImage dst = new BufferedImage(w, h, type);
    Graphics2D g2 = dst.createGraphics();
    g2.drawImage(src, 0, 0, w, h, this);
    g2.dispose();
    return new ImageIcon(dst);
}

private void loadRSSFeedList()
{
      SAXBuilder builder = new SAXBuilder();
      Document config;

      // Get the root element and iterate through the tree setting the lesson name and file path lists

      if (rssFeedList.exists())
      {
          if (rssFeedList.length() == 0)
          {
              Debug.debug("RssFeed.loadRSSFeedList - RSS file is empty.");
              return;
          }
      }
      else
      {
          Debug.debug("RssFeed.loadRSSFeedList - RSS file does not exist.");
          return;
      }
      //cpodLessonList.removeAllElements();  
      //fileListModel.removeAllElements();
      
      try 
      {

         config = builder.build(rssFeedList);

         Element root = config.getRootElement();
         
         List nodeList = root.getChildren();
         int len = nodeList.size();
 
         for (int i = 0; i < len; i++)
         {
            Element tmpNode = root.getChild("RSSItem"+i);
            if (tmpNode != null)
            {              
               Element itemTitle = tmpNode.getChild("RSSItemTitle");               
               Element itemSubtitle = tmpNode.getChild("RSSItemSubtitle");              
               Element itemURL = tmpNode.getChild("RSSItemURL"); //GUID main lesson discussion page URL              
               Element itemSummary = tmpNode.getChild("RSSItemSummary");              
               Element itemEncoded = tmpNode.getChild("RSSItemEncoded"); //encoded other lesson page links and jpg
         
               //String rssFeedTitle, String rssFeedGUID (guid URL), String rssFeedURL (encoded: lessons links), String rssFeedSubtitle, String rssFeedSummary
               RSSLesson rssl = new RSSLesson(itemTitle.getText(), itemURL.getText(), itemEncoded.getText(), itemSubtitle.getText(), itemSummary.getText());
               //cpodLessonList.add(rssl);
               //fileListModel.addElement(itemTitle.getText());
               addLessonTitle(rssl, false, 0);
            }
         }
         
         
      } catch (JDOMException ex) {
         Debug.debug("RssFeed.loadRSSFeedList - JDOM exception.");
      } catch (IOException ex) {
         Debug.debug("RssFeed.loadRSSFeedList - IO exception.");
      }
          
      return;
}

private void saveRSSFeedList()
{
      //write out the config file with JDOM
      
      Element root = new Element("RSSItemList");
      Document newLessonSet = new Document(root); 
         
      for (int i = 0; i < cpodLessonList.size(); i++)
      {
          RSSLesson rssl = (RSSLesson) cpodLessonList.get(i);
          Element tmpLeaf = new Element("RSSItem"+i);
          Element itemTitle = new Element("RSSItemTitle");
          itemTitle.setText(rssl.getRSSTitle());
          Element itemSubtitle = new Element("RSSItemSubtitle");
          itemSubtitle.setText(rssl.getLessonSubtitle());
          Element itemURL = new Element("RSSItemURL"); //GUID URL, main lesson discussion page URL
          itemURL.setText(rssl.getLessonURL());
          Element itemSummary = new Element("RSSItemSummary");
          itemSummary.setText(rssl.getLessonSummary());
          Element itemEncoded = new Element("RSSItemEncoded"); //links to other lesson pages and jpg
          itemEncoded.setText(rssl.getLessonEncoded());

          tmpLeaf.addContent(itemTitle);
          tmpLeaf.addContent(itemSubtitle);
          tmpLeaf.addContent(itemURL);
          tmpLeaf.addContent(itemSummary);
          tmpLeaf.addContent(itemEncoded);
          root.addContent(tmpLeaf);
          //lessonControllerPanel.printStatusMessage("Added: " + lesson);
          
      }
      
      try 
      {
          PrintWriter wout = new PrintWriter(rssFeedList, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!
          serializer.output(newLessonSet, wout);
          wout.close();
          //lessonControllerPanel.printStatusMessage("Saved file: " + lessonSetFile.getName());
      }
      catch (IOException e) {
          if (Config.DEBUG)
             Debug.debug("RSSFeed.saveRSSFeedList() - Could not write RSS list file.");
          return;
      }
      if (Config.DEBUG)
      {
         Debug.debug("RSSFeed.saveRSSFeedList() - saved RSS list file."); 
      }      
}

}