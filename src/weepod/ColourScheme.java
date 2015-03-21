/*  Copyright 2009 Derek Chadwick
 
    This file is part of the WeePod WLCP GUI.

    WeePod WLCP GUI is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WeePod WLCP GUI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WeePod WLCP GUI.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Project: WeePod WLCP GUI
 * Author : Derek Chadwick
 * Date   : 1/2/2009
 * Class  : ColourSceme
 * 
 * Description: Sets the Look and Feel.
 * 
 * 
 */
/* FOR new substance LAF
name=SeePodColourScheme
kind=Light
colorUltraLight=#FFFFFF
colorExtraLight=#D7DDE0
colorLight=#B3BCC4
colorMid=#737373
colorDark=#4F4F4F
colorUltraDark=#7D0519
colorForeground=#030303
 */
 /*
  name=SeePodDarkColourScheme
kind=Dark
colorUltraLight=#FFFFFF
colorExtraLight=#D7DDE0
colorLight=#B3BCC4
colorMid=#737373
colorDark=#4F4F4F
colorUltraDark=#7D0519
colorForeground=#030303


*/
       //frame.setBackground(new Color(194,21,21));
       //frame.setForeground(new Color(194,21,21));

package weepod;

import java.awt.Font;
import java.util.Enumeration;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.fonts.FontPolicies;
import org.jvnet.substance.fonts.FontPolicy;
import org.jvnet.substance.fonts.FontSet;
import org.jvnet.substance.fonts.FontSets;
import org.jvnet.substance.skin.SubstanceAutumnLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;
import org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel;
import org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel;
import org.jvnet.substance.skin.SubstanceCremeLookAndFeel;
import org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel;
import org.jvnet.substance.skin.SubstanceMagmaLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel;
import org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel;
import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;
import org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel;
import org.jvnet.substance.skin.SubstanceNebulaLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel;
import org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel;
import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;
import org.jvnet.substance.skin.SubstanceRavenLookAndFeel;
import org.jvnet.substance.skin.SubstanceSaharaLookAndFeel;
import org.jvnet.substance.skinpack.SubstanceFieldOfWheatLookAndFeel;
import org.jvnet.substance.skinpack.SubstanceFindingNemoLookAndFeel;
import org.jvnet.substance.skinpack.SubstanceGreenMagicLookAndFeel;
import org.jvnet.substance.skinpack.SubstanceMangoLookAndFeel;
import org.jvnet.substance.skinpack.SubstanceStreetlightsLookAndFeel;

public class ColourScheme implements SPConstants
{
   private Config seePodConfig;
   private JFrame aFrame;
   private JFrame pdfFrame;
   private Font sysFont;
   
   public ColourScheme(Config spc)
   {
      if (spc == null)
      {
         Debug.debug("ColourScheme() - config is null.");
         return;        
      }
      else
      {
         seePodConfig = spc;
      }
      sysFont = getFont();
      aFrame = seePodConfig.getAppFrame();
   }
   
   private Font getFont() //fix substance LAF font policies, they cannot display UTF-8 characters properly!
   {
      Font systemDefaultFont = null;
      
      UIDefaults defaults = UIManager.getDefaults();
      Enumeration keys = defaults.keys();
      while(keys.hasMoreElements()) 
      {
         Object key = keys.nextElement();
         Object value = defaults.get(key);
         if(value != null && value instanceof Font) 
         {
            UIManager.put(key, null);
            systemDefaultFont = UIManager.getFont(key);
            if(systemDefaultFont != null) 
            {
               Debug.debug("System Default Font: ", systemDefaultFont.getFontName());
               break;
            }
         }
      }    
      return systemDefaultFont;
   }
   
   private void setSubstanceFontPolicy() //only call this after setting the new substance LAF, otherwise get a null pointer exception
   {
       FontSet fs = FontSets.createDefaultFontSet(sysFont);
       FontPolicy fp = FontPolicies.createFixedPolicy(fs);
       SubstanceLookAndFeel.setFontPolicy(fp);
   }

   public void testUILookAndFeel(ColourSchemeDialog csd)  //DEPRECATED: causes instability with the dialog still open
   {
      setUILookAndFeel();  
      aFrame = seePodConfig.getAppFrame();
      SwingUtilities.updateComponentTreeUI(aFrame);
      SwingUtilities.updateComponentTreeUI(csd);
      aFrame.pack();

   }
   
   public void resetUITree()
   {
      aFrame = seePodConfig.getAppFrame();
      SwingUtilities.updateComponentTreeUI(aFrame);
      //SwingUtilities.updateComponentTreeUI(pdfFrame);
      WeePod.resetUI();
      WeePod.weePodDownloadPanel.resetUI();
      //WeePod.seePodTagPanel.resetUI();
      //WeePod.seePodLessonPanel.resetUI();
      aFrame.pack();
      //pdfFrame.pack();
   }
   
