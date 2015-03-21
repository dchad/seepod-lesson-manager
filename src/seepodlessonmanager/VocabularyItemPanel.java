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
 * Class  : VocabularyItemPanel
 * 
 * Description: loads and display a vocab list, formats: CPOD(CSV, XML), Anki 0.9 and Pleco 2.0 XML.
 * 
 */


package seepodlessonmanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class VocabularyItemPanel extends JPanel
{
   private JTextArea hanziArea;
   private JTextArea pinyinArea;
   private JTextArea defnArea;
   private Font hanziFont;
   private Font defnFont;
   
   public VocabularyItemPanel()
   {
      super(false);
      
      setBorder(BorderFactory.createTitledBorder("Word Definition"));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(300,900));
      
      hanziFont = new Font("Dialog", Font.BOLD, 64);
      defnFont = new Font("Dialog", Font.BOLD, 18);
      
      hanziArea = new JTextArea(30,10);
      hanziArea.setFont(hanziFont);
      hanziArea.setEditable(false);
      hanziArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      pinyinArea = new JTextArea(30,10);
      pinyinArea.setFont(defnFont);
      pinyinArea.setEditable(false);
      defnArea = new JTextArea(30,10);
      defnArea.setFont(defnFont);
      defnArea.setEditable(false);

      JScrollPane scroller = new JScrollPane();
      scroller.getViewport().add(hanziArea);
      scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

      add(Box.createRigidArea(new Dimension(0,5)));
      add(scroller);
      add(Box.createRigidArea(new Dimension(0,5)));
      add(pinyinArea);
      add(Box.createRigidArea(new Dimension(0,5)));
      add(defnArea);
      add(Box.createRigidArea(new Dimension(0,5)));

   }

   public void loadVocabItem(VocabularyItem vi)
   {
       if (vi != null)
       {
           hanziArea.setText(vi.getWord());
           pinyinArea.setText(vi.getPinyin());
           defnArea.setText(vi.definition);
       }
   }
}
