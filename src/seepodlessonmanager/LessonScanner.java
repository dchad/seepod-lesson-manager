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
 * Class  : Lesson Scanner
 * 
 * Description: Recursively scans lesson directory tree searching for lessons to add to a lesson set.
 * 
 *
 */



package seepodlessonmanager;

import java.io.File;

public class LessonScanner implements Runnable
{
  
  private LessonManagerControl controller;
  private Config seePodConfig;
  private LessonListPanel manager;
  private Boolean terminateScan;
   
  public LessonScanner(LessonListPanel parent, Config spc, LessonManagerControl lmc)
  {
     if (lmc != null)
     {
        controller = lmc;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("LessonScanner() - Control panel object is null."); 
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
     if (parent != null)
     {
        manager = parent;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("LessonScanner() - Parent is null."); 
     }
     terminateScan = false;

  }
  
  void startScan() //throws InterruptedException, IOException
  {
      // here we go!!!
      if (Config.DEBUG)
      {
         Debug.debug("LessonScanner.start(): started directory scan...");
      }
      
      String startDir = seePodConfig.getLessonDirectory();
      if (startDir != null)
      {
         File topDir = new File(startDir);
         if (topDir.exists())
         {
            if (topDir.isDirectory())
            {
               scanDirectory(topDir);
            }
         }
      }
      //Debug.debug("LessonScanner.startScan() - Finished scan.");
  }
  
  private void scanDirectory(File startDir)
  {
     if (terminateScan)
     {
        return;
     }

     File[] files = startDir.listFiles(new DirectoryAndMP3FileFilter());
     String path = null;
     path = startDir.getAbsolutePath();

      if (files.length > 0)
      {
         //now sort the file list so they are nicely alphabetical order/Hanzi character order
         //java.util.Arrays.sort(files, java.text.Collator.getInstance(Locale.CHINESE));
         //not necessary, already in sorted order
         for (int i = 0; i < files.length; i++)
         {
           //Debug.debug("LessonScanner.scanDirectory() - Scanning file: " + files[i].getAbsolutePath());
           File f = files[i];
           String filePath = f.getAbsolutePath();
           if (f.exists())
           {
              if (f.isDirectory())
              {
                 scanDirectory(f);
              }
              else
              {
                 //check for cpod and wlcp lesson mp3 files
                 String tmp = f.getName();
                 String listItem = null;
                 //Debug.debug("LessonScanner.scanDirectory() - Scanning file: " + tmp);
                 if (tmp.endsWith("pb.mp3") || tmp.endsWith("pr.mp3")) //check for manually downloaded lesson files
                 {
                       
                     //Lesson spLesson = new Lesson(f);
                     ID3TagSet tags = new ID3TagSet(f);
                   
                     if (tags.hasID3v2Tags())
                     {
                        listItem = "(" + tags.getTrackNumber() + ") - " + tags.getSongTitle(); // + " - " + tmp;
                     }
                     else
                     {
                        listItem = "Untagged - " + tmp;
                     }
                     manager.addLesson(listItem, filePath);
                     controller.printStatusMessage("Found lesson: " + tmp);
                 }
                 else if (tmp.endsWith("expansion_sentences.mp3")) //check for WLCP generated files
                 {                   
                     String tmpFileName = f.getAbsolutePath().replace("expansion_sentences.mp3", "lesson.mp3");
                     File lessonFile = new File(tmpFileName);
                     if (lessonFile.exists()) //no lesson file so check for tags in the expansion file
                     {
                        ID3TagSet tags = new ID3TagSet(lessonFile);
                        if (tags.hasID3v2Tags())
                        {
                           listItem = "(" + tags.getTrackNumber() + ") - " + tags.getSongTitle(); // + " - " + tmpFileName;
                           filePath = lessonFile.getAbsolutePath(); //set the file path to the main lesson file
                        }
                        else
                        {
                           listItem = "Untagged - " + tmpFileName;
                        }
                     }
                     else //
                     {
                        ID3TagSet tags = new ID3TagSet(f); //no lesson file so check if there are ID3 tags in the expansion file
                        if (tags.hasID3v2Tags())
                        {
                           listItem = "(" + tags.getTrackNumber() + ") - " + tags.getSongTitle(); // + " - " + tmp;
                        }
                        else
                        {
                           listItem = "Untagged - " + tmp;
                        }
                     }
                     manager.addLesson(listItem, filePath);
                     controller.printStatusMessage("Found lesson: " + tmp);
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
  
  public synchronized void setTerminateScan(Boolean bTerm)
  {
     terminateScan = true;
  }

  private class DirectoryAndMP3FileFilter implements java.io.FileFilter
  {
     public boolean accept(File f)
     {
        String filename = f.getName().toLowerCase();
        return (f.isDirectory() || filename.endsWith(".mp3"));

     }
  }
}