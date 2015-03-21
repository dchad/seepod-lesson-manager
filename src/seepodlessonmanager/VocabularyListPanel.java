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
 * Class  : VocabularyListPanel
 * 
 * Description: loads and display a vocab list, formats: CPOD(CSV, XML), Anki 0.9 and Pleco 2.0 XML.
 * 
 */

package seepodlessonmanager;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class VocabularyListPanel extends JPanel
{
   private Vector vocabItemList;
   private File vocabFile;
   private JList vocabList;
   private DefaultListModel vocabListModel;
   private BufferedReader inputStream;
   private int index;
   private VocabularyItemPanel vocabItemPanel;
   private Config seePodConfig;

   public VocabularyListPanel(Config spc, VocabularyItemPanel vip)
   {
      super(false);
      
      setBorder(BorderFactory.createTitledBorder("Vocabulary Items"));
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setPreferredSize(new Dimension(300,900));

      seePodConfig = spc;
      vocabItemPanel = vip;

      vocabListModel = new DefaultListModel();
      vocabList = new JList(vocabListModel);
      vocabItemList = new Vector();

      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1)
            {
               if (vocabListModel.size() > 0)
               {
                  index = vocabList.locationToIndex(e.getPoint());
                  //now tell the vocab panel to show the list
                  vocabItemPanel.loadVocabItem((VocabularyItem)vocabItemList.get(index));
               }
            }
          }
         
      };
      vocabList.addMouseListener(mouseListener);
      
      JButton flashCards = new JButton("Flashcards");
      flashCards.setSize(40, 30);
      //flashCards.setIcon(new javax.swing.ImageIcon(getClass().getResource("/seepodlessonmanager/images/folder_yellow.png"))); // NOI18N
      flashCards.setToolTipText("Open flashcards dialog...");
      flashCards.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            if (vocabItemList.size() > 0)
            {
               VocabularyFlashcardDialog vfd = new VocabularyFlashcardDialog(seePodConfig, vocabItemList);
               vfd.setVisible(true);
            }
            else
            {
               JOptionPane.showMessageDialog(seePodConfig.getAppFrame(), "No vocabulary list, select a vocab file in the file list.");
            }
         }
      });

      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
      buttonBox.add(flashCards);
      buttonBox.add(Box.createHorizontalGlue());            

      JScrollPane scroller = new JScrollPane();
      scroller.getViewport().add(vocabList);
      add(scroller);
      add(Box.createRigidArea(new Dimension(0,5)));
      add(buttonBox);
      add(Box.createRigidArea(new Dimension(0,5)));
   }

   public void loadVocabList(File vlist)
   {
      if (vlist.exists())
      {
         vocabFile = vlist;
         //check format then load it up
         String fileName = vlist.getName();
         if (fileName.endsWith(".xml"))
         {
             loadXML();
         }
         else if (fileName.endsWith(".txt"))
         {
             loadTXT();
         }
         else if (fileName.endsWith(".csv"))
         {
             loadCSV();
         }
         else
         {
            //popup an info dialog for unsupported format
            Debug.debug("VocabularyList() - Unsupported vocabulary format.");
         }
      }      
   }

   private void loadXML()
   {
      //Debug.debug("VocabularyList.loadXML() - loading vocab file: ", vocabFile.getName());
      try
      {
            try
            {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(vocabFile), "UTF-8"));
            } catch (FileNotFoundException ex) {
                Debug.debug("VocabularyList.loadXML() - file not found.", ex);
            }
      } catch (UnsupportedEncodingException ex) {
            Debug.debug("VocabularyList.loadXML() - unsupported encoding.", ex);
        return;
      }

      //read in the lesson list if any
      String entry;
      try
      {
         if ((entry = inputStream.readLine()) != null)
         {
            entry = inputStream.readLine();
            //Debug.debug("VocabularyList.loadXML() - root element line: ", entry);
            if (entry.contains("pleco"))
            {
                loadPlecoXML();
            }
            if (entry.contains("vocab"))
            {
                loadCPODXML();
            }
         }
      } catch (IOException ex) {
            Debug.debug("VocabularyList.loadXML() - Could not read line from buffered file reader.", ex);
         return;
      }

      if (inputStream != null)
      {
         try
         {
            inputStream.close();
         } catch (IOException ex) {
            Debug.debug("VocabularyList.loadXML() - Counld not close buffered file reader.", ex);
         }
      }
   }

   private void loadTXT() //Anki 0.9 format is tab seperated text lines
   {
      vocabItemList.removeAllElements();
      vocabListModel.removeAllElements();

      Debug.debug("VocabularyList.loadTXT() - loading Anki txt vocab file: ", vocabFile.getName());
      try
      {
            try
            {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(vocabFile), "UTF-8"));
            } catch (FileNotFoundException ex) {
                Debug.debug("VocabularyList.loadTXT() - file not found.", ex);
            }
      } catch (UnsupportedEncodingException ex) {
            Debug.debug("VocabularyList.loadTXT() - unsupported encoding.", ex);
        return;
      }

      //read in the lesson list if any
      String entry;
      try
      {
         while ((entry = inputStream.readLine()) != null)
         {
            //Debug.debug("VocabularyList.loadCSV() - read line: ", entry);
            String[] breakdown = entry.split("\t");
            //StringTokenizer st = new StringTokenizer(entry, ",");
            if (breakdown.length > 2)
            {
               String iword = breakdown[0];
               String idefn = breakdown[1];
               String ipinyin = breakdown[2];
               VocabularyItem vi = new VocabularyItem(iword, ipinyin, idefn);
               vocabItemList.add(vi);
               vocabListModel.addElement(iword); 
            }
         }
      } catch (IOException ex) {
            Debug.debug("VocabularyList.loadTXT() - Could not read line from buffered file reader.", ex);
         return;
      }

      if (inputStream != null)
      {
         try
         {
            inputStream.close();
         } catch (IOException ex) {
            Debug.debug("VocabularyList.loadTXT() - Counld not close buffered file reader.", ex);
         }
      }       
   }

   private void loadCSV()
   {
      vocabItemList.removeAllElements();
      vocabListModel.removeAllElements();

      //Debug.debug("VocabularyList.loadCSV() - loading vocab file: ", vocabFile.getName());
      try
      {
            try
            {
                inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(vocabFile), "UTF-8"));
            } catch (FileNotFoundException ex) {
                Debug.debug("VocabularyList.loadCSV() - file not found.", ex);
            }
      } catch (UnsupportedEncodingException ex) {
            Debug.debug("VocabularyList.loadCSV() - unsupported encoding.", ex);
        return;
      }

      //read in the lesson list if any
      String entry;
      try
      {
         while ((entry = inputStream.readLine()) != null)
         {
            //Debug.debug("VocabularyList.loadCSV() - read line: ", entry);
            String[] breakdown = entry.split(",");
            //StringTokenizer st = new StringTokenizer(entry, ",");
            if (breakdown.length > 3)
            {
               String iword = breakdown[0] + " : " + breakdown[1];
               iword = iword.replace('\"', ' ');
               String ipinyin = breakdown[2].replace('\"', ' ');
               String idefn = breakdown[3].replace('\"', ' ');
               VocabularyItem vi = new VocabularyItem(iword, ipinyin, idefn);
               vocabItemList.add(vi);
               vocabListModel.addElement(iword); 
            }
         }
      } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCSV() - Could not read line from buffered file reader.", ex);
         return;
      }

      if (inputStream != null)
      {
         try
         {
            inputStream.close();
         } catch (IOException ex) {
            Debug.debug("VocabularyList.loadCSV() - Counld not close buffered file reader.", ex);
         }
      }       
   }
   
   private void loadCPODXML()
   {
      SAXBuilder builder = new SAXBuilder();
      Document config;

      vocabItemList.removeAllElements();
      vocabListModel.removeAllElements();

      // Get the root element and iterate through the tree setting the lesson name and file path lists
      //Debug.debug("VocabularyList.loadCPODXML() - loading CPOD XML vocab file: ", vocabFile.getName());
      try 
      {

         config = builder.build(vocabFile);

         Element root = config.getRootElement();
 
         List nodeList = root.getChildren();

         Iterator iter = nodeList.iterator();
         while (iter.hasNext())
         {
            Element vocabItem = (Element) iter.next();
          
            if (vocabItem != null)
            {
                  Element word = vocabItem.getChild("zh_cn");
                  Element word_tr = vocabItem.getChild("zh_traditional");
                  Element pinyin = vocabItem.getChild("pinyin");
                  Element defn = vocabItem.getChild("en");
                  
                  String iword = "NULL";
                  String ipinyin = "NULL";
                  String idefn = "NULL";
                  if (word != null)
                  {
                      iword = word.getText() + " : " + word_tr.getText();
                  }
                  if (pinyin != null)
                  {
                      ipinyin = pinyin.getText();
                  }
                  if (defn != null)
                  {
                      idefn = defn.getText();
                  }
                  VocabularyItem vi = new VocabularyItem(iword, ipinyin, idefn);
                  vocabItemList.add(vi);
                  vocabListModel.addElement(iword);
            }
         }
         
      } catch (JDOMException ex) {
         Debug.debug("RssFeed.loadCPODXML - JDOM exception.");
      } catch (IOException ex) {
         Debug.debug("RssFeed.loadCPODXML - IO exception.");
      }
          
      return;       
   }
   
   private void loadPlecoXML()
   {
      SAXBuilder builder = new SAXBuilder();
      Document config;

      vocabItemList.removeAllElements();
      vocabListModel.removeAllElements();
      
      // Get the root element and iterate through the tree setting the lesson name and file path lists
      //Debug.debug("VocabularyList.loadPlecoXML() - loading Pleco vocab file: ", vocabFile.getName());
      try 
      {

         config = builder.build(vocabFile);

         Element root = config.getRootElement();
 
         Element tmpNode = root.getChild("cards");
         List nodeList = tmpNode.getChildren();

         Iterator iter = nodeList.iterator();
         while (iter.hasNext())
         {
            Element vocabItem = (Element) iter.next();
            if (vocabItem != null)
            {
               Element vitem = vocabItem.getChild("entry");
               if (vitem != null)
               {
                  Element word = vitem.getChild("headword");
                  Element pinyin = vitem.getChild("pron");
                  Element defn = vitem.getChild("defn");
                  
                  String iword = "NULL";
                  String ipinyin = "NULL";
                  String idefn = "NULL";
                  if (word != null)
                  {
                      iword = word.getText();
                  }
                  if (pinyin != null)
                  {
                      ipinyin = pinyin.getText();
                  }
                  if (defn != null)
                  {
                      idefn = defn.getText();
                  }
                  VocabularyItem vi = new VocabularyItem(iword, ipinyin, idefn);
                  vocabItemList.add(vi);
                  vocabListModel.addElement(iword);
               }
            }
         }
         
      } catch (JDOMException ex) {
         Debug.debug("RssFeed.loadPlecoXML - JDOM exception.");
      } catch (IOException ex) {
         Debug.debug("RssFeed.loadPlecoXML - IO exception.");
      }
          
      return;
   }
}
