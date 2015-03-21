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
 * Class  : WLCPThread
 * 
 * Description: WeLoveChinesePod lesson downloader class, executes the WLCP Python script in a separate Process.
 * 
 * 
 */



package weepod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class WLCPThread implements Runnable
{
  
  private DownloadStatus statusOut;
  private Config seePodConfig;
  private String wlcpDirectory;
  private File wlcpScriptFile;
  private String pythonExecutable;
  private Process wlcpRunTime;
  private Boolean terminateWLCP;
  private WLCPController controller;
   
  public WLCPThread(WLCPController wlcpc, Config spc, DownloadStatus dls)
  {
     if (dls != null)
     {
        statusOut = dls;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("WLCPThread() - Download status object is null."); 
     }
     if (spc != null)
     {
        seePodConfig = spc;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("WLCPThread(): Config is null."); 
     }
     if (wlcpc != null)
     {
        controller = wlcpc;
     }
     else
     {
        if (Config.DEBUG)
           Debug.debug("WLCPThread() - WLCP controller object is null."); 
     }
  }
  
  public void start()
  {
      wlcpDirectory = seePodConfig.getWLCPDirectory();
      pythonExecutable = seePodConfig.getPythonExecutable();
      File python = new File(pythonExecutable);
      if (!python.exists())
      {
         statusOut.printStatusLine("Could not find Python, check Python path in the settings dialogue.");
         if (Config.DEBUG)
            Debug.debug("WLCPThread.start(): Could not find python.");
         return;
      }
      
      // first check the directory and script exist
      String[] execStr = new String[3];
     
      String pythonArg = wlcpDirectory + File.separator + seePodConfig.getWLCPScript(); //try the config setting first, then the defaults
      execStr[0] = pythonExecutable;
      execStr[1] = "-u"; //unbuffered output required, otherwise get long delays between status messages!
      wlcpScriptFile = new File(pythonArg);
      if (wlcpScriptFile.exists())
      {
         execStr[2] = pythonArg;
      }
      else
      {
         pythonArg = wlcpDirectory + File.separator + Config.WLCP_PYTHON_SCRIPT;
         wlcpScriptFile = new File(pythonArg);
         if (wlcpScriptFile.exists())
         {
            execStr[2] = pythonArg;
         }
         else
         {
            pythonArg = wlcpDirectory + File.separator + Config.WLCP_ALT_PYTHON_SCRIPT;
            wlcpScriptFile = new File(pythonArg);
            if (wlcpScriptFile.exists())
            {
               execStr[2] = pythonArg;
            }
            else
            {
               statusOut.printStatusLine("Could not find WLCP script file.");
               if (Config.DEBUG)
               {
                  Debug.debug("WLCPThread.start() - Could not find script: ", wlcpScriptFile.getAbsolutePath());
               }
               return;
            }
         }
      }
      
      if (wlcpScriptFile.exists())
      {
        //Debug.debug("WLCPThread.start(): starting WLCP: ", execStr);
        statusOut.printStatusLine("Starting WLCP script file...");
         try 
         {   
            if (Config.DEBUG)
            {
                Debug.debug("WLCPThread.start() : ", execStr[0]);
                Debug.debug("WLCPThread.start() : ", execStr[1]);
                Debug.debug("WLCPThread.start() : ", execStr[2]);
                
            }
            wlcpRunTime = Runtime.getRuntime().exec(execStr); //go the process
         } catch (IOException ex) {
            //JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Python failed to start, check Python settings.", "Alert", JOptionPane.ERROR_MESSAGE);
            Debug.debug("WLCPThread.start(): IO exception while starting process.");
            return;
         }
         try 
         {
            //now connect to the process and display the output
            monitorWLCPThread();
         } catch (IOException ex) {
            Debug.debug("WLCPThread.start(): IO exception while monitoring process.");
         }
         
      }
      else
      {
         Debug.debug("WLCPThread.start() - could not find wlcp script: ", wlcpScriptFile.getAbsolutePath());
      }
     
  }
  
     
  private void monitorWLCPThread() throws IOException
  {
      //lets go!
      Boolean running = true;
      terminateWLCP = false;
      //Timer wlcpTimeout; implement a 5 minute timeout if WLCP crashes or stops sending output - currentTimeMillis().
      
      //BufferedInputStream wlcpOutput = new BufferedInputStream(wlcpRunTime.getInputStream()); //does not work well, use a buffered reader!!!
      BufferedReader wlcpOutput = new BufferedReader(new InputStreamReader(wlcpRunTime.getInputStream()));

      while(running)
      {
         String inStr = null;
         try 
         {
            Thread.sleep(250); //have a little rest while waiting for WLCP to do something
         } catch (InterruptedException ex) {
            Debug.debug("WLCPThread.monitorWLCPThread() - Thread.sleep() interrupted.");
         }
         if (wlcpOutput.ready())
         {
             inStr = wlcpOutput.readLine();
         }
         else
         {
            int ev = -1;
            try
            {
               Thread.sleep(1000); //have another little rest while waiting for WLCP to do something
            } catch (InterruptedException ex) {
               Debug.debug("WLCPThread.monitorWLCPThread() - Thread.sleep() interrupted.");
            }
            try
            {
               ev = wlcpRunTime.exitValue();
            } catch (IllegalThreadStateException ex) { } //do nothing as WLCP is still running 
            if (ev >= 0)  //WARNING: exit value is system dependent, have to check this on cross-platform tests!!!
            {
               terminateWLCP = true;
               if (Config.DEBUG)
                  Debug.debug("WLCPThread.monitorWLCPThread() - Exit value is: ", ev);
            }
            //Debug.debug("WLCPThread.monitorWLCPThread() - alive...");
         }
         
         if (inStr != null)
         {
            statusOut.printStatusLine(inStr);
            if (Config.DEBUG)
               Debug.debug("WLCPThread.monitorWLCPThread() - WLCP data in: ", inStr);
         }
                 
         if (terminateWLCP)  // for the cancel button if the user gets sick of waiting
         {
            wlcpRunTime.destroy();
            running = false;
            statusOut.printStatusLine("WLCP download completed.");
            if (Config.DEBUG)
               Debug.debug("WLCPThread.monitorWLCPThread() : WLCP terminated. ");

         }
      }

      wlcpOutput.close(); // we are all done
      
      controller.wlcpDownloadFinished(); //notify the controller
  }
  
  @Override
  public void run()
  {  
     start();
  }
  
  public synchronized void setTerminateWLCP()
  {
     if (Config.DEBUG)
        Debug.debug("WLCPThread.setTerminateWLCP() : setting terminate.");
     terminateWLCP = true;
  }
  
  
}