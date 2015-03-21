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
 * Class  : ChinesePodLesson 
 * 
 * Description: Checks that lesson file exists then creates pathnames for the other lesson files, review, dialog, pdf etc.
 *              Used by LessonScanner to obtain lesson files during directory scans.
 * 
 */


package seepodlessonmanager;

import java.io.File;

public class ChinesePodLesson implements SPConstants
{

   private File   lessonMP3File;
   private String lessonFileName;
   private String lessonSimpPDFFileName;
   private String lessonTradPDFFileName;
   private String lessonRevFileName;
   private String lessonDialogFileName;
   
   private String wlcpExpansionFileName;
   private String wlcpVocabFileName;
   private String wlcpJPEGFileName;

   private Boolean hasWLCPFiles;
   private Boolean hasWLCPLesson; 
   private Boolean hasCPODLesson;

   private ID3TagSet lessonID3Tags;
   private boolean hasID3Tags;

   private int lessonStatus;


   public ChinesePodLesson(File lessonFile)
   {
      
      hasWLCPFiles = false;
      hasWLCPLesson = false;  
      hasCPODLesson = false;
      hasID3Tags = false;
      lessonStatus = SPConstants.LESSON_STATUS_NONE;

      if (lessonFile == null)
      {
         Debug.debug("ChinesePodLesson() - MP3 file is null!");
         return;
      }
      if (!lessonFile.exists())
      {
         Debug.debug("ChinesePodLesson() - MP3 file does not exist: ", lessonFile);
         return;
      }
      
      //Debug.debug("ChinesePodLesson() - Creating lesson for MP3 file: ", lessonFile.getName());
      
      String tmp = null;
      String filePath = null;

      tmp = lessonFile.getName();
   
      filePath = lessonFile.getAbsolutePath();
      if (Config.DEBUG)
      {
         Debug.debug("ChinesePodLesson() - File path: ", filePath);
      }

      if (tmp != null)
      {
         if (tmp.startsWith("chinesepod")) //manually downloaded from chinesepod.com
         {
            hasCPODLesson = true;
            if (tmp.endsWith("pb.mp3"))
            {
               lessonFileName = filePath; //the main lesson file
               lessonSimpPDFFileName = filePath.replace("pb.mp3", ".pdf"); 
               lessonTradPDFFileName = lessonSimpPDFFileName;
               lessonRevFileName = filePath.replace("pb.mp3", "rv.mp3");
               lessonDialogFileName = filePath.replace("pb.mp3", "dg.mp3");
               lessonMP3File = lessonFile;
               lessonID3Tags = new ID3TagSet(lessonFile);
               if (lessonID3Tags.hasID3v2Tags())
               {
                  hasID3Tags = true;
               }
 
            }
            else if (tmp.endsWith("pr.mp3"))
            {
               lessonFileName = filePath; //the main lesson file
               lessonSimpPDFFileName = filePath.replace("pr.mp3", ".pdf"); 
               lessonTradPDFFileName = lessonSimpPDFFileName;
               lessonRevFileName = filePath.replace("pr.mp3", "rv.mp3");
               lessonDialogFileName = filePath.replace("pr.mp3", "dg.mp3");
               lessonMP3File = lessonFile;
               lessonID3Tags = new ID3TagSet(lessonFile);
               if (lessonID3Tags.hasID3v2Tags())
               {
                  hasID3Tags = true;
               }
  
            }

         }
         else if (tmp.endsWith("expansion_sentences.mp3")) //lesson downloaded with WLCP
         {
            wlcpExpansionFileName = filePath;
            hasWLCPFiles = true;
            
            lessonFileName = filePath.replace("expansion_sentences.mp3", "lesson.mp3"); //the main lesson mp3 file
            //Debug.debug("ChinesePodLesson() - file path: ", lessonFileName);
            lessonDialogFileName = filePath.replace("expansion_sentences.mp3", "dialogue.mp3");
            //Debug.debug("ChinesePodLesson() - file path: ", lessonDialogFileName);
            lessonRevFileName = filePath.replace("expansion_sentences.mp3", "review.mp3");
            //Debug.debug("ChinesePodLesson() - file path: ", lessonRevFileName);
            lessonSimpPDFFileName = filePath.replace("_expansion_sentences.mp3", "-simp.pdf");
            //Debug.debug("ChinesePodLesson() - file path: ", lessonSimpPDFFileName);
            lessonTradPDFFileName = filePath.replace("_expansion_sentences.mp3", "-trad.pdf");
            //Debug.debug("ChinesePodLesson() - file path: ", lessonTradPDFFileName);
            wlcpJPEGFileName = filePath.replace("_expansion_sentences.mp3", ".jpeg");
            //Debug.debug("ChinesePodLesson() - file path: ", wlcpJPEGFileName);
            wlcpVocabFileName = filePath.replace("expansion_sentences.mp3", "vocabulary_sentences.mp3");
            //Debug.debug("ChinesePodLesson() - file path: ", wlcpVocabFileName);
            
            lessonMP3File = new File(lessonFileName);
            if (lessonMP3File.exists())
            {
               hasWLCPLesson = true;
               lessonID3Tags = new ID3TagSet(lessonFile);
               if (lessonID3Tags.hasID3v2Tags())
               {
                  hasID3Tags = true;
               }
               
            }
            else
            {
               //try the expansion file for id3 tags
               
               lessonID3Tags = new ID3TagSet(lessonFile);
               if (lessonID3Tags.hasID3v2Tags())
               {
                  hasID3Tags = true;
               }
               else
               {
                  Debug.debug("ChinesePodLesson() - No ID3 tags found for lesson: ", lessonFile.getName());
               }
            }
            
         }
         else
         {
            //some unknown file
            lessonMP3File = lessonFile;
         }
      }


   }
   
