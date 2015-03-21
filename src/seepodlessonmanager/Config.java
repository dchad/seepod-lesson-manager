
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
 * Class  : Config
 * 
 * Description: Read and write configuration file using XML.
 * 
 * 
 * 
 * 
 */


package seepodlessonmanager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Config implements SPConstants
{
   //file and directory paths
   
   private String wlcpDirectory;
   private String wlcpScript;
   private String lessonDirectory;
   private String rssFeedURL;
   private String pythonExecutable;
   private java.net.URL startupDirectory;
   private File configFile;
   
   //Tag Editor & WLCP options
   
   private Vector trackNumber;
   private Vector trackTitle;
   private Vector albumName;
   private Vector artistName;
   private Vector genreName;
   private Vector year;
   private Vector comment;
   
   private Boolean ignoreSentenceFiles;
   private Boolean deleteSentenceFiles;
   private Boolean archiveSentenceFiles;
   private Boolean includeSubdirectories;
   
   //WLCP Download options
   
   private Boolean rssAutoTag;
   private Boolean rssAutoFeed;
   private Boolean rssAutoSave;
   
   private String operatingSystemArch;
   private String operatingSystemName;
   private String operatingSystemVersion;
   private Map<String, String> env;
   private String homeDirectory;
   private String cwdDirectory;
   private String databaseDirectory;
   
   private String uiLookAndFeel;
   
   //Lesson database management options
   
   private Boolean dbAutoUpdate;
   private Boolean dbAutoSave;
   
   private static JFrame appFrame;
   private PDFViewerDialog pdfViewer;
   private DirectoryTree treePanel;

   private int screenWidth;
   private int screenHeight;
   private int x;
   private int y;
   private Dimension screen;
   
   public static Boolean DEBUG;

   public Config()
   {
      DEBUG = true;
      trackNumber = new Vector();
      trackTitle = new Vector();
      albumName = new Vector();
      artistName = new Vector();
      genreName = new Vector();
      year = new Vector();
      comment = new Vector();

      screen = Toolkit.getDefaultToolkit().getScreenSize();
      if (screen.width >= 1200)
      {
          screenWidth = 1200;
      }
      else
      {
          screenWidth = screen.width;
      }
      if (screen.height >= 800)
      {
          screenHeight = 800;
      }
      else
      {
          screenHeight = screen.height;
      }
      x = (screen.width - screenWidth) / 2;
      y = (screen.height - screenHeight) / 2;

      getEnvironment();

      configFile = new File(homeDirectory + File.separator + SP_CONFIG_FILE);
      if (configFile.exists())
      {
         loadConfigFile();
      }
      else
      {
         if (DEBUG)
            Debug.debug("Config() - No configuration file, setting default configuration.");

         setDefaultConfig();   
         saveConfigFile();    
      }
      checkWLCPScript();
   }

   public int getScreenWidth()
   {
       return screenWidth;
   }

   public int getScreenHeight()
   {
       return screenHeight;
   }

   public int getScreenX()
   {
       return x;
   }

   public int getScreenY()
   {
       return y;
   }
   
   private void getEnvironment()
   {
      operatingSystemArch = System.getProperty("os.arch");
      operatingSystemName = System.getProperty("os.name");
      operatingSystemVersion = System.getProperty("os.version");
      startupDirectory = Config.class.getProtectionDomain().getCodeSource().getLocation();
      cwdDirectory = System.getProperty("user.dir");

      File installDirectory = getInstallDirectory();
      if (installDirectory != null)
      {
         homeDirectory = installDirectory.getAbsolutePath();
         databaseDirectory = homeDirectory + File.separator + "database";
      }
      else
      {
         homeDirectory = cwdDirectory; //no choice, have to use the current working directory, this may or may not be the WeePod install directory
         databaseDirectory = cwdDirectory + File.separator + "database";
      }
      
      env = System.getenv();
      if (DEBUG)
      {
         Debug.debug("Config() - startup directory: ", startupDirectory);
         Debug.debug("Config() - OS Architecture: ", operatingSystemArch);
         Debug.debug("Config() - OS Name: ", operatingSystemName);
         Debug.debug("Config() - OS Version: ", operatingSystemVersion);
         Debug.debug("Config() - Home Directory: ", homeDirectory);
         Debug.debug("Config() - Current Working Directory: ", cwdDirectory);
         for (Map.Entry<String,String> e : env.entrySet())
            Debug.debug("Config() - Environment Variables: " + e.getKey() + " : " + e.getValue());
      }

      pythonExecutable = "Unknown";

      if (operatingSystemName.contains("Windows"))
      {
         File tmp = new File(WIN_PYTHON25_PATH);
         if (tmp.exists())
         {
            pythonExecutable = WIN_PYTHON25_PATH;
         }
         else
         {
            tmp = new File(WIN_PYTHON26_PATH);
            if (tmp.exists())
            {
               pythonExecutable = WIN_PYTHON26_PATH;
            }
            else
            {
               //do python3
               tmp = new File(WIN_PYTHON30_PATH);
               if (tmp.exists())
               {
                  pythonExecutable = WIN_PYTHON30_PATH;
               }
            }
         }
      }
      else if (operatingSystemName.contains("Linux"))
      {
         File tmp = new File(LINUX_PYTHON_PATH);
         if (tmp.exists())
         {
            pythonExecutable = LINUX_PYTHON_PATH;
         }
      }
      else
      {
         if (DEBUG)
            Debug.debug("Config() - Could not find Python.");
      }
      if (DEBUG)
         Debug.debug("Config() - Python path: ", pythonExecutable);
   }
   
   public synchronized String getHomeDirectory()
   {
       return homeDirectory;
   }

   public synchronized String getDatabaseDirectory()
   {
      return databaseDirectory;
   }
   
   public synchronized void setAppFrame(JFrame topFrame)
   {
      appFrame = topFrame;
   }
   
   public synchronized JFrame getAppFrame()
   {
      return appFrame;
   }
      
   public synchronized void setPDFFrame(PDFViewerDialog pdfvd)
   {
      pdfViewer = pdfvd;
   }
   
   public synchronized JFrame getPDFFrame()
   {
      return pdfViewer;
   }

   public synchronized void setTreePanel(DirectoryTree tp)
   {
      treePanel = tp;
   }

   public synchronized DirectoryTree getTreePanel()
   {
      return treePanel;
   }

   public synchronized String getWLCPDirectory()
   {
      return(wlcpDirectory);
   }
   
   public synchronized void setWLCPDirectory(String wlcpdir)
   {
      if (wlcpdir != null)
      {
         wlcpDirectory = wlcpdir;
      }
   }
   
   public synchronized String getWLCPScript()
   {
      return(wlcpScript);
   }
   
   public synchronized void setWLCPScript(String wlcps)
   {
      if (wlcps != null)
      {
         wlcpScript = wlcps;
      }
   }
   
   public synchronized String getLessonDirectory()
   {
      return(lessonDirectory);
   }
   
   public synchronized void setLessonDirectory(String ldir)
   {
      if (ldir != null)
      {
         lessonDirectory = ldir;
      }
   }

   public synchronized String getRSSFeedURL()
   {
      return(rssFeedURL);
   }
   
   public synchronized void setRSSFeedURL(String fURL)
   {
      if (fURL != null)
      {
         rssFeedURL = fURL;
      }
   }

   public synchronized void setPythonPath(String pPath)
   {
      if (pPath != null)
      {
         pythonExecutable = pPath;
      }
   }
   
   public synchronized String getPythonPath()
   {
      return pythonExecutable;
   }
   
   public synchronized String getPythonExecutable()
   {
      return pythonExecutable;
   }

   public synchronized Boolean getAutoTagWLCPFiles()
   {
      return(rssAutoTag);
   }
   
   public synchronized void setAutoTagWLCPFiles(Boolean autoTagWLCP)
   {
      if (autoTagWLCP != null)
      {
         rssAutoTag = autoTagWLCP;
      }
   }
   
   public synchronized Boolean getAutoRSSFeed()
   {
      return(rssAutoFeed);
   }
   
   public synchronized void setAutoRSSFeed(Boolean autoFeed)
   {
      if (autoFeed != null)
      {
         rssAutoFeed = autoFeed;
      }
   }
 
   public synchronized Boolean getAutoSaveRSSFeed()
   {
      return(rssAutoSave);
   }
   
   public synchronized void setAutoSaveRSSFeed(Boolean autoSave)
   {
      if (autoSave != null)
      {
         rssAutoSave = autoSave;
      }
   }
      
   public synchronized Boolean getIncludeSubdirectories()
   {
      return(includeSubdirectories);
   }
   
   public synchronized void setIncludeSubdirectories(Boolean includeSubs)
   {
      if (includeSubs != null)
      {
         includeSubdirectories = includeSubs;
      }
   }
   
   public synchronized void setIgnoreSentenceFiles(Boolean bval)
   {
      ignoreSentenceFiles = bval;
   }
   
   public synchronized void setDeleteSentenceFiles(Boolean bval)
   {
      deleteSentenceFiles = bval;
   }
   
   public synchronized void setArchiveSentenceFiles(Boolean bval)
   {
      archiveSentenceFiles = bval;
   }
   
   public synchronized Boolean getIgnoreSentenceFiles()
   {
      return ignoreSentenceFiles;
   }
   
   public synchronized Boolean getDeleteSentenceFiles()
   {
      return deleteSentenceFiles;
   }

   public synchronized Boolean getArchiveSentenceFiles()
   {
      return archiveSentenceFiles;
   }
   
   public synchronized Boolean getAutoSaveDatabase()
   {
      return dbAutoSave;
   }   
   
   public synchronized Boolean getAutoUpdateDatabase()
   {
      return dbAutoUpdate;
   }   
   
   public synchronized void setAutoUpdateDatabase(Boolean val)
   {
      dbAutoUpdate = val;
   }

   public synchronized void setAutoSaveDatabase(Boolean val)
   {
      dbAutoSave = val;
   }
   
   public synchronized Vector getTagContent(int tagID)
   {
      Vector tagValues = null;
      switch (tagID)
      {
         case ID3_ALBUM_NAME_KEY:  tagValues = albumName; break;
         case ID3_ARTIST_NAME_KEY: tagValues = artistName; break;
         case ID3_GENRE_NAME_KEY:  tagValues = genreName; break; 
         case ID3_YEAR_KEY:        tagValues = year; break;
         case ID3_COMMENT_KEY:     tagValues = comment; break;
         case ID3_TRACK_NAME_KEY:  tagValues = trackTitle; break;
         case ID3_TRACK_NUMBER_KEY: tagValues = trackNumber; break;
         default: 
            Debug.debug("Config.getTagContent() - Invalid content id: ", tagID);
      }
         
      return tagValues;
   }
   
   public synchronized void setUILookAndFeel(String uiLAF)
   {
      if (uiLAF != null)
      {
         uiLookAndFeel = uiLAF;
      }
   }
   
   public synchronized String getUILookAndFeel()
   {
      return uiLookAndFeel;
   }
   
   public synchronized void setTagContent(Vector tagValues, int tagID)
   {
      //do another switch here
      switch (tagID)
      {
         case ID3_ALBUM_NAME_KEY:  albumName = tagValues; break;
         case ID3_ARTIST_NAME_KEY: artistName = tagValues; break;
         case ID3_GENRE_NAME_KEY:  genreName = tagValues; break; 
         case ID3_YEAR_KEY:        year = tagValues; break;
         case ID3_COMMENT_KEY:     comment = tagValues; break;
         case ID3_TRACK_NAME_KEY:  trackTitle = tagValues; break;
         case ID3_TRACK_NUMBER_KEY: trackNumber = tagValues; break;
         default: 
            Debug.debug("Config.setTagContent() - Invalid content id: ", tagID);
      }
   }

   private void checkWLCPScript()
   {
      if (wlcpScript.startsWith("Unknown"))
      {
         if (wlcpDirectory.equalsIgnoreCase("."))
         {
            wlcpDirectory = homeDirectory;
         }
         File tmp = new File(wlcpDirectory + File.separator + WLCP_PYTHON_SCRIPT);
         if (tmp.exists())
         {
            wlcpScript = WLCP_PYTHON_SCRIPT;
         }
         else
         {
            tmp = new File(wlcpDirectory + File.separator + WLCP_ALT_PYTHON_SCRIPT);
            if (tmp.exists())
            {
               wlcpScript = WLCP_ALT_PYTHON_SCRIPT;
            }
         }
      }
   }

   private void setDefaultConfig()
   {
      wlcpDirectory = ".";
      wlcpScript = "Unknown";

      lessonDirectory = ".";
      rssFeedURL = "Unknown";
   
      String[] tmp3 = {"Newbie","Elementary","Intermediate","Upper-Intermediate","Advanced","Media","Fix","Show"};
      albumName = new Vector();
      albumName.copyInto(tmp3);
      String[] tmp4 = {"ChinesePod.com","ChinesePod","WLCP","Unknown"};
      artistName = new Vector();
      artistName.copyInto(tmp4);
   
      ignoreSentenceFiles = true;
      deleteSentenceFiles = false;
      archiveSentenceFiles = false;
      includeSubdirectories = true;
   
      rssAutoTag = false;
      rssAutoFeed = false;
      rssAutoSave = false;

      dbAutoSave = false;
      dbAutoUpdate = false;
      
      uiLookAndFeel = "Default";

      DEBUG = false;
      
      return;
   }
   
   public void saveConfigFile()
   {
      //write out the config file with JDOM
      configFile = new File(homeDirectory + File.separator + Config.SP_CONFIG_FILE);

      Element root = new Element("SeePodConfiguraton");
      Document config = new Document(root); 
     
      Element wlcpDirectoryNode = new Element("WLCPDirectory");
      wlcpDirectoryNode.setText(wlcpDirectory);
      root.addContent(wlcpDirectoryNode);
      
      Element wlcpScriptNode = new Element("WLCPScript");
      wlcpScriptNode.setText(wlcpScript);
      root.addContent(wlcpScriptNode);
      
      Element lessonDirectoryNode = new Element("LessonDirectory");
      lessonDirectoryNode.setText(lessonDirectory);
      root.addContent(lessonDirectoryNode);
         
      Element rssFeedNode = new Element("RSSFeedURL");
      rssFeedNode.setText(rssFeedURL);
      root.addContent(rssFeedNode);

      Element pythonPathNode = new Element("PythonPath");
      pythonPathNode.setText(pythonExecutable);
      root.addContent(pythonPathNode);

      if (albumName != null)
      {
         if (albumName.size() > 0)
         {
            Element albumNameNode = new Element("LessonLevels");
            root.addContent(albumNameNode);
            for (int i = 0; i < albumName.size(); i++) //put a limit of 25 for combobox drop lists
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("Level"+i);
                tmpLeaf.setText((String) albumName.get(i));
                albumNameNode.addContent(tmpLeaf);
            }
         }
      }

      if (artistName != null)
      {
         if (artistName.size() > 0)
         {
            Element artistNameNode = new Element("Artist");
            root.addContent(artistNameNode);
            for (int i = 0; i < artistName.size(); i++)
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("Artist"+i);
                tmpLeaf.setText((String) artistName.get(i));
                artistNameNode.addContent(tmpLeaf);
            }
         }
      }

      if (trackNumber != null)
      {
         if (trackNumber.size() > 0)
         {
            Element trackNumberNode = new Element("TrackNumber");
            root.addContent(trackNumberNode);
            for (int i = 0; i < trackNumber.size(); i++)
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("TrackNo"+i);
                tmpLeaf.setText((String) trackNumber.get(i));
                trackNumberNode.addContent(tmpLeaf);
            }
         }
      }

      if (trackTitle != null)
      {
         if (trackTitle.size() > 0)
         {
            Element trackTitleNode = new Element("TrackTitle");
            root.addContent(trackTitleNode);
            for (int i = 0; i < trackTitle.size(); i++)
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("TrackName"+i);
                tmpLeaf.setText((String) trackTitle.get(i));
                trackTitleNode.addContent(tmpLeaf);
            }
         }
      }

      if (year != null)
      {
         if (year.size() > 0)
         {
            Element yearNode = new Element("Year");
            root.addContent(yearNode);
            for (int i = 0; i < year.size(); i++)
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("Year"+i);
                tmpLeaf.setText((String) year.get(i));
                yearNode.addContent(tmpLeaf);
            }
         }
      }

      if (comment != null)
      {
         if (comment.size() > 0)
         {
            Element commentNode = new Element("Comment");
            root.addContent(commentNode);
            for (int i = 0; i < comment.size(); i++)
            {
                if (i > 24)
                   break;
                Element tmpLeaf = new Element("Comment"+i);
                tmpLeaf.setText((String) comment.get(i));
                commentNode.addContent(tmpLeaf);
            }
         }
      }

      Element id3Options = new Element("ID3Options");
      root.addContent(id3Options);
      
      Element ignoreSentenceFilesNode = new Element("IgnoreSentenceFiles");
      ignoreSentenceFilesNode.setText(ignoreSentenceFiles.toString());
      id3Options.addContent(ignoreSentenceFilesNode);
      
      Element deleteSentenceFilesNode = new Element("DeleteSentenceFiles");
      deleteSentenceFilesNode.setText(deleteSentenceFiles.toString());
      id3Options.addContent(deleteSentenceFilesNode);
 
      Element archiveSentenceFilesNode = new Element("ArchiveSentenceFiles");
      archiveSentenceFilesNode.setText(archiveSentenceFiles.toString());
      id3Options.addContent(archiveSentenceFilesNode);
 
      Element includeSubdirectoriesNode = new Element("IncludeSubdirectories");
      includeSubdirectoriesNode.setText(includeSubdirectories.toString());
      id3Options.addContent(includeSubdirectoriesNode);
     
      Element rssOptions = new Element("RSSOptions");
      root.addContent(rssOptions);
      
      Element rssAutoTagNode = new Element("RSSAutoTag");
      rssAutoTagNode.setText(rssAutoTag.toString());
      rssOptions.addContent(rssAutoTagNode);
      
      Element rssAutoFeedNode = new Element("RSSAutoFeed");
      rssAutoFeedNode.setText(rssAutoFeed.toString());
      rssOptions.addContent(rssAutoFeedNode);
      
      Element rssAutoSaveNode = new Element("RSSAutoSave");
      rssAutoSaveNode.setText(rssAutoSave.toString());
      rssOptions.addContent(rssAutoSaveNode);
      
      Element dbAutoSaveNode = new Element("DBAutoSave");
      dbAutoSaveNode.setText(dbAutoSave.toString());
      rssOptions.addContent(dbAutoSaveNode);
      
      Element dbAutoUpdateNode = new Element("DBAutoUpdate");
      dbAutoUpdateNode.setText(dbAutoUpdate.toString());
      rssOptions.addContent(dbAutoUpdateNode);
      
      Element uiSkinNode = new Element("UILookAndFeel");
      uiSkinNode.setText(uiLookAndFeel);
      root.addContent(uiSkinNode);

      Element debugNode = new Element("DEBUG");
      debugNode.setText(DEBUG.toString());
      root.addContent(debugNode);
      
       try 
       {
          PrintWriter wout = new PrintWriter(configFile, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!
          serializer.output(config, wout);
          wout.close();
       }
       catch (IOException e) {
          if (DEBUG)
             Debug.debug("Config.saveConfigFile() - Could not write configuration file.");
       }

      
   }
   
   
   public void loadConfigFile()
   {
      //read in the config file with JDOM
      SAXBuilder builder = new SAXBuilder();
      Document config;

    // Get the root element and iterate through the tree setting the config items
      
      try 
      {
         config = builder.build(configFile);
         Element root = config.getRootElement();
         
         // lets go
         Element wlcpdir = root.getChild("WLCPDirectory");
         wlcpDirectory = wlcpdir.getTextTrim();
         Element wlcpscr = root.getChild("WLCPScript");
         wlcpScript = wlcpscr.getTextTrim();
         Element lDir = root.getChild("LessonDirectory");
         lessonDirectory = lDir.getTextTrim();
         Element rssFeed = root.getChild("RSSFeedURL");
         rssFeedURL = rssFeed.getTextTrim();
   
         Element pythonPath = root.getChild("PythonPath");
         pythonExecutable = pythonPath.getTextTrim();
         if (pythonExecutable.startsWith("Unknown"))
         {
            getEnvironment();
         }

         if (DEBUG)
            Debug.debug("Config.loadConfigFile() - Python Executable: ", pythonExecutable);
         
         Element albumNameNode = root.getChild("LessonLevels");
         if (albumNameNode != null)
         {
            List albumList = albumNameNode.getChildren();
            int len = albumList.size();
            albumName = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = albumNameNode.getChild("Level"+i);
               albumName.add(tmpLeaf.getText());
            }
         }
         
         Element artistNameNode = root.getChild("Artist");
         if (artistNameNode != null)
         {
            List artistList = artistNameNode.getChildren();
            int len = artistList.size();
            artistName = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = artistNameNode.getChild("Artist"+i);
               artistName.add(tmpLeaf.getTextTrim());
            }
         }

         Element trackNumberNode = root.getChild("TrackNumber");
         if (trackNumberNode != null)
         {
            List itemList = trackNumberNode.getChildren();
            int len = itemList.size();
            trackNumber = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = trackNumberNode.getChild("TrackNo"+i);
               trackNumber.add(tmpLeaf.getTextTrim());
            }
         }

         Element trackTitleNode = root.getChild("TrackTitle");
         if (trackTitleNode != null)
         {
            List itemList = trackTitleNode.getChildren();
            int len = itemList.size();
            trackTitle = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = trackTitleNode.getChild("TrackName"+i);
               trackTitle.add(tmpLeaf.getTextTrim());
            }
         }

         Element yearNode = root.getChild("Year");
         if (yearNode != null)
         {
            List itemList = yearNode.getChildren();
            int len = itemList.size();
            year = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = yearNode.getChild("Year"+i);
               year.add(tmpLeaf.getTextTrim());
            }
         }

         Element commentNode = root.getChild("Comment");
         if (commentNode != null)
         {
            List itemList = commentNode.getChildren();
            int len = itemList.size();
            comment = new Vector();
            for (int i = 0; i < len; i++)
            {
               Element tmpLeaf = commentNode.getChild("Comment"+i);
               comment.add(tmpLeaf.getTextTrim());
            }
         }

         Element id3Options = root.getChild("ID3Options");

         if (id3Options != null)
         {
         Element ignoreSentenceFilesNode = id3Options.getChild("IgnoreSentenceFiles");
         if (ignoreSentenceFilesNode.getTextTrim().equalsIgnoreCase("true"))
         {
            ignoreSentenceFiles = true;
         }
         else
         {
            ignoreSentenceFiles = false;
         }
         Element deleteSentenceFilesNode = id3Options.getChild("DeleteSentenceFiles");
         if (deleteSentenceFilesNode.getTextTrim().equalsIgnoreCase("true"))
         {
            deleteSentenceFiles = true;
         }
         else
         {
            deleteSentenceFiles = false;
         }
         Element archiveSentenceFilesNode = id3Options.getChild("ArchiveSentenceFiles");
         if (archiveSentenceFilesNode.getTextTrim().equalsIgnoreCase("true"))
         {
            archiveSentenceFiles = true;
         }
         else
         {
            archiveSentenceFiles = false;
         }
         
         Element includeSubdirectoriesNode = id3Options.getChild("IncludeSubdirectories");
         if (includeSubdirectoriesNode.getTextTrim().equalsIgnoreCase("true"))
         {
            includeSubdirectories = true;
         }
         else
         {
            includeSubdirectories = false;
         }
         }

         Element rssOptions = root.getChild("RSSOptions");
                  
         if (rssOptions != null)
         {
         Element rssAutoTagNode = rssOptions.getChild("RSSAutoTag");
         if (rssAutoTagNode.getTextTrim().equalsIgnoreCase("true"))
         {
            rssAutoTag = true;
         }
         else
         {
            rssAutoTag = false;
         }

         Element rssAutoFeedNode = rssOptions.getChild("RSSAutoFeed");
         if (rssAutoFeedNode.getTextTrim().equalsIgnoreCase("true"))
         {
            rssAutoFeed = true;
         }
         else
         {
            rssAutoFeed = false;
         }
         
         Element rssAutoSaveNode = rssOptions.getChild("RSSAutoSave");
         if (rssAutoSaveNode.getTextTrim().equalsIgnoreCase("true"))
         {
            rssAutoSave = true;
         }
         else
         {
            rssAutoSave = false;
         }
         }

         Element dbAutoSaveNode = rssOptions.getChild("DBAutoSave");
         if (dbAutoSaveNode == null)
         {
            dbAutoSave = false;
         }
         else
         {
            if (dbAutoSaveNode.getTextTrim().equalsIgnoreCase("true"))
            {
               dbAutoSave = true;
            }
            else
            {
               dbAutoSave = false;
            }
         }

         Element dbAutoUpdateNode = rssOptions.getChild("DBAutoUpdate");
         if (dbAutoUpdateNode == null)
         {
            dbAutoUpdate = false;
         }
         else
         {
            if (dbAutoUpdateNode.getTextTrim().equalsIgnoreCase("true"))
            {
               dbAutoUpdate = true;
            }
            else
            {
               dbAutoUpdate = false;
            }
         }
         
         Element uiSkinNode = root.getChild("UILookAndFeel");
         uiLookAndFeel = uiSkinNode.getTextTrim();

         Element debugNode = root.getChild("DEBUG");
         if (debugNode.getTextTrim().equalsIgnoreCase("true"))
         {
            DEBUG = true;
         }
         else
         {
            DEBUG = false;
         }
         
      } catch (JDOMException ex) {
         Debug.debug("Config.loadConfigFile() - JDOM exception loading configuration file.");
      } catch (IOException ex) {
         Debug.debug("Config.loadConfigFile() - IO exception loading configuration file.");
      }
          
      return;
   }

  private File getInstallDirectory()
  {
    String className = Config.class.getName();
    String resourceName = className.replace('.', '/') + ".class";
    ClassLoader classLoader = Config.class.getClassLoader();

    if(classLoader==null)
    {
      classLoader = ClassLoader.getSystemClassLoader();
    }

    URL url = classLoader.getResource(resourceName);

    String szUrl = url.toString();
    if(szUrl.startsWith("jar:file:")) //for running installed application
    {
      try
      {
        szUrl = szUrl.substring("jar:".length(), szUrl.lastIndexOf("SeePod_Lesson_Manager"));
        if (Config.DEBUG)
           Debug.debug("Install directory (jar): ", szUrl);
        URI uri = new URI(szUrl);
        return new File(uri);
      } catch(URISyntaxException e) {
        return null;
      }
    }
    else if(szUrl.startsWith("file:")) //for running in netbeans ide
    {
      try
      {
        int idx = szUrl.lastIndexOf("Manager") + 7;
        szUrl = szUrl.substring(0, idx);
        if (Config.DEBUG)
           Debug.debug("Install directory (file): ", szUrl);
        URI uri = new URI(szUrl);
        return new File(uri);
      } catch(URISyntaxException e) {
        return null;
      }
    }
    return null;
  }


}
        

        
 