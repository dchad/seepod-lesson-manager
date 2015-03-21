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
 * Class  : ColourSchemeDialog
 * 
 * Description: Dialogue to set the Look And Feel
 * 
 * 
 */


package seepodlessonmanager;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ColourSchemeDialog extends JDialog
{
   private Config seePodConfig;
   private Container cp;
   private JPanel contentPanel;
   private JScrollPane scroller;
   private JList lafList;
   private DefaultListModel lafListModel;
   private JTextField lafDescription;
   private Vector lafDescriptionList;
   private File lafFile;
   private int index;
   private ColourScheme landf;
   
   public ColourSchemeDialog(JFrame frame, ColourScheme laf, Config spc)
   {
      super(frame, "Colour Scheme Dialogue", false);
      //setTitle();
      
      if (spc == null)
      {
         Debug.debug("ColourSchemeDialog() - config is null.");
         return;        
      }
      else
      {
         seePodConfig = spc;
      }
      
      if (laf == null)
      {
         Debug.debug("ColourSchemeDialog() - laf is null.");
         return;        
      }
      else
      {
         landf = laf;
      }
      
      cp = getContentPane();
      cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
      cp.setSize(400,600);
     
      contentPanel = new JPanel();
      contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
      contentPanel.setPreferredSize(new Dimension(400,500));      
      contentPanel.setBorder(BorderFactory.createTitledBorder("Look And Feel List"));
      
      lafListModel = new DefaultListModel();
      lafList = new JList(lafListModel);
      scroller = new JScrollPane(lafList);
      
      contentPanel.add(scroller);
      
      lafDescription = new JTextField();
      lafDescription.setMaximumSize(new Dimension(400,30));
      //lafDescription.setEditable(false);
      MouseListener mouseListener = new MouseAdapter() //add lesson to the download list
      {     
         @Override
         public void mouseClicked(MouseEvent e) 
         {
            if (e.getClickCount() == 1) //|| (e.getClickCount() == 2))
            {
               if (lafListModel.size() > 0)
               {
                  index = lafList.locationToIndex(e.getPoint());
                  String lafd = (String) lafDescriptionList.get(index);
                  lafDescription.setText(lafd);
               }
            }
          }
      };
      lafList.addMouseListener(mouseListener);
      contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
      contentPanel.add(lafDescription);
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(Box.createHorizontalGlue());
            
      JButton saveButton = new JButton("Save");
      saveButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            String lafkey = (String) lafListModel.get(index);
            setVisible(false);
            seePodConfig.setUILookAndFeel(lafkey);
            seePodConfig.saveConfigFile();
            landf.setUILookAndFeel();
            landf.resetUITree();
         }
      });
      buttonBox.add(saveButton);
      buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
      
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new java.awt.event.ActionListener() 
      {
         public void actionPerformed(java.awt.event.ActionEvent evt) 
         {
            setVisible(false);
         }
      });
      buttonBox.add(closeButton);
      buttonBox.add(Box.createRigidArea(new Dimension(15,0)));
      
      
      cp.add(contentPanel);
      cp.add(Box.createRigidArea(new Dimension(0,10)));
      cp.add(buttonBox);
      cp.add(Box.createRigidArea(new Dimension(0,10)));
      
      index = 0;
      
      getLAFList();
      pack();
      
   }
   
   public void showDialog()
   {
      setVisible(true);
   }
   
   private void getLAFList()
   {
      lafDescriptionList = new Vector();
      lafFile = new File(seePodConfig.getHomeDirectory() + File.separator + Config.SP_LAF_LIST_FILE);
      if (!lafFile.exists())
      {
         setDefaultLAFList();
         saveLAFFile();
      }
      else
      {
         loadLAFFile();
      }
   }
   
   private void setDefaultLAFList()
   {
      lafDescriptionList.add("System default look and feel.");
      lafListModel.addElement(Config.LAF_DEFAULT);
      lafDescriptionList.add("Java \"Metal\" look and feel.");
      lafListModel.addElement(Config.LAF_JAVA);
      lafDescriptionList.add("Substance Autumn look and feel");
      lafListModel.addElement(Config.LAF_SUBSTANCE_AUTUMN);
   }
   
   
   private void loadLAFFile()
   {
      
      SAXBuilder builder = new SAXBuilder();
      Document lafDoc;

      // Get the root element and iterate through the tree setting the lesson name and file path lists
      
      lafDescriptionList.removeAllElements();  //clear the lists
      lafListModel.removeAllElements();
      
      try 
      {
         lafDoc = builder.build(lafFile);
         Element root = lafDoc.getRootElement();
         
         List nodeList = root.getChildren();
         int len = nodeList.size();
 
         for (int i = 0; i < len; i++)
         {
            Element tmpNode = root.getChild("LAF"+i);
            Element lafKeyNode = tmpNode.getChild("LAFKey");
            String lafKeyString = lafKeyNode.getText();
            Element lafDescNode = tmpNode.getChild("LAFDescription");
            String lafDescString = lafDescNode.getText();
            lafDescriptionList.add(lafDescString);                    
            lafListModel.addElement(lafKeyString); 
            //Debug.debug("ColourSchemeDialog.loadLAFFile() - loaded LAF: ", lafDescString);
         }
         
         
      } catch (JDOMException ex) {
         Debug.debug("ColourSchemeDialog.loadLAFFile() - JDOM exceptino reading look and feel file.");
      } catch (IOException ex) {
         Debug.debug("ColourSchemeDialog.loadLAFFile() - IO exception reading look and feel file.");
      }
          
      return;
   }
   
   private void saveLAFFile()
   {
      
      Element root = new Element("SeePodLookAndFeelList");
      Document newLAFList = new Document(root); 
         
      for (int i = 0; i < lafListModel.size(); i++)
      {
          Element tmpNode = new Element("LAF"+i);
          Element lafKeyNode = new Element("LAFKey");
          Element lafDescriptionNode = new Element("LAFDescription");
          String lafKeyString = (String) lafListModel.get(i);
          lafKeyNode.setText(lafKeyString);
          String lafDescString = (String) lafDescriptionList.get(i);
          lafDescriptionNode.setText(lafDescString);
          tmpNode.addContent(lafKeyNode);
          tmpNode.addContent(lafDescriptionNode);
          root.addContent(tmpNode);
         
      }
      
      try 
      {
          PrintWriter wout = new PrintWriter(lafFile, "UTF-8"); //line output
          XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat()); //pretty format!!!!
          serializer.output(newLAFList, wout);
          wout.close();
        
      }
      catch (IOException e) {
          if (Config.DEBUG)
             Debug.debug("ColourSchemeDialog.saveLAFFile() - IO exception writing look and feel file.");
      }
      
      return;
   }
}