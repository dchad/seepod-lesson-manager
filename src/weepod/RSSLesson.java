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
 * Class  : RSSLesson
 * 
 * Description: Resolves lesson names and numbers from ChinesePod.com lesson rss feed items.
 * 
 * 
 */


package weepod;

public class RSSLesson
{
   private String rssName;
   private String lessonName;
   private String lessonNumber;
   private String lessonGUIDURL;
   private String lessonLevel;
   private String lessonSummary;
   private String lessonSubtitle;
   private String lessonEncoded;
   private String iconFileName;
   private String iconURL;
  
   public RSSLesson (String rssFeedTitle, String rssFeedGUID, String rssFeedURL, String rssFeedSubtitle, String rssFeedSummary)
   {
      //parse the RSS feed items to extract the lesson name and lesson number
      //chinesepod lesson file names are all in this format: chinesepod_B1080.mp3
      //rssFeedGUID (rss element -> guid) is the URL to the main lesson discussion page.
      //rssFeedURL (rss element -> encoded) is the links to the other lesson pages and the lesson image jpg.
      
      int idx = -1;
      if ((rssFeedTitle == null) || (rssFeedGUID == null) || (rssFeedURL == null)) // || (rssFeedSubtitle == null) || (rssFeedSummary == null))
      {
         if (Config.DEBUG)
            Debug.debug("RSSLesson() - Invalid parameters.");
         return;
      }

      if (rssFeedTitle.startsWith(Config.CPOD_DEAR_AMBER_STR) || rssFeedTitle.startsWith(Config.CPOD_QING_WEN_STR) || rssFeedTitle.startsWith(Config.CPOD_NEWS_STR) || rssFeedTitle.startsWith(Config.CPOD_VIDEO_STR))
      {
         lessonNumber = "0000";
         //Debug.debug("RSSLesson() - RSS feed URL: ", rssFeedURL);
      }
      else
      {
         idx = rssFeedURL.indexOf('_');                 //find the file name underscore character
         if (idx != -1)
         {
            lessonNumber = rssFeedURL.substring(idx + 2, idx + 6); //lesson number is 4 characters
         }
         else
         {
            if (Config.DEBUG)
               Debug.debug("ChinesePodLesson() - Lesson number not found in: ", rssFeedURL);
         }
      }
      
     
      idx = rssFeedTitle.indexOf(" - ");
      if (idx != -1)
      {
         lessonLevel = rssFeedTitle.substring(0,idx);
      }
      else
      {
         lessonLevel = "Unknown";
      }
      
      if (rssFeedTitle.startsWith(Config.CPOD_ADVANCED_STR) || rssFeedTitle.startsWith(Config.CPOD_MEDIA_STR)) 
      {  //advanced and media lesson URLs use chinese characters, need the Hanzi, not the hex encoded URL for WLCP downloads
         idx = rssFeedTitle.indexOf(" - ");
         if (idx != -1)
         {
            lessonName = rssFeedTitle.substring(idx + 3);
            lessonName = lessonName.concat(Config.LINE_FEED); 
         }
         else
         {
            if (Config.DEBUG)
               Debug.debug("RSSLesson() - Lesson name not found in: ", rssFeedURL);
         }
      }
      else //the lesson name in the URL is in english characters
      {
         String tmp;
         int idxStart = rssFeedGUID.indexOf("/lessons/");
         //int idxEnd = rssFeedGUID.indexOf("/discussion");
         if (idxStart != -1)
         {
            tmp = rssFeedGUID.substring(idxStart + 9); //lesson URL name format is: chinesepod.com/lessons/lesson-name/discussion
            lessonName = tmp.concat(Config.LINE_FEED); //stick a linefeed on the end so it displays nicely.
         }
         else
         {
            lessonName = "Unknown";
         }
      }
      
      lessonGUIDURL = rssFeedGUID; //main lesson discussion URL, do not put a linefeed on a URL
      rssName = rssFeedTitle;  //do not put a linefeed on the feed title
      lessonEncoded = rssFeedURL; //lesson links and jpg
      
      if (rssFeedSummary == null)
      {
          lessonSummary = "No Lesson Summary.";
      }
      else
      {
          lessonSummary = rssFeedSummary; //this is html!!!
      }
      if (rssFeedSubtitle == null)
      {
          lessonSubtitle = "No Lesson Subtitle.";
      }
      else
      {
          lessonSubtitle = rssFeedSubtitle;
      }            
      
      int imageStartIndex = rssFeedURL.indexOf("http://");
      int imageEndIndex = rssFeedURL.indexOf(".jpg");
      iconURL = rssFeedURL.substring(imageStartIndex, imageEndIndex+4);
      
      if (iconURL.contains("/images/"))
      {
         imageStartIndex = iconURL.indexOf("images/");
         iconFileName = iconURL.substring(imageStartIndex+7);
      }
      else
      {
         imageStartIndex = iconURL.indexOf("extra/");
         iconFileName = iconURL.substring(imageStartIndex+6);
      }

      //if (Config.DEBUG)
      //{
      //   Debug.debug("RSSLesson() - Lesson GUID: ", rssFeedGUID);
      //   Debug.debug("RSSLesson() - RSS title: ", rssName);
      //   Debug.debug("RSSLesson() - Lesson nummber: ", lessonNumber);
      //   Debug.debug("RSSLesson() - icon URL: ", iconURL);
      //   Debug.debug("RSSLesson() - icon file: ", iconFileName);
      //}
   }

   public String getIconFile()
   {
       return iconFileName;
   }

   public String getIconURL()
   {
       return iconURL;
   }
   
   public String getLessonName()
   {
      return lessonName;
   }
   
   public String getLessonNumber()
   {
      return lessonNumber;
   }
   
   public String getLessonURL() //main lesson discussion URL
   {
      return lessonGUIDURL;
   }
   
   public String getRSSTitle()
   {
      return rssName;
   }
   
   public String getLessonLevel()
   {
      return lessonLevel;
   }
   
   public String getLessonSummary()
   {
      return lessonSummary;
   }
   
   public String getLessonSubtitle()
   {
      return lessonSubtitle;   
   }

   public String getLessonEncoded() //Lesson links and jpg
   {
      return lessonEncoded;
   }
   
   @Override
   public String toString()
   {
      String tmp = lessonNumber;
      tmp.concat(" ");
      tmp.concat(rssName);
      tmp.concat(" ");
      tmp.concat(lessonName);
      return tmp;
   }
   
}