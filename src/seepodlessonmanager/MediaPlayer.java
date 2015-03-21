
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
 * Class  : MediaPlayer
 * 
 * Description: Thread for managing MP3 audio playback.
 * 
 * 
 * 
 * 
 */

package seepodlessonmanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;

public class MediaPlayer implements Runnable
{
   private MediaPlayerPanel mpPanel;
   private File nowPlaying;
   private BufferedInputStream is;
   private MP3PlayerJZ mp3Player;
   private Thread playerThread;
   
   public MediaPlayer(MediaPlayerPanel parent, File mp3File)
   {
     
      if (mp3File != null)
      {
         nowPlaying = mp3File; 
      }
      else
      {
         Debug.debug("MediaPlayer() - MP3 file is null.");
         return;
      }
      if (parent != null)
      {
         mpPanel = parent;
      }
      else
      {
         Debug.debug("MediaPlayer() - MediaPlayerPanel is null.");
         return;
      }
      is = null;

      FileInputStream fis = null;
      try 
      {
         fis = new FileInputStream(nowPlaying);
      } catch (FileNotFoundException ex) {
         Debug.debug("MediaPlayer() - File not found exception.");
         return;
      }
      is = new BufferedInputStream(fis);
      try 
      {
         mp3Player = new MP3PlayerJZ(is);
         //now add a volume controller
      } catch (JavaLayerException ex) {
         Debug.debug("MediaPlayer() - JavaLayerException.");
      }

      //now add a volume controller
      
   }
   
  public synchronized void play()
  {
     playerThread = new Thread(mp3Player);
     playerThread.setPriority(Thread.MAX_PRIORITY); //make it go fast
     playerThread.start();
     monitorPlayBackProgress(); 
  }
  
  private void monitorPlayBackProgress()
  {
     int lastPosition = 0;
     // while not finished get position and send to MediaPlayerPanel to update progress slider
     while (!mp3Player.isComplete())
     {
         try 
         {
            Thread.sleep(100);
         } catch (InterruptedException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
         }
         int pos = mp3Player.getPosition();
         if (pos > 0)
         {
            pos = pos/1000; //convert from milliseconds to seconds
         }
         if (pos > lastPosition)
         {
            lastPosition = pos;
            mpPanel.updateProgressBar(pos);
         }

         if (mpPanel.stop())
         {
            mp3Player.close();
            break;
         }
         if (mpPanel.pause())
         {
            doPause();
         }

     }
     try 
     {
         is.close();
     } catch (IOException ex) {
         Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
     }
     mpPanel.playBackComplete();
  }

  public void close()
  {
     mp3Player.close();
  }
  
   public void run() 
   {
      play();
   }

   private void doPause()
   {
      mp3Player.pause();
    
      while(mpPanel.pause())
      {
         try 
         {
            Thread.sleep(500);
         } catch (InterruptedException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      mp3Player.unpause();
    
   }
}