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
 * Class  : VocabularyItem
 * 
 * Description: Vocabulary item class.
 * 
 */
package seepodlessonmanager;

public class VocabularyItem
{
   String word;
   String pinyin;
   String definition;

   public VocabularyItem(String w, String p, String d)
   {
      word = "Unknown";
      pinyin = "Unknown";
      definition = "Unknown";

      if (w != null)
      {
         word = w;
      }
      if (p != null)
      {
         pinyin = p;
      }
      if (d != null)
      {
         definition = d;
      }
   }

   public String getWord()
   {
      return word;
   }

   public String getPinyin()
   {
      return pinyin;
   }

   public String getDefinition()
   {
      return definition;
   }

}