   @Override
   public String toString()
   {
      String tmp = null;
      
      if (hasID3Tags) //should use scanf here
      {
         tmp = lessonID3Tags.getSongTitle() + " - " + "(" + lessonID3Tags.getTrackNumber() + ") - " + lessonMP3File.getName();
    
      }
      else
      {
         //no id3 tags and no lesson file, so it is probably a nontagged expansion file generated by an early version of WLCP or an unknown file 
         tmp = "Untagged - " + lessonMP3File.getName();
      }
      return tmp;
   }

   public Boolean hasMP3Tags()
   {
      return hasID3Tags;
   }
   
   public Boolean hasWLCPFiles()
   {
      return hasWLCPFiles;
   }
   
   public Boolean hasWLCPLesson()
   {
      return hasWLCPLesson;
   }

   public Boolean hasCPODLesson()
   {
      return hasCPODLesson;
   }

   public File getCPODLessonFile()
   {
      return lessonMP3File;
   }
   
   public File getWLCPLessonFile()
   {
      return lessonMP3File;
   }
   
   public ID3TagSet getID3Tags()
   {
      ID3TagSet tags = null;
      if (hasID3Tags)
         tags = lessonID3Tags;
      else
         Debug.debug("ChinesePodLesson.getID3Tags() - No ID3 tags in file: ", lessonMP3File.getName());
      
      return tags;
   }
   

   public String getLessonItem(int key)
   {
      String item = null;
      
      switch (key)
      {
         case LESSON_MP3_FILE_KEY:           item = lessonFileName; break;
         case LESSON_REV_FILE_KEY:           item = lessonRevFileName; break;
         case LESSON_DIALOG_FILE_KEY:        item = lessonDialogFileName; break;
         case LESSON_SIMP_PDF_FILE_KEY:      item = lessonSimpPDFFileName; break;
         case LESSON_TRAD_PDF_FILE_KEY:      item = lessonTradPDFFileName; break;
         case LESSON_WLCP_VOCAB_FILE_KEY:      item = wlcpVocabFileName; break;
         case LESSON_WLCP_JPG_KEY:             item = wlcpJPEGFileName; break;
         case LESSON_WLCP_EXPANSION_FILE_KEY:  item = wlcpExpansionFileName; break;
         default:
            Debug.debug("ChinesePodLesson.getLessonItem() - Invalid lesson key: ", key);
      }
      //Debug.debug("ChinesePodLesson.getLessonItem() - returning item: ", item);
      return item;

   }

   public int getLessonStatus()
   {
      return lessonStatus;
   }

   public void setLessonStatus(int ls)
   {
      lessonStatus = ls;
   }
}