   public void setUILookAndFeel()
   {
       String laf = seePodConfig.getUILookAndFeel();
       try 
       {
             // Set the system L&F

          if (Config.DEBUG)
             Debug.debug("ColourScheme.setUILookAndFeel() - setting LAF: ", laf);
          
          if (laf.startsWith(LAF_DEFAULT))
          {
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   
          }
          else if (laf.startsWith(LAF_JAVA))
          {
             UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());   //also has THEMES 
          }
          else if (laf.startsWith(LAF_NIMROD))
          {
             //NimRODLookAndFeel.java - getSupportsWindowDecorations() was false, modified to true to stop system default frame decorations!!!!
             UIManager.setLookAndFeel(new com.nilo.plaf.nimrod.NimRODLookAndFeel());
          }
          else if (laf.startsWith(LAF_LIPSTIK))
          {
             //modified lipstick themes default font to Dialog
             UIManager.setLookAndFeel(new com.lipstikLF.LipstikLookAndFeel());
          }
          else if (laf.startsWith(LAF_SQUARENESS))
          {
             UIManager.setLookAndFeel(new net.beeger.squareness.SquarenessLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_AERO))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.aero.AeroLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_ACRYL))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.acryl.AcrylLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_ALUMINIUM))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.aluminium.AluminiumLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_BERNSTEIN))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.bernstein.BernsteinLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_FAST))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.fast.FastLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_HIFI))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.hifi.HiFiLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_MCWIN))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.mcwin.McWinLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_MINT))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.mint.MintLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_NOIRE))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.noire.NoireLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_SMART))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.smart.SmartLookAndFeel());
          }
          else if (laf.startsWith(LAF_JTATTOO_LUNA))
          {
             UIManager.setLookAndFeel(new com.jtattoo.plaf.luna.LunaLookAndFeel());
          }
          else if (laf.startsWith(LAF_SUBSTANCE_AUTUMN))
          {
             UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_BUSINESSBLACKSTEEL))
          {
             UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_BUSINESSBLUESTEEL))
          {
             UIManager.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_BUSINESS))
          {
             UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_CHALLENGERDEEP))
          {
             UIManager.setLookAndFeel(new SubstanceChallengerDeepLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_CREMECOFFEE))
          {
             UIManager.setLookAndFeel(new SubstanceCremeCoffeeLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_CREME))
          {
             UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_EMERALDDUSK))
          {
             UIManager.setLookAndFeel(new SubstanceEmeraldDuskLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_MAGMA))
          {
             UIManager.setLookAndFeel(new SubstanceMagmaLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_MISTAQUA))
          {
             UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_MISTSILVER))
          {
             UIManager.setLookAndFeel(new SubstanceMistSilverLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_MODERATE))
          {
             UIManager.setLookAndFeel(new SubstanceModerateLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_NEBULABRICKWALL))
          {
             UIManager.setLookAndFeel(new SubstanceNebulaBrickWallLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_NEBULAR))
          {
             UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_OFFICEBLUE))
          {
             UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_OFFICESILVER))
          {
             UIManager.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_RAVENGRAPHITEGLASS))
          {
             UIManager.setLookAndFeel(new SubstanceRavenGraphiteGlassLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_RAVENGRAPHITE))
          {
             UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_RAVEN))
          {
             UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_SAHARA))
          {
             UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_FIELDOFWHEAT))
          {
             UIManager.setLookAndFeel(new SubstanceFieldOfWheatLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_FINDINGNEMO))
          {
             UIManager.setLookAndFeel(new SubstanceFindingNemoLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_GREENMAGIC))
          {
             UIManager.setLookAndFeel(new SubstanceGreenMagicLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_MANGO))
          {
             UIManager.setLookAndFeel(new SubstanceMangoLookAndFeel());
             setSubstanceFontPolicy();
          }
          else if (laf.startsWith(LAF_SUBSTANCE_STREETLIGHTS))
          {
             UIManager.setLookAndFeel(new SubstanceStreetlightsLookAndFeel());
             setSubstanceFontPolicy();
          }
          else
          {
             Debug.debug("ColourScheme.setUILookAndFeel() - Unknown look and feel.");
          }
          
       } 
       catch (UnsupportedLookAndFeelException e) {
          Debug.debug("ColourScheme.setUILookAndFeel() - Unsupported look and feel.");
       }    
       catch (ClassNotFoundException e) {
          Debug.debug("ColourScheme.setUILookAndFeel() - Class not found.");
       }
       catch (InstantiationException e) {
          Debug.debug("ColourScheme.setUILookAndFeel() - Instantiation exception.");
       }
       catch (IllegalAccessException e) {
          Debug.debug("ColourScheme.setUILookAndFeel() - Illegal access exception.");
       } 

       JFrame.setDefaultLookAndFeelDecorated(true);
       JDialog.setDefaultLookAndFeelDecorated(true); //make all windows look the same  
   }
}

