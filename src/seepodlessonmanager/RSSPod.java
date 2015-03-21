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
 * Class  : RSSPod
 * 
 * Description: ChinesePod.com RSS feed downloader class.
 * 
 * 
 */



package seepodlessonmanager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class RSSPod implements Runnable
{
  
  private String rssFeedURL; 
  private RssFeed rss;
  private DownloadStatus dlStatus;
  private boolean stopFeedUpdate;
  private File logFile;
  private PrintWriter feedLogger;
  private String databaseDirectory;
  
  public RSSPod(RssFeed parent, DownloadStatus dls, String cpodRSSFeedURL, String dbDirectory)
  {
     if (dls != null)
     {
        dlStatus = dls;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("RSSPod() - Download status object is null."); 
        return;
     }
     if (parent != null)
     {
        rss = parent;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("RSSPod() - Parent object is null."); 
        return;
     }
     if (cpodRSSFeedURL != null)
     {
        rssFeedURL = cpodRSSFeedURL;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("RSSPod() - Parent object is null."); 
        return;
     }
     stopFeedUpdate = false;
     
     databaseDirectory = dbDirectory;

     logFile = new File(databaseDirectory + File.separator + Config.SP_RSS_FEED_FILE);
     if (logFile.exists())
     {
         logFile.delete();
     }

     try 
     {
         logFile.createNewFile();
     } catch (IOException ex) {
         Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
     }   
   
     try 
     {
         feedLogger = new PrintWriter(logFile, "UTF-8");
         feedLogger.println("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> ");
     } catch (FileNotFoundException ex) {
         Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
     } catch (UnsupportedEncodingException ex) {
         Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
     }
        
 
  }
  
  public static InputStream getDocumentAsInputStream(URL url) throws IOException 
  {
  
    InputStream in = url.openStream();
    return in;
  
  }

  public static InputStream getDocumentAsInputStream(String url) throws MalformedURLException, IOException 
  {
  
    URL u = new URL(url);
    return getDocumentAsInputStream(u);
  
  }

  public static String getDocumentAsString(URL url) throws IOException 
  {
  
    StringBuffer result = new StringBuffer();
    InputStream in = url.openStream();
    int c;
    while ((c = in.read()) != -1) result.append((char) c);
    return result.toString();
  
  }

  public static String getDocumentAsString(String url) throws MalformedURLException, IOException 
  {
  
    URL u = new URL(url);
    return getDocumentAsString(u);
  
  }
   
  public void starttest()
  {
     //generate a bunch of dummy RSS items and send to RSSFeed
     for (int i = 0; i < 10; i++)
     {
         RSSLesson rssl = new RSSLesson("Intermediate - Chinese For Dummies: " + i, "http://chinesepod.com/lessons/lesson-name", "http://chinesepod.com/images/chinesepod_D8888.jpg",  "Lesson subtitle.", "Lesson summary.");
         rss.addLessonTitle(rssl, true, i);
     }
      
  }
  
  public void start() throws IOException
  {
     XMLReader xmlParser;
     XMLTextExtractor xmlHandler = new XMLTextExtractor();
     
     if (Config.DEBUG)
        Debug.debug("RSSPod.start() - starting cpod rss feed: " + rssFeedURL); 
     
     try { // xerces
        xmlParser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        if (Config.DEBUG)
           Debug.debug("RSSPod.start() - got xerces parser."); 
     }
     catch (SAXException e1) {
     try { // Ælfred
      xmlParser = XMLReaderFactory.createXMLReader("gnu.xml.aelfred2.XmlReader");
     }
     catch (SAXException e2) { 
      try { // Piccolo
        xmlParser = XMLReaderFactory.createXMLReader("com.bluecast.xml.Piccolo");
      }
      catch (SAXException e3) {
        try { // default
          xmlParser = XMLReaderFactory.createXMLReader();
        }
            catch (SAXException e6) { throw new NoClassDefFoundError("No SAX parser is available");
          }
        }
      }
    } 
    xmlParser.setContentHandler(xmlHandler);
    try 
    {
        if (Config.DEBUG)
           Debug.debug("RSSPod.start() - starting xml parser for feed: " + rssFeedURL); 
        dlStatus.printStatusLine("Starting rss feed: " + rssFeedURL);
        xmlParser.parse(rssFeedURL);
    } catch (SAXException ex) {
        dlStatus.printStatusLine("Stopping ChinesePod.com rss feed.");  
        if (Config.DEBUG)
           Debug.debug("RSSPod.start() - stopped parsing xml document from: " + rssFeedURL); 
    }
    dlStatus.printStatusLine("Finished rss feed: " + rssFeedURL);
    feedLogger.println("</html>");
    feedLogger.close();
    rss.checkRSSAutoSave(); //check for auto save
  }
  
@Override
public void run()
{
    //starttest();
      try
      {
         start();
      } catch (IOException ex) {
         Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
      }
}
 
public void stopFeed()
{
   if (Config.DEBUG)
      Debug.debug("RSSPod.stopFeed() - stopping rss feed."); 
   
   stopFeedUpdate = true;
}

//here is the content handler for the parser callbacks
public class XMLTextExtractor implements ContentHandler 
{
  private int elementCounter;
  private int itemCounter;
  private boolean receivedLessonTitle;
  private boolean receivedLessonLink;
  private String lessonName;
  private String lessonGUID;
  private String lessonSummary;
  private String lessonLink;
  private String lessonSubtitle;
  private boolean receivedLessonguid;
  private boolean receivedLessonSummary;
  private boolean receivedLessonItem;
  private boolean receivedLessonSubtitle;
  private boolean receivedLessonURL;
  private boolean receivedLessonEnclosure;
  private boolean skipItem;
  
  public XMLTextExtractor() 
  {
     elementCounter = 0;
     itemCounter = 0;
     receivedLessonTitle = false;
     receivedLessonLink = false;
     receivedLessonguid = false;
     receivedLessonSummary = false;
     receivedLessonItem = false;
     receivedLessonSubtitle = false;
     receivedLessonURL = false;
     receivedLessonEnclosure = false;
     skipItem = false;
  
  }
    
  @Override
  public void characters(char[] text, int start, int length) throws SAXException 
  {
     String elementText = new String(text, start, length);
     
     //if (Config.DEBUG)
     //   Debug.debug("RSSPod.characters() - received text: " + elementText);

     //throw SAXException if stopFeedUpdate is true
     if (stopFeedUpdate)
     {
         throw new SAXException("XMLTextExtractor.characters() - finished parsing.");
     }
     if (skipItem || !receivedLessonItem)
     {
        return;
     }
     
     if (receivedLessonEnclosure)
     {
        Debug.debug("RSSPod.characters() - enclosure: ", text);
     }

     if (receivedLessonTitle)
     {
        if (elementText.startsWith("Lesson Review") || 
                elementText.startsWith("PDF") ||
                elementText.startsWith("Dialogue") ||
                elementText.startsWith("Vocab") || 
                elementText.startsWith("The Menu Stealer"))
        {
           skipItem = true;
           if (Config.DEBUG)
              Debug.debug("RSSPod.characters() - skipping lesson item: " + elementText);  
        }
        else
        {
              lessonName = elementText; 
              elementCounter++;
              dlStatus.printStatusLine("Received Lesson: " + elementText);
              feedLogger.print("<b>");
              feedLogger.println(elementText);
              feedLogger.print("</b><br />");
        }
     }
     else if (receivedLessonURL)
     {
          if (Config.DEBUG)
             Debug.debug("RSSPod.characters() - lesson URL: " + elementText);          
     }
     else if (receivedLessonguid) 
     {
        //if (Config.DEBUG)
        //   Debug.debug("RSSPod.characters() - received lesson guid: " + elementText);
        lessonGUID = elementText;
        feedLogger.println(elementText);
        elementCounter++;        
     }
     else if (receivedLessonSummary) 
     {
        //if (Config.DEBUG)
        //   Debug.debug("RSSPod.characters() - received lesson summary: " + elementText);
        lessonSummary = elementText;
        feedLogger.println(elementText);
        elementCounter++;       
     }
     else if (receivedLessonLink) 
     {
        //if (Config.DEBUG)
        //   Debug.debug("RSSPod.characters() - received lesson encoded: " + elementText);
        lessonLink = elementText;
        feedLogger.println(elementText);
        elementCounter++;      
     }
     else if (receivedLessonSubtitle) 
     {       
        //if (Config.DEBUG)
        //   Debug.debug("RSSPod.characters() - received lesson subtitle: " + elementText);
        lessonSubtitle = elementText;
        feedLogger.println(elementText);
        elementCounter++;     
     }     
     
     try 
     {
         Thread.sleep(1);
     } catch (InterruptedException ex) {
         Logger.getLogger(RSSPod.class.getName()).log(Level.SEVERE, null, ex);
     }
     
     
     return;
  }  
    
  @Override
  public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts) 
  {
     if (Config.DEBUG)
     {
        Debug.debug("RSSPod.startElement() - start element namespace: " + namespaceURI);
        Debug.debug("RSSPod.startElement() - start element localname: " + localName);
        Debug.debug("RSSPod.startElement() - start element qualname : " + localName); 
     }
             
     if (localName.equalsIgnoreCase("item"))
     {
        receivedLessonItem = true;
     }
     else if (localName.equalsIgnoreCase("title"))
     {
        receivedLessonTitle = true;
     }
     else if (localName.equalsIgnoreCase("link"))
     {
        receivedLessonURL = true;
     }
     else if (localName.equalsIgnoreCase("encoded")) //old RFD content descriptor???
     {
        receivedLessonLink = true;
     }
     else if (localName.equalsIgnoreCase("enclosure")) //is this the same as encoded content descriptor
     {
        receivedLessonEnclosure = true;
     }
     else if (localName.equalsIgnoreCase("guid"))
     {
        receivedLessonguid = true;
     }
     else if (localName.equalsIgnoreCase("summary"))
     {
        receivedLessonSummary = true;
     }
     else if (localName.equalsIgnoreCase("subtitle"))
     {
        receivedLessonSubtitle = true;
     }
     return;
  }
  
  @Override
  public void endElement(String namespaceURI, String localName, String qualifiedName) 
  {
     if (Config.DEBUG)
        Debug.debug("RSSPod.endElement() - received END element : " + localName);
     
     if (localName.equalsIgnoreCase("item"))
     {
        receivedLessonItem = false;
        if (!skipItem)
           processLessonItem();
        skipItem = false;
     }
     else if (localName.equalsIgnoreCase("title"))
     {
        receivedLessonTitle = false;
     }
     else if (localName.equalsIgnoreCase("link"))
     {
        receivedLessonURL = false;
     }
     else if (localName.equalsIgnoreCase("encoded"))
     {
        receivedLessonLink = false;
     }
     else if (localName.equalsIgnoreCase("enclosure"))
     {
        receivedLessonEnclosure = false;
     }
     else if (localName.equalsIgnoreCase("guid"))
     {
        receivedLessonguid = false;
     }
     else if (localName.equalsIgnoreCase("summary"))
     {
        receivedLessonSummary = false;
     }
     else if (localName.equalsIgnoreCase("subtitle"))
     {
        receivedLessonSubtitle = false;
     }
  }
  
  private void processLessonItem()
  {
     RSSLesson cpodLesson = new RSSLesson(lessonName, lessonGUID, lessonLink, lessonSubtitle, lessonSummary);
     String iconFilename = cpodLesson.getIconFile();
     File imageFile = new File(databaseDirectory + File.separator + iconFilename);
     if (!imageFile.exists())
     {
        downloadImage(cpodLesson.getIconURL(), iconFilename);
     }

     rss.addLessonTitle(cpodLesson, true, itemCounter);
     itemCounter++;
     elementCounter = 0;
     feedLogger.println("====================End of RSS Lesson Item===================<br /><br /><br /><br />");
  }
  
   public void downloadImage(String aURL, String aFile) 
   {
     DataInputStream di = null;
     FileOutputStream fo = null;
     byte [] b = new byte[1024];  
       
     try 
     {
       URL url = new URL(aURL);
       URLConnection urlConnection = url.openConnection();
       urlConnection.connect();
       di = new DataInputStream(urlConnection.getInputStream());

       fo = new FileOutputStream(databaseDirectory + File.separator + aFile);

       int bytesIn = di.read(b, 0, 1024);
       while(bytesIn != -1) 
       {
         fo.write(b, 0, bytesIn);
         bytesIn = di.read(b, 0, 1024);
       }

       if (Config.DEBUG)
          Debug.debug("XMLTextExtractor.downloadImage() - downloaded image: ", aURL);

       di.close();  
       fo.close();                
     }
     catch (Exception ex) { 
        Debug.debug("XMLTextExtractor.downloadImage() - could not download image: ", aURL);
        Debug.debug("XMLTextExtractor.downloadImage() - exception: ", ex);
     }

   }  
   
  // do-nothing methods
  public void setDocumentLocator(Locator locator) {}
  public void startDocument() {}
  public void endDocument() {}
  public void startPrefixMapping(String prefix, String uri) {}
  public void endPrefixMapping(String prefix) {}
  public void ignorableWhitespace(char[] text, int start, int length) throws SAXException {}
  public void processingInstruction(String target, String data){}
  public void skippedEntity(String name) {}

} // end XMLTextExtractor


  
}
