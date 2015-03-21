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
 * Class  : DirectoryTree 
 * 
 * Description: Uses a JTree to view directories and subdirectories selected by the user.
 * 
 * 
 */

package seepodlessonmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Collections;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public class DirectoryTree extends JPanel
{
   private String curDirectory;
   private JTree tree;
   private Config seePodConfig;
   private File selectedNode;
   private StatusPanel statusPanel;
   JScrollPane scrollpane;
   
  public DirectoryTree(Config spc, StatusPanel es) 
  {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Directory Tree"));
    
    if (spc != null)
    {
       seePodConfig = spc;
       curDirectory = seePodConfig.getLessonDirectory();
       if (curDirectory.equalsIgnoreCase("."))
       {
          curDirectory = seePodConfig.getHomeDirectory(); 
       }
    }
    else
    {
       curDirectory = ".";  //root = new File(System.getProperty("user.home"));
    }
    
    if (es != null)
    {
       statusPanel = es;
    }
     
    selectedNode = null;
    
  }
   @Override
  public Dimension getMinimumSize() 
   {
    return new Dimension(200, 400);
  }

   @Override
  public Dimension getPreferredSize() 
   {
    return new Dimension(400, 800);
  }

  public void setTopDirectory(String srcDir)
  {
     if (!srcDir.equalsIgnoreCase(curDirectory))
     {
        SPFile dir = new SPFile(srcDir);
        if (dir.exists())
        {
           if (Config.DEBUG)
              Debug.debug("DirectoryTree.setTopDirectory() - about to make tree. ");
           curDirectory = srcDir;
           TreeBuilder tb = new TreeBuilder(dir);
           tb.run();
        }
        else
        {
           if (Config.DEBUG)
              Debug.debug("DirectoryTree.setTopDirectory() - Directory does not exist: ", srcDir);
           statusPanel.printStatusLine("Invalid directory: " + srcDir);
        }
     }
     else
     {
        Debug.debug("DirectoryTree.setTopDirectory() - same directory: ", srcDir);
     }
  }
  
  public File getSelectedNode()
  {
     return selectedNode;
  }

   public void update()
   {
      if (curDirectory != null)
      {
         File dir = new File(curDirectory);
         if (dir.exists())
         {
            TreeBuilder tb = new TreeBuilder(dir);
            tb.run();
         }
         else
         {
            Debug.debug("DirectoryTree.update() - Directory does not exist.");
         }
      }
      else
      {
         Debug.debug("DirectoryTree.update() - Directory is null.");
      }
   }

   private void finishedTreeBuild()
   {
       scrollpane = new JScrollPane(tree);
       add(scrollpane);
       validate();
   }

   private class TreeBuilder implements Runnable
   {
      private File directory;
      private String startDirectory;

      public TreeBuilder (File dir)
      {
         if (dir != null)
         {
            directory = dir;
            if (!directory.exists())
            {
               Debug.debug("TreeBuilder() - Directory does not exist: ", directory.getName());
            }
            else
            {
               startDirectory = dir.getAbsolutePath();
            }
         }
         else
         {
            Debug.debug("TreeBuilder() - Directory is null.");
         }
      }
     private void makeDirectoryTree()
     {
        SPFile dir = new SPFile(startDirectory);

        if (tree != null)
        {
           removeAll();
           invalidate();
        }

       //Make a tree list with all the nodes, and make it a JTree
       tree = new JTree(addNodes(null, dir));

       String laf = seePodConfig.getUILookAndFeel();
       //if (laf.startsWith("Default") || laf.startsWith("Java") || laf.startsWith("Qua")) //fix the white font problem and icons caused by substance LaF
       if (laf.startsWith("Substance"))
       {
          SubstanceDefaultTreeCellRenderer renderer = new SubstanceDefaultTreeCellRenderer();
          tree.setCellRenderer(renderer);
          //Debug.debug("TreeBuilder.makeDirectoryTree() - Set Substance tree cell renderer.");
       }
       else
       {
          DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
          getIcons(renderer);
          tree.setCellRenderer(renderer);
          //Debug.debug("TreeBuilder.makeDirectoryTree() - Set default tree cell renderer.");
       }
       
       // Add a listener
       tree.addTreeSelectionListener(new TreeSelectionListener()
       {
         public void valueChanged(TreeSelectionEvent e)
         {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
           //Debug.debug("TreeBuilder.valueChanged() - selected " + node);

           SPFile sn = (SPFile) node.getUserObject();
           if (sn.isDirectory())
           {
              selectedNode = sn;
              //Debug.debug("TreeBuilder.valueChanged() selected - " + selectedNode.getAbsolutePath());
              FileList.listDirectory(selectedNode.getAbsolutePath());
           }
           else
           {
              FileList.clearList();
           }
         }
       });

       finishedTreeBuild(); //add tree to the JPanel
     }

     private DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, SPFile dir)
     {
       String curPath = dir.getPath();
        //String curPath = dir.getName();
       statusPanel.printStatusLine("Added directory: " + curPath);

       DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(dir);

       if (curTop != null)
       { // should only be null at root
         curTop.add(curDir);
       }

       Vector ol = new Vector();
       String[] tmp = dir.list();
       for (int i = 0; i < tmp.length; i++)
       {
         ol.addElement(tmp[i]);
       }

       Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);

       SPFile f;

       for (int i = 0; i < ol.size(); i++)
       {
         String thisObject = (String) ol.elementAt(i);
         String newPath;
         if (curPath.equals("."))
         {
           newPath = thisObject;
         }
         else
         {
           newPath = curPath + File.separator + thisObject;
         }
         if ((f = new SPFile(newPath)).isDirectory())
         {
           addNodes(curDir, f);
         }
       }

       return curDir;
     }


      public void run()
      {
         makeDirectoryTree();
      }

      private void getIcons(DefaultTreeCellRenderer rdr)
      {
          //Use substance tree renderer to fix node label rendering, but it does not support Icons!!!
          //Subclass and override setLeafIcon(), setOpenIcon() and setClosedIcon()?
          java.net.URL imageURL = DirectoryTree.class.getResource("/seepodlessonmanager/images/folder.png");

          if (imageURL != null)
          {
             ImageIcon leafIcon = new ImageIcon(imageURL, "MP3"); //replace the default leaf icon
             if (leafIcon != null)
             {
                rdr.setLeafIcon(leafIcon);
             }
             else
             {
                Debug.debug("TreeBuilder.makeDirectoryTree() - Failed to render leaf icon.");
             }
          }
          else
          {
             Debug.debug("TreeBuilder.makeDirectoryTree() - Failed to find icon resource image file.");
          }

          imageURL = DirectoryTree.class.getResource("/seepodlessonmanager/images/folder_red.png");

          if (imageURL != null)
          {
            ImageIcon nodeIcon = new ImageIcon(imageURL, "MP3"); //replace the default leaf icon
            if (nodeIcon != null)
            {
                //substance tree renderer does not implement open and closed icon!!!
                rdr.setOpenIcon(nodeIcon);
                rdr.setClosedIcon(nodeIcon);
                //renderer.setIcon(nodeIcon); setting label icon does not work either!!!
             }
             else
             {
                Debug.debug("TreeBuilder.makeDirectoryTree() - Failed to render leaf icon.");
             }
          }
          else
          {
             Debug.debug("TreeBuilder.makeDirectoryTree() - Failed to find icon resource image file.");
          }

      }
   }
  
}

