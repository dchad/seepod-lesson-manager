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
 * Class  : ID3TagSet
 * 
 * Description: Wrapper class for ID3 tag library, isolate the dependencies in this class.
 * 
 * 
 */



package weepod;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;


public class ID3TagSet
{
   private MP3File mp3File;
   private String filePath;
   private File srcFile;
   
   private Boolean hasID3v2;
   private Boolean hasID3v1; //DEPRECATED: Too problematic, maintaining backward compatibilty with obsolete and flawed data formats should be discouraged!!!
   
   private ID3v23Tag v2tags; 
   private ID3v1Tag v1tags;   //DEPRECATED

   private Boolean id3Error;

   public ID3TagSet()
   {
      hasID3v2 = false;
      hasID3v1 = false;
      mp3File = null;
   }
   
   public ID3TagSet(File targetFile) 
   {
      //MP3File.logger.setLevel(Level.OFF); 
      //Turn off vast deluge of status messages when reading/writing id3 tags!!!
      Logger logger = Logger.getLogger("org.jaudiotagger.audio");
      logger.setLevel(Level.OFF);
      Logger id3logger = Logger.getLogger("org.jaudiotagger.tag.id3");
      id3logger.setLevel(Level.OFF);
      Logger datalogger = Logger.getLogger("org.jaudiotagger.tag.datatype");
      datalogger.setLevel(Level.OFF);

      id3Error = false;
      hasID3v2 = false;
      //hasID3v1 = false;
      v2tags = new ID3v23Tag();
      //v23tags = null;
      
      if (targetFile != null)
      {
         srcFile = targetFile;
         
         try
         {  
            mp3File = new MP3File(srcFile);
            filePath = srcFile.getCanonicalPath();
         } catch (IOException ex) {
            Debug.debug("ID3Tag() - IO exception reading file: ", srcFile.getName());
            id3Error = true;
         } catch (TagException ex) {
            Debug.debug("ID3Tag() - Invalid ID3 tag in file: ", srcFile.getName());
            id3Error = true;
         } catch (ReadOnlyFileException ex) {
            Debug.debug("ID3Tag() - Read only file: ", srcFile.getName());
            id3Error = true;
         } catch (InvalidAudioFrameException ex) {
            Debug.debug("ID3Tag() - Invalid audio header in file: ", srcFile.getName());
            id3Error = true;
         }

         if (!id3Error)
         {
            parseID3Tags();
         }
      }
      else
      {
         mp3File = null;
      }
  
      
      if (mp3File == null)
      {
         Debug.debug("ID3Tag() - No id3 metadata found.");
         return;
      }
      
      
   }

   public Boolean isValidMP3File()
   {
       return ((mp3File != null) && (!id3Error));
   }
   
   public void copyID3Tags(ID3v23Tag copyTags)
   {
      try 
      {
         v2tags.setAlbum(copyTags.getFirstAlbum());
         v2tags.setArtist(copyTags.getFirstArtist());
         v2tags.setGenre(copyTags.getFirstGenre());
         v2tags.setTitle(copyTags.getFirstTitle());
         v2tags.setTrack(copyTags.getFirstTrack());
         v2tags.setComment(copyTags.getFirstComment());
         v2tags.setYear(copyTags.getFirstYear());
      } catch (FieldDataInvalidException ex) {
         Debug.debug("ID3TagSet.copyID3Tags() - Invalid field data.");
      }
      
      hasID3v2 = true;
   }
   
   public ID3v23Tag getID3Tags()
   {
      return v2tags;
   }
   
   private void parseID3Tags()
   {
      if (mp3File.hasID3v2Tag())
      {
         AbstractID3v2Tag copyTags = mp3File.getID3v2Tag(); //have to do this to stop lib from converting tags to v2.4
         try 
         {
            v2tags.setAlbum(copyTags.getFirstAlbum());
            v2tags.setArtist(copyTags.getFirstArtist());
            v2tags.setGenre(copyTags.getFirstGenre());
            v2tags.setTitle(copyTags.getFirstTitle());
            v2tags.setTrack(copyTags.getFirstTrack());
            v2tags.setComment(copyTags.getFirstComment());
            v2tags.setYear(copyTags.getFirstYear());
            //v2tags.add(copyTags.getFirstArtwork());
            //v2tags.setEncoding("UTF-8"); DEPRECATED: unsupported operation in lib
         } catch (FieldDataInvalidException ex) {
            Debug.debug("ID3TagSet.copyID3Tags() - Invalid field data.");
         }        

         hasID3v2 = true;
         
      }
 
      if (!hasID3v2)
      {
         try 
         {
            Debug.debug("ID3TagSet.parseID3Tags() - No ID3 tags found in file: ", srcFile.getCanonicalFile());
         } catch (IOException ex) {
            Debug.debug("ID3Tag.parseID3Tags() - IO exception reading file: ", filePath);
         }
      }
  
   }
   
   public Boolean hasID3v2Tags()
   {
      return hasID3v2;
   }
   
   public String getArtist()
   {
      return v2tags.getFirstArtist();
   }
   
   public String getTrackNumber()
   {
      return v2tags.getFirstTrack();
   }
   
   public String getSongTitle()
   {
      return v2tags.getFirstTitle();
   }
   
   public String getAlbumName()
   {
      return v2tags.getFirstAlbum();
   }
   
   public String getYear()
   {
      return v2tags.getFirstYear();
   }
   
   public String getGenre()
   {
      return v2tags.getFirstGenre();
   }
   
   public String getComment()
   {
      return v2tags.getFirstComment();
   }

   public void setArtist(String artistName)
   {
      try 
      {
         v2tags.setArtist(artistName);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setTrackNumber(String trackNumber)
   {
      try 
      {
         v2tags.setTrack(trackNumber);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setSongTitle(String songName)
   {
      try 
      {
         v2tags.setTitle(songName);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setAlbumName(String albumName)
   {
      try 
      {
         v2tags.setAlbum(albumName);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setYear(String year)
   {
      try 
      {
         v2tags.setYear(year);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setGenre(String genreName)
   {
      try 
      {
         //v2tags.setGenre("Podcast");
         v2tags.setGenre(genreName);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }
   
   public void setComment(String com)
   {
      try 
      {
         v2tags.setComment(com);
          
      } catch (FieldDataInvalidException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
      hasID3v2 = true;
   }

   public void writeID3Tags()
   {
      try 
      {
         if (mp3File != null) //will be null if file has invalid/corrupt mp3 data
         {
            mp3File.setID3v2Tag(v2tags);
            mp3File.save();
         }
      } catch (IOException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      } catch (TagException ex) {
         Logger.getLogger(ID3TagSet.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   public void clearID3Tags()
   {
      
   }

   public String showAllTags()
   {
      //can also use: 
      return mp3File.displayStructureAsPlainText();
      //return mp3File.displayStructureAsXML();
   }
}