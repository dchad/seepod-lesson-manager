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
 * Date   : 7/May/2009
 * Class  : CEDICTDictionary
 * 
 * Description: Dictionary manager. This uses seperate hash maps for simplified and and traditional characters
 *              as it is not easy to map user input to simplified or traditional, so just lookup both maps.
 * 
 */
package seepodlessonmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;

public class CEDICTDictionary
{
   private File cedictFile;
   private HashMap simpDict;
   private HashMap tradDict;
   private BufferedReader inputStream;
   
   public CEDICTDictionary(Config seePodConfig)
   {
      cedictFile = new File(seePodConfig.getDatabaseDirectory() + File.separator + Config.CEDICT_DICTIONARY_FILE);
      if (cedictFile.exists())
      {
          startDictionaryLoader();
      }
      else
      {
         Debug.debug("CEDICTDictionary() - Dictionary file not found: ", cedictFile.getAbsolutePath());
      }
   }

   private void startDictionaryLoader()
   {
       DictionaryLoader dLoader = new DictionaryLoader();
       Thread t = new Thread(dLoader);
       t.start();
   }

   public String findSimplifiedDefinitions(String lookupChars)
   {

      String entry = null;
      if (simpDict.containsKey(lookupChars))
      {
          entry = (String) simpDict.get(lookupChars);
      }
      
      return entry;
   }

   public String findTraditionalDefinitions(String lookupChars)
   {

      String entry = null;
      if (tradDict.containsKey(lookupChars))
      {
          entry = (String) tradDict.get(lookupChars);
      }
      
      return entry;
   }

   public Vector findCharacterDefinitions(String lookupChars)
   {
       Vector hanziDefinitions = new Vector();

       //loop through and lookup each hanzi
       int len = lookupChars.length();
       char lucArray[] = lookupChars.toCharArray();
       for (int i = 0; i < len; i++)
       {
           String tmp = findSimplifiedDefinitions(Character.toString(lucArray[i]));
           if (tmp == null)
           {
               tmp = findTraditionalDefinitions(Character.toString(lucArray[i]));
           }
           if (tmp != null)
           {
               hanziDefinitions.add(tmp);
           }
       }

       return hanziDefinitions;
   }
   
   public Vector wordDefinitionSearch(String lookupChars)
   {
      //scan the string for words up to four characters long

      Vector wordDefinitions = new Vector();
      int size = lookupChars.length();
      for (int i = 0; i < size; i++)
      {
          for (int j = 1; j < 5; j++) //limit the search to 4 hanzi characters
          {
             int nextwordlimit = i+j;
          
             if (nextwordlimit < size+1)
             {
                String tmp = lookupChars.substring(i, nextwordlimit);
                String res = findSimplifiedDefinitions(tmp);
                if (res == null)
                {
                   res = findTraditionalDefinitions(tmp);
                }
                if (res != null)
                {
                   wordDefinitions.add(res);
                }
             }
             else
             {
                 continue;
             }
          }          
      }
      return wordDefinitions; 
   }
   
   private class DictionaryLoader implements Runnable
   {

       public DictionaryLoader ()
       {
          simpDict = new HashMap<String, String>();
          tradDict = new HashMap<String, String>();
       }
       
       @Override
       public void run() 
       {
          loadDictionary();
       }
       
       private void loadDictionary()
       {
          try 
          {
                try
                {
                    inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(cedictFile), "UTF-8"));
                } catch (FileNotFoundException ex) {
                    Debug.debug("CEDICTDictionary.loadDictionary() - file not found.", ex);
                }
          } catch (UnsupportedEncodingException ex) {
                Debug.debug("CEDICTDictionary.loadDictionary() - unsupported encoding.", ex);
            return;
          }

          //read in the lesson list if any
          String entry;
          long entryCount = 0;
          try 
          {
             while ((entry = inputStream.readLine()) != null) 
             {
                if (entry.charAt(0) == '#')
                    continue;
                int firstSpace = entry.indexOf(" ");
                int nextSpace = entry.indexOf(" ", firstSpace+1);
                String tradKey = entry.substring(0, firstSpace);
                String simpKey = entry.substring(firstSpace+1, nextSpace);
                simpDict.put(simpKey, entry);
                tradDict.put(tradKey, entry);
                
                if (Config.DEBUG)
                {
                   entryCount++;
                   //if (entryCount < 500)
                   //{
                   //   Debug.debug("CEDICTDictionary.loadDictionary() - Read line: ", entry);
                   //   Debug.debug("CEDICTDictionary.loadDictionary() - Trad key: ", tradKey);
                   //   Debug.debug("CEDICTDictionary.loadDictionary() - Simp key: ", simpKey);
                   //}
                }
             }
          } catch (IOException ex) {
                Debug.debug("CEDICTDictionary.loadDictionary() - Could not read line from buffered file reader.", ex);
             return;
          }

          if (Config.DEBUG)
             Debug.debug("CEDICTDictionary.loadDictionary() - loaded entries: ", entryCount);
          
          if (inputStream != null) 
          {
             try 
             {
                inputStream.close();
             } catch (IOException ex) {
                Debug.debug("CEDICTDictionary.loadDictionary() - Counld not close buffered file reader.", ex);
             }
          }
          return;      
       }       
   }
}
