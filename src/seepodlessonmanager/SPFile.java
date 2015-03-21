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
 * Class  : SPFile
 *
 * Description: File wrapper class to override toString()
 *
 *
 */


package seepodlessonmanager;

import java.io.File;

/**
 *
 * @author derek
 */
  class SPFile extends File //mini wrapper so the tree will not display the canonical path in every directory node!
  {
     public SPFile(String filePath)
     {
        super(filePath);
     }

     @Override
     public String toString()
     {
        return getName(); //File.toString() returns getPath(), this looks awful in large directory trees
     }
  }