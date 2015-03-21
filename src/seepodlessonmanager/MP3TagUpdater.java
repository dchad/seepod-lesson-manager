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
 * Class  : MP3TagUpdater
 * 
 * Description: Called by MP3Editor to do a recursive mp3 tag update on a directory tree.
 * 
 * 
 */



package seepodlessonmanager;

import java.io.File;

public class MP3TagUpdater implements Runnable
{
  
  private MP3Editor controller;
  private File topDirectory;
  private Config seePodConfig;
  private String id3TrackNumber;
  private String id3SongTitle;
  private String id3AlbumName;
  private String id3ArtistName;
  private String id3Year;
  private String id3Comment;
   
  private Boolean setTrackNumber;
  private Boolean setSongTitle;
  private Boolean setAlbumName;
  private Boolean setArtistName;
  private Boolean setYear;
  private Boolean setComment;
  
  public MP3TagUpdater(File startDir, MP3Editor mp3Control, Config spc)
  {
     if (mp3Control != null)
     {
        controller = mp3Control;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("MP3TagUpdater() - Control panel object is null."); 
        return;
     }
     if (spc != null)
     {
        seePodConfig = spc;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("LessonScanner() - Config is null."); 

     }
     if (startDir != null)
     {
        topDirectory = startDir;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("LessonScanner() - Start directory is null."); 

     }                
     
     id3TrackNumber = null;
     id3SongTitle = null;
     id3AlbumName = null;
     id3ArtistName = null;
     id3Year = null;
     id3Comment = null;
     
     setTrackNumber = controller.getUpdateTrackNumberTag();
     setSongTitle = controller.getUpdateTitleTag();
     setAlbumName = controller.getUpdateAlbumTag();
     setArtistName = controller.getUpdateArtistTag();
     setYear = controller.getUpdateYearTag();
     setComment = controller.getUpdateCommentTag();
     

  }
  
  void startScan() 
  {
      // here we go!!!
      if (Config.DEBUG)
      {
         Debug.debug("MP3Editor.startScan(): started directory scan...");
      }
      
      id3TrackNumber = controller.getTrackNumberTag();
      id3SongTitle = controller.getTitleTag();
      id3AlbumName = controller.getAlbumTag();
      id3ArtistName = controller.getArtistTag();
      id3Year = controller.getYearTag();
      id3Comment = controller.getCommentTag();
      
      if (topDirectory != null)
      {
         if (topDirectory.exists())
         {
            if (topDirectory.isDirectory())
            {
               scanDirectory(topDirectory);
            }
         }
      }
      //Debug.debug("LessonScanner.startScan() - Finished scan.");
  }
  
  private void scanDirectory(File startDir)
  {

     File[] files = startDir.listFiles();
     
      if (files.length > 0)
      {
         for (int i = 0; i < files.length; i++)
         {

           //Debug.debug("MP3TagUpdater.scanDirectory() - Scanning file: " + files[i].getName());

           File f = files[i];
           if (f.exists())
           {
              if (f.isDirectory())
              {
                 scanDirectory(f);
              }
              else
              {
                 String tmp = f.getAbsolutePath();
                 if (tmp.endsWith(".mp3"))
                 {
                    writeMP3Tags(f);
                    //controller.printStatusMessage("Found lesson: " + tmp);
                 }   
              }
           }
        }
     }
  }
  
  public void run()
  {
     startScan();
  }
  
  private void writeMP3Tags(File of)
  {
      
      ID3TagSet tagger = new ID3TagSet(of);

      if (tagger == null)
      {
         Debug.debug("MP3TagUpdate.writeMP3Tags() - ID3 tag set is null.");
         return;
      }
      //get the text field values and update selected file
      if (setAlbumName)
      {
         if (id3AlbumName != null)
         {
            tagger.setAlbumName(id3AlbumName);
         }
      }
      if (setArtistName)
      {
         if (id3ArtistName != null)
         {
            tagger.setArtist(id3ArtistName);
         }
      }
      if (setSongTitle)
      {
         if (id3SongTitle != null)
         {
            tagger.setSongTitle(id3SongTitle);
         }
      }
      if (setTrackNumber)
      {
         if (id3TrackNumber != null)
         {
            tagger.setTrackNumber(id3TrackNumber);
         }
      }
      if (setYear)
      {
         if (id3Year != null)
         {
            tagger.setYear(id3Year);
         }
      }
      if (setComment)
      {
         if (id3Comment != null)
         {
            tagger.setComment(id3Comment);
         }
      }
      
      tagger.writeID3Tags();
      
      controller.printStatusMessage("Updated tags in file: " + of.getName());
      
      return;   
  }   
   
  
}