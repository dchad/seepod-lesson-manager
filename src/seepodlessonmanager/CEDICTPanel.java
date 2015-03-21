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
 * Class  : CEDICTPanel
 * 
 * Description: Dictionary lookup panel.
 * 
 */

package seepodlessonmanager;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CEDICTPanel extends JPanel
{
   private Config seePodConfig;
   private CEDICTDictionary cedict;
   private JTextField lookupField;
   private JButton lookupButton;
   private JList resultList;
   private DefaultListModel entryListModel;
   private JScrollPane listScroller;
   private Vector cedictEntryList;   
   private JTextArea bigCharArea;
   private JTextArea pinyinNumberField;
   private JTextArea pinyinAccentField;
   private JTextArea entryDefinition;
   private int index;

   public CEDICTPanel(Config spc)
   {
      super(false);
      
      if (spc != null)
      {
         seePodConfig = spc;
      }
      else
      {
         Debug.debug("CEDICTPanel() - Config is null.");
      }
      
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      
      JPanel searchPanel = new JPanel(false);
      searchPanel.setBorder(BorderFactory.createTitledBorder("Dictionary Search"));
      searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
      //searchPanel.setPreferredSize(new Dimension(300,40));
      
      JLabel dirLabel = new JLabel("Text: ");
      searchPanel.add(dirLabel);
      searchPanel.add(Box.createHorizontalGlue());
      
      lookupField = new JTextField();
      lookupField.setMinimumSize(new Dimension(500,25));
      lookupField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
         searchDictionary();
      }
      });
      searchPanel.add(lookupField);
      searchPanel.add(Box.createHorizontalGlue());
      searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
      
      lookupButton = new JButton();
      lookupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/search.png"))); // NOI18N
      lookupButton.setToolTipText("Search the dictionary");
      
      lookupButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            searchDictionary();
         }
      });
      
      searchPanel.add(lookupButton);
      
      
      JPanel resultPanel = new JPanel(false);
      resultPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
      resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
      //searchPanel.setPreferredSize(new Dimension(300,40));      
      entryListModel = new DefaultListModel();
      resultList = new JList(entryListModel);
      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1)
            {
               if (entryListModel.size() > 0)
               {
                  index = resultList.locationToIndex(e.getPoint());
                  CEDICTEntry cedict = (CEDICTEntry) cedictEntryList.get(index);
                  bigCharArea.setText(cedict.getSimplifiedChars() + " : " + cedict.getTraditionalChars());
                  pinyinNumberField.setText(cedict.getPinYinNumbered());
                  pinyinAccentField.setText(cedict.getPinYinAccented());
                  entryDefinition.setText(cedict.getEnglishDefinitions());
               }
            }
          }
         
      };
      resultList.addMouseListener(mouseListener);

      //CEDICT definitionInfo
      Font infoFont = new Font("Dialog", Font.BOLD, 36);
      Font defnFont = new Font("Dialog", Font.BOLD, 16);
      Box infoBox = Box.createVerticalBox();
      infoBox.setPreferredSize(new Dimension(400,500));
      bigCharArea = new JTextArea(30,30);
      bigCharArea.setEditable(false);
      bigCharArea.setFont(infoFont);
      bigCharArea.setMaximumSize(new Dimension(400, 60));
      pinyinNumberField = new JTextArea(30,30);
      pinyinNumberField.setEditable(false);
      pinyinNumberField.setFont(defnFont);
      pinyinNumberField.setMaximumSize(new Dimension(400, 60));
      pinyinAccentField = new JTextArea(30,30);
      pinyinAccentField.setEditable(false);
      pinyinAccentField.setFont(defnFont);
      pinyinAccentField.setMaximumSize(new Dimension(400, 60));
      entryDefinition = new JTextArea(30,30);
      entryDefinition.setEditable(false);
      entryDefinition.setFont(defnFont);
      entryDefinition.setLineWrap(true);
      entryDefinition.setMaximumSize(new Dimension(400, 60));
      JScrollPane defnScroller = new JScrollPane();
      defnScroller.getViewport().add(entryDefinition);
      defnScroller.setMaximumSize(new Dimension(400, 180));
      defnScroller.setMinimumSize(new Dimension(400, 180));
      
      infoBox.add(bigCharArea);
      infoBox.add(Box.createRigidArea(new Dimension(0,5)));
      infoBox.add(Box.createVerticalGlue());
      infoBox.add(pinyinNumberField);
      infoBox.add(Box.createRigidArea(new Dimension(0,5)));
      infoBox.add(Box.createVerticalGlue());
      infoBox.add(pinyinAccentField);
      infoBox.add(Box.createRigidArea(new Dimension(0,5)));
      infoBox.add(Box.createVerticalGlue());
      infoBox.add(defnScroller);
      infoBox.add(Box.createRigidArea(new Dimension(0,5)));
      infoBox.add(Box.createHorizontalGlue());
            
      //resultList.setMinimumSize(new Dimension(750, 300));      
      listScroller = new JScrollPane();
      listScroller.getViewport().add(resultList);
      Box listBox = Box.createVerticalBox();
      listBox.setPreferredSize(new Dimension(2000, 300));
      //listBox.setMinimumSize(new Dimension(750, 300));
      listBox.add(listScroller);
      listBox.add(Box.createRigidArea(new Dimension(0,5)));

      resultPanel.add(Box.createRigidArea(new Dimension(5,0)));
      resultPanel.add(listBox);
      resultPanel.add(Box.createRigidArea(new Dimension(5,0)));
      resultPanel.add(infoBox);
      resultPanel.add(Box.createRigidArea(new Dimension(5,0)));
      
      add(searchPanel);
      add(Box.createRigidArea(new Dimension(0,5)));
      add(resultPanel);
      add(Box.createRigidArea(new Dimension(0,5)));

      cedict = new CEDICTDictionary(seePodConfig);
      cedictEntryList = new Vector();
   }
   
   private void searchDictionary()
   {
       cedictEntryList.removeAllElements();
       //resultList.removeAll();
       entryListModel.removeAllElements();
       
       String foundDefinition = null;
       String lookText = lookupField.getText();
       lookText = lookText.trim();
       //determine if traditional, simplified or english then call the appropriate dictionary method
       foundDefinition = cedict.findSimplifiedDefinitions(lookText);
       if (foundDefinition == null)
       {
          foundDefinition = cedict.findTraditionalDefinitions(lookText);
       }
       if (foundDefinition != null)
       {
          entryListModel.addElement(foundDefinition);
          CEDICTEntry ce = new CEDICTEntry(foundDefinition);
          cedictEntryList.add(ce);
       }
       else
       {          
          //do a lazy character search, use an ArrayList of CEDICTEntry's
          //Vector results = cedict.findCharacterDefinitions(lookText);
          Vector results = cedict.wordDefinitionSearch(lookText);
          int size = results.size();
          if (size > 0)
          {
             for (int i = 0; i < size; i++)
             {
                entryListModel.addElement(results.get(i));
                CEDICTEntry ce = new CEDICTEntry((String)results.get(i));
                cedictEntryList.add(ce);
                //Debug.debug("CEDICTPanel.searchDictionary() - entry pinyin: ",ce.getPinYinAccented());
             }
          }
          else
          {
             results = cedict.findCharacterDefinitions(lookText);
             size = results.size();
             if (size > 0)
             {
                for (int i = 0; i < size; i++)
                {
                   entryListModel.addElement(results.get(i));
                   CEDICTEntry ce = new CEDICTEntry((String)results.get(i));
                   cedictEntryList.add(ce);
                   //Debug.debug("CEDICTPanel.searchDictionary() - entry pinyin: ",ce.getPinYinAccented());
                }
             }
          }
       }
   }

}
