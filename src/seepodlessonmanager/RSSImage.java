/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package seepodlessonmanager;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author derek
 */
public class RSSImage implements Runnable
{
   String aFile;
   String aURL;

   RSSImage(String u, String s)
   { 
     aURL  = u;
     aFile = s;
   }

   public void downloadImage() 
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

       // output
       //String homePath = RSSImage.class.
       fo = new FileOutputStream("database" + File.separator + aFile);

       int bytesIn = di.read(b, 0, 1024);
       while(bytesIn != -1) 
       {
         fo.write(b, 0, bytesIn);
         bytesIn = di.read(b, 0, 1024);
       }
       Debug.debug("RSSImage.downloadImage() - downloaded image: ", aURL);
       di.close();  
       fo.close();                
     }
     catch (Exception ex) { 
        Debug.debug("RSSImage.downloadImage() - could not download image: ", aURL);
     }

   }

    @Override
    public void run()
    {
        downloadImage();
    }
}