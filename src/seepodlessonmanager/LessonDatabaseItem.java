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
 * Class  : LessonDatabaseItem
 * 
 * Description: ChinesePod lesson item.
 * 
 * 
 */

package seepodlessonmanager;

public class LessonDatabaseItem
{
    private String lessonNumber;
    private String lessonDate;
    private String lessonLevel;
    private String lessonTitle;
    private String displayString;
    private String lessonName;
    private int lessonStatus;
    
    public LessonDatabaseItem(String lnum, String ldate, String llevel, String ltitle, int lstatus)
    {
        lessonNumber = lnum;
        lessonDate = ldate;
        lessonLevel = llevel;
        lessonTitle = ltitle;
        lessonStatus = lstatus;
        displayString = "(" + lessonNumber + ") " + lessonTitle;
        //Debug.debug("LessonDatabaseItem() = lesson: ", displayString);
    }

    public LessonDatabaseItem(String lesson)
    {
       String[] breakdown = lesson.split("\t");
       //dbItem = tags.getTrackNumber() + "\t" + tags.getYear() + "\t" + level + "\t"+ tags.getSongTitle();
       if (breakdown.length > 3)
       {
           lessonNumber = breakdown[0];
           lessonDate = breakdown[1];
           lessonLevel = breakdown[2];
           lessonTitle = breakdown[3];
           displayString = "(" + lessonNumber + ") " + lessonTitle;
           lessonName = lessonTitle; //??????????
       }
    }
    
    public LessonDatabaseItem(String lnum, String ldate, String llevel, String ltitle)
    {
        lessonNumber = lnum;
        lessonDate = ldate;

        if (llevel.startsWith("new"))
        {
            lessonLevel = "Newbie";
        }
        else if (llevel.startsWith("ele"))
        {
            lessonLevel = "Elementary";
        }
        else if (llevel.startsWith("int"))
        {
            lessonLevel = "Intermediate";
        }
        else if (llevel.startsWith("upp"))
        {
            lessonLevel = "Upper-Intermediate";
        }
        else if (llevel.startsWith("adv"))
        {
            lessonLevel = "Advanced";
        }
        else if (llevel.startsWith("med"))
        {
            lessonLevel = "Media";
        }
        else
        {
            lessonLevel = llevel;
        }

        //lessonTitle = lessonLevel + " - " + ltitle;
        int index = ltitle.indexOf(" - ");

        if (index > 0)
        {
            lessonName = ltitle.substring(index + 3);
            lessonName = lessonName.toLowerCase();
            //lessonName = lessonName.replace("[^a-z]", "");
            if (!(lessonLevel.startsWith("Media") || lessonLevel.startsWith("Advanced")))
            {             
               lessonName = lessonName.replace(' ', '-');
               lessonName = lessonName.replaceAll("[^a-z0-9\\-]", "");
            }
            //Debug.debug("DatabaseItem() - lesson name: ", lessonName);
            
        }
        else
        {
            lessonName = ltitle;
        }
        lessonTitle = ltitle;

        displayString = "(" + lessonNumber + ") " + lessonTitle;
    }
    
    public String getLessonNumber()
    {
        return lessonNumber;
    }
    public String getLessonDate()
    {
        return lessonDate;
    }
    public String getLessonLevel()
    {
        return lessonLevel;
    }
    public String getLessonTitle()
    {
        return lessonTitle;
    }
    public String getLessonName()
    {
        return lessonName;
    }
    public void setLessonStatus(int lstatus)
    {
        lessonStatus = lstatus;
    }
    public int getLessonStatus()
    {
        return lessonStatus;
    }
    @Override
    public String toString()
    {
        return displayString;
    }
    

}
