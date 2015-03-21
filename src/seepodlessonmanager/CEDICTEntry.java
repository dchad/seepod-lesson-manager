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
 * Class  : CEDICTEntry
 * 
 * Description: Dictionary entry parser, see cedict_ts.u8 for entry format.
 * 
 */

package seepodlessonmanager;

public class CEDICTEntry 
{

   private String tradChars;
   private String simpChars;
   private String pinyinNumber;
   private String pinyinAccent;
   private String englishDefinitions;

   public CEDICTEntry(String dentry)
   {
      PinYinConverter pyc = new PinYinConverter();
      
      int hanzilimit = dentry.indexOf('[');
      String[] hanziSplit = dentry.split(" ", hanzilimit);

      tradChars = hanziSplit[0];
      simpChars = hanziSplit[1];

      int pinyinlimit = dentry.indexOf(']');
      pinyinNumber = dentry.substring(hanzilimit+1, pinyinlimit);
      try 
      {
         pinyinAccent = pyc.convert(pinyinNumber);
      } catch (Exception ex) {
         Debug.debug("CEDICTEntry() - pinyin converter exception.");
      }
      
      String definition = dentry.substring(pinyinlimit+3);
      //definition = definition.substring(2);
      englishDefinitions = definition.replace("/", "; ");
   }

   public String getSimplifiedChars()
   {
       return simpChars;
   }

   public String getTraditionalChars()
   {
       return tradChars;
   }

   public String getPinYinNumbered()
   {
       return pinyinNumber;
   }

   public String getPinYinAccented()
   {
       return pinyinAccent;
   }

   public String getEnglishDefinitions()
   {
       return englishDefinitions;
   }

}
