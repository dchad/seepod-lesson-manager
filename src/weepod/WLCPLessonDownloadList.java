/*  Copyright 2009 Derek Chadwick
 
    This file is part of the WeePod WLCP GUI package.

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
 * Class  : WLCPLessonDownloadList
 * 
 * Description: JPanel to display, edit and save lessons.txt used by WLCP to download lessons from ChinesePod.com.
 * 
 * 
 */


package weepod;

//WLCPLessonDownloadList, WLCPController and Download status all go in the Download tab.


import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class WLCPLessonDownloadList extends JPanel 
{
   
   private JTextArea lessonList;
   private String wlcpDirectory;
   private File wlcpLessonFile;
   private DownloadStatus statusOut;
   private Config seePodConfig;
   private WLCPThread wlcpExec;
   private Thread wlcpThread;
   private WLCPController controller;

   
   public WLCPLessonDownloadList(WLCPController wlcpc, Config spc, DownloadStatus dlStatus) 
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(600,600));
      setBorder(BorderFactory.createTitledBorder("WLCP Download List"));
      
      if (dlStatus != null)
      {
         statusOut = dlStatus;
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList() - Invalid download status panel.");
      }
      
      if (spc != null)
      {
         seePodConfig = spc;
         wlcpDirectory = seePodConfig.getWLCPDirectory();
      }
      else
      {
         wlcpDirectory = ".";
      }
      
      if (wlcpc != null)
      {
         controller = wlcpc;
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList() - Invalid WLCP contoller panel.");
      }
      
      wlcpExec = null;
      wlcpThread = null;
           
      lessonList = new JTextArea(50,50); //(rows,columns)
      
      JButton startWLCPDownloadButton = new JButton("Start");
      startWLCPDownloadButton.setToolTipText("Start WLCP to download selected lessons.");
      startWLCPDownloadButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            try 
            {
               startDownloadButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(WLCPLessonDownloadList.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
      
      JButton stopWLCPDownloadButton = new JButton("Stop");
      stopWLCPDownloadButton.setToolTipText("Terminate the WLCP download.");
      stopWLCPDownloadButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            try 
            {
               cancelDownloadButtonActionPerformed(evt);
            } catch (IOException ex) {
               Logger.getLogger(WLCPLessonDownloadList.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      });
      
      JButton saveWLCPDownloadButton = new JButton("Save");
      saveWLCPDownloadButton.setToolTipText("Save the selected lessons to the file: lessons.txt");
      saveWLCPDownloadButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            saveLessonFile();
         }
      });
      
      Box wlcpButtonBox = Box.createHorizontalBox();
      wlcpButtonBox.add(Box.createHorizontalGlue());
      wlcpButtonBox.add(startWLCPDownloadButton);
      wlcpButtonBox.add(Box.createHorizontalGlue());
      wlcpButtonBox.add(stopWLCPDownloadButton);
      wlcpButtonBox.add(Box.createHorizontalGlue());
      wlcpButtonBox.add(saveWLCPDownloadButton);
      wlcpButtonBox.add(Box.createHorizontalGlue());
      
      JScrollPane scrollpane = new JScrollPane();
      scrollpane.getViewport().add(lessonList);

      add(scrollpane);
      add(Box.createRigidArea(new Dimension(0,10)));
      add(wlcpButtonBox);
      add(Box.createRigidArea(new Dimension(0,5)));
      
      File startDir = new File(wlcpDirectory);
      
      if (startDir.exists())  //initialise the default files
      {
         wlcpLessonFile = new File(wlcpDirectory + File.separator + Config.WLCP_LESSON_FILE); 
         if (wlcpLessonFile.exists())
         {
            openLessonFile();
         }
         //*******NOTE***************** do not open lesson.txt unless the file already exists
       
         if (Config.DEBUG)
         {
            Debug.debug("WLCPLessonDownloadList() - Lesson file: ", wlcpLessonFile.getAbsolutePath());
         }
      }
      else
      {
         statusOut.printStatusLine("WLCP directory not found: " + wlcpDirectory);
      }
   }
   
   public void addRSSLesson(String lessonName)
   {
      if (lessonName != null)
      {
         lessonList.append(lessonName);
         //lessonList.append(Config.LINE_FEED); horrible line separator problems when using JTextArea
         lessonList.setCaretPosition(lessonList.getText().length());
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList.addRSSLesson() - Lesson name is null.");
      }
      return;
   }

   public void addLesson(String lessonName)
   {
      if (lessonName != null)
      {
         lessonList.append(lessonName);
         lessonList.append(Config.LINE_FEED);
         lessonList.setCaretPosition(lessonList.getText().length());
      }
      else
      {
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList.addLesson() - Lesson name is null.");
      }
      return;
   }
   
   public void saveLessonFile()
   {
      String lineOut;  
      PrintWriter outputStream = null;

      wlcpDirectory = seePodConfig.getWLCPDirectory();
      wlcpLessonFile = new File(wlcpDirectory + File.separator + Config.WLCP_LESSON_FILE); 
      if (!wlcpLessonFile.exists())
      {
         try 
         {
            wlcpLessonFile.createNewFile();
         } catch (IOException ex) {
            statusOut.printStatusLine("Could not create lesson.txt file.");
            if (Config.DEBUG)
               Debug.debug("WLCPLessonDownloadList.saveLessonFile() - Could not create lesson.txt file.", ex);
            return;
         }
      }
      try 
      {
         outputStream = new PrintWriter(wlcpLessonFile, "UTF-8");
         //outputStream = new PrintWriter(new FileWriter(wlcpLessonFile));
         lineOut = lessonList.getText();
         String[] strseq = lineOut.split("\\s");
         for (int i = 0; i < strseq.length; i++)
         {
            String outstr = strseq[i];
            if (outstr.length() > 1)
            {
               outputStream.println(outstr);
               //outputStream.print("\r\n");
               if (Config.DEBUG)
                  Debug.debug("WLCPLessonDownloadList.saveLessonFile() - wrote: ", outstr);
            }
         }
      } catch (IOException ex) {
         statusOut.printStatusLine("Could not write to lessons.txt file.");
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList.saveLessonFile() - Could not create buffered file writer.", ex);
         return;
      }          
           
      if (outputStream != null) 
      {
          outputStream.close();
      }
      statusOut.printStatusLine("Saved: " + wlcpLessonFile.getAbsolutePath());

      return;
   }
   
   public void openLessonFile() 
   {
      BufferedReader inputStream = null;
      
      if (!wlcpLessonFile.exists())
      {
         try 
         {
            wlcpLessonFile.createNewFile();
         } catch (IOException ex) {
            statusOut.printStatusLine("Could not open lessons.txt file.");
            if (Config.DEBUG)
               Debug.debug("WLCPLessonDownloadList.openLessonFile() - Counld not create lesson.txt file.", ex);
            return;
         }
      }
      try 
      {
         try 
         {
            inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(wlcpLessonFile), "UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WLCPLessonDownloadList.class.getName()).log(Level.SEVERE, null, ex);
         }
      } catch (FileNotFoundException ex) {
         statusOut.printStatusLine("Could not read lessons.txt file.");
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList.openLessonFile() - Could not create buffered file reader.", ex);
         return;
      }
      
      //read in the lesson list if any
      String lesson;
      try 
      {
         while ((lesson = inputStream.readLine()) != null) 
         {
            addLesson(lesson);
            if (Config.DEBUG)
               Debug.debug("WLCPLessonDownloadList.openLessonFile() - Read line: ", lesson);
         }
      } catch (IOException ex) {
         statusOut.printStatusLine("Could not read lessons.txt file.");
         if (Config.DEBUG)
            Debug.debug("WLCPLessonDownloadList.openLessonFile() - Could not read line from buffered file reader.", ex);
         return;
      }
      
      statusOut.printStatusLine("Opened: " + wlcpLessonFile.getAbsolutePath());
      
      if (inputStream != null) 
      {
         try 
         {
            inputStream.close();
         } catch (IOException ex) {
            statusOut.printStatusLine("Could not close lessons.txt file.");
            if (Config.DEBUG)
               Debug.debug("WLCPLessonDownloadList.openLessonFile() - Counld not close buffered file reader.", ex);
         }
      }
      return;
   }
   
   private void startDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
       saveLessonFile(); 
       wlcpExec = new WLCPThread(controller, seePodConfig, statusOut);
       wlcpThread = new Thread(wlcpExec);
       wlcpThread.start();
       statusOut.printStatusLine("Starting WLCP...");
       //Debug.debug("WLCPLessonDownloadList.startDownloadButton: starting wlcp...");
   }
   
   private void cancelDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException
   {
      if (wlcpExec != null)
      {
         statusOut.printStatusLine("Terminating WLCP...");
         wlcpExec.setTerminateWLCP();
      }
      //Debug.debug("WLCPLessonDownloadList.cancelDownloadButton: terminating wlcp...");
   }
   
}