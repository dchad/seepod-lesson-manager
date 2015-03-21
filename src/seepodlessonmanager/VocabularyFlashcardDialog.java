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
 * Date   : 1/5/2009
 * Class  : VocabularyFlashcardDialog
 * 
 * Description: JDialog to display flashcards for vocabulary lists.
 * 
 * 
 */

package seepodlessonmanager;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class VocabularyFlashcardDialog extends JDialog
{
   private Config seePodConfig;
   private JPanel contentPanel;
   private Container cp;
   private JLabel bigCharArea;
   private JTextField defn;
   private JTextField pinyin;
   private Font hanziFont;
   private Font defnFont;
   private Font smallFont;
   private Font mediumFont;
   private Font largeFont;
   //private Vector vocabList;
   private VocabularyItem currentVitem;
   private int index;
   private ArrayList vocabList;
   private JRadioButton smallFontButton;
   private JRadioButton mediumFontButton;
   private JRadioButton largeFontButton;
   private int width;
   private int height;
   private Dimension screen;
   
   public VocabularyFlashcardDialog(Config spc, Vector vList)
   {
      super(spc.getAppFrame(), "SeePod Flashcards", false);
      
      width = 800;
      height = 600;
      
      screen = Toolkit.getDefaultToolkit().getScreenSize();
            
      int x = (screen.width - width) / 2;
      int y = (screen.height - height) / 2;
      
      setBounds(x, y, width, height);

      seePodConfig = spc;
      
      cp = getContentPane();
     
      setSize(800,600);
     
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(1000,800));
     
      hanziFont = new Font("Dialog", Font.BOLD, 128);
      //hanziFont.
      largeFont = new Font("Dialog", Font.BOLD, 128);
      mediumFont = new Font("Dialog", Font.BOLD, 64);
      smallFont = new Font("Dialog", Font.BOLD, 32);
      defnFont = new Font("Dialog", Font.BOLD, 18);

      smallFontButton = new JRadioButton("Small Font");
      smallFontButton.setActionCommand("Small");
      //smallFontButton.setToolTipText("Ignore all sentence files (rec-*.mp3) in lesson directories.");
      
      mediumFontButton = new JRadioButton("Medium Font");
      mediumFontButton.setActionCommand("Medium");
      //mediumFontButton.setToolTipText("Delete all sentence files (rec-*.mp3).");
      
      largeFontButton = new JRadioButton("Large Font");
      largeFontButton.setActionCommand("Large");
      largeFontButton.setSelected(true);
      //largeFontButton.setToolTipText("Place all sentence files (rec-*.mp3) in a ZIP archive.");
      
      ButtonGroup bGroup = new ButtonGroup();
      
      bGroup.add(smallFontButton);
      bGroup.add(mediumFontButton);
      bGroup.add(largeFontButton);

      smallFontButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
               radioButtonActionPerformed(evt);
         }
      });
      mediumFontButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
               radioButtonActionPerformed(evt);  
         }
      });
      largeFontButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
               radioButtonActionPerformed(evt);
         }
      });
      
      Box radioButtonBox = Box.createHorizontalBox();
      Box smallBox = Box.createHorizontalBox();
      smallBox.add(smallFontButton);
      smallBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(smallBox);
      Box mediumBox = Box.createHorizontalBox();
      mediumBox.add(mediumFontButton);
      mediumBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(mediumBox);
      Box largeBox = Box.createHorizontalBox();
      largeBox.add(largeFontButton);
      largeBox.add(Box.createHorizontalGlue());
      radioButtonBox.add(largeBox);
      radioButtonBox.add(Box.createHorizontalGlue());
      
      Box topBox = Box.createVerticalBox();
      topBox.add(radioButtonBox);
      
      bigCharArea = new JLabel();
      bigCharArea.setFont(largeFont);
      bigCharArea.setVerticalAlignment(SwingConstants.CENTER);
      bigCharArea.setHorizontalAlignment(SwingConstants.CENTER);
      //bigCharArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      //bigCharArea.setA

      JScrollPane bigCharScroller = new JScrollPane(bigCharArea);
      bigCharScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      bigCharScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
      
      Box infoBox = Box.createHorizontalBox();
      JLabel enLabel = new JLabel("English: ");
      defn = new JTextField(30);
      defn.setFont(defnFont);
      defn.setMaximumSize(new Dimension(500, 60));
      //defn.setMinimumSize(new Dimension(500, 50));
      //defn.setPreferredSize(new Dimension(500, 50));
      JButton enButton = new JButton();
      //enButton.setSize(20, 20);
      enButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/info.png")));
      enButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
               defn.setText(currentVitem.getDefinition());
         }
      });      
     
      JLabel pyLabel = new JLabel("Pinyin: ");
      pinyin = new JTextField(30);
      pinyin.setFont(defnFont);
      pinyin.setMaximumSize(new Dimension(500, 60));
      //pinyin.setMinimumSize(new Dimension(500, 50));
      //pinyin.setPreferredSize(new Dimension(500, 50));
      JButton pyButton = new JButton();
      //pyButton.setSize(20, 20);
      pyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/info.png"))); 
      pyButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
               pinyin.setText(currentVitem.getPinyin());
         }
      });

      infoBox.add(pyLabel);
      infoBox.add(pinyin);
      infoBox.add(Box.createRigidArea(new Dimension(5,0)));
      infoBox.add(pyButton);
      infoBox.add(Box.createRigidArea(new Dimension(10,0)));
      infoBox.add(enLabel);
      infoBox.add(defn);
      infoBox.add(Box.createRigidArea(new Dimension(5,0)));
      infoBox.add(enButton);

      topBox.add(bigCharScroller);
      topBox.add(Box.createRigidArea(new Dimension(0,5)));
      topBox.add(infoBox);
      topBox.add(Box.createRigidArea(new Dimension(0,5)));
      
      Box buttonBox = Box.createHorizontalBox();
      JButton prevButton = new JButton("Prev");
      prevButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            if (index > 0)
            {
               index--;
               currentVitem = (VocabularyItem) vocabList.get(index);
               bigCharArea.setText(currentVitem.getWord());
            }
         }
      });
      JButton shuffleButton = new JButton("Shuffle");
      shuffleButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            Collections.shuffle(vocabList);
            currentVitem = (VocabularyItem) vocabList.get(0);
            bigCharArea.setText(currentVitem.getWord());
         }
      });      
      JButton nextButton = new JButton("Next");
      nextButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            if (index < (vocabList.size() - 1))
            {
               index++;
               currentVitem = (VocabularyItem) vocabList.get(index);
               bigCharArea.setText(currentVitem.getWord());
            }
            else
            {
               JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "Last item in list.");
            }
         }
      });
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            hideDialog();
         }
      });
  
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(prevButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(shuffleButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(nextButton);
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(closeButton);
      buttonBox.add(Box.createHorizontalGlue());

      contentPanel.add(topBox);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      //contentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
      contentPanel.add(buttonBox);
      contentPanel.add(Box.createRigidArea(new Dimension(0,10)));
      
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      cp.add(contentPanel);

      vocabList = new ArrayList<String>();
      vocabList.addAll(vList);
      currentVitem = (VocabularyItem) vocabList.get(0);
      bigCharArea.setText(currentVitem.getWord());
      index = 0;
   }
   
   private void radioButtonActionPerformed(java.awt.event.ActionEvent evt)
   {
      // not used
      String action = evt.getActionCommand();
      if (action.startsWith("Small"))
      {
         //update config, only need to react to a set action not the deselect action for a button group, Config does the rest.
         if (smallFontButton.isSelected())
         {
            bigCharArea.setFont(smallFont);
         }
      }
      else if (action.startsWith("Medium"))
      {
         if (mediumFontButton.isSelected())
         {
            bigCharArea.setFont(mediumFont);
         }
      }
      else
      {
         if (largeFontButton.isSelected())
         {
            bigCharArea.setFont(largeFont);
         }
      }
   }

   private void hideDialog()
   {
       this.setVisible(false);
   }
}
