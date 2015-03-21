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
 * Class  : SPConstants
 * 
 * Description: Global constant declarations.
 * 
 * 
 */


package seepodlessonmanager;


public interface SPConstants
{
      public static final int CPOD_NEWBIE = 1;
      public static final int CPOD_ELEMENTARY = 2;
      public static final int CPOD_INTERMEDIATE = 3;
      public static final int CPOD_UPPER_INTERMEDIATE = 4;
      public static final int CPOD_ADVANCED = 5;
      public static final int CPOD_MEDIA = 6;
      public static final int CPOD_QING_WEN = 7;
      public static final int CPOD_DEAR_AMBER = 8;
      public static final int CPOD_VIDEO = 9;

         // Text for JLabels etc
      public static final String CPOD_NEWBIE_STR = "Newbie";
      public static final String CPOD_ELEMENTARY_STR = "Elementary";
      public static final String CPOD_INTERMEDIATE_STR = "Intermediate";
      public static final String CPOD_UPPER_INTERMEDIATE_STR = "Upper-Intermediate";
      public static final String CPOD_ADVANCED_STR = "Advanced";
      public static final String CPOD_MEDIA_STR = "Media";
      public static final String CPOD_QING_WEN_STR = "Qing Wen";
      public static final String CPOD_DEAR_AMBER_STR = "Dear Amber";
      public static final String CPOD_VIDEO_STR = "Video";
      public static final String CPOD_NEWS_STR = "News and Features";
      public static final String CPOD_SHOW_STR = "Show";
      public static final String CPOD_DIALOGUE_STR = "Dialogue";
      public static final String CPOD_REVIEW_STR = "Review";
      public static final String CPOD_FIX_STR = "Fix";
      public static final String CPOD_EXPANSION_STR = "Expansion";
      public static final String CPOD_VOCABULARY_STR = "Vocabulary";
      public static final String CPOD_CHINESEPOD_STR = "ChinesePod.com";
      
      // Text for WLCP operations
      public static final String CPOD_URL = "http://www.chinesepod.com/";
      public static final String WIN_PYTHON25_PATH = "C:\\python25\\python.exe";  //default win path
      public static final String WIN_PYTHON26_PATH = "C:\\python26\\python.exe";
      public static final String WIN_PYTHON30_PATH = "C:\\python30\\python.exe";  //latest version
      public static final String LINUX_PYTHON_PATH = "/usr/bin/python";           //default linux path
      public static final String WLCP_LESSON_FILE = "lessons.txt";
      public static final String WLCP_PYTHON_SCRIPT = "welovechinesepod.py";
      public static final String WLCP_ALT_PYTHON_SCRIPT = "wlcpod.py";
      public static final String SP_LESSONS_FILE = "seepodlessons.xml";
      public static final String SP_RSS_FEED_FILE = "seepodrssfeed.html";
      public static final String SP_RSS_FEED_LIST = "cpodrsslist.xml";
      public static final String SP_LESSON_DATABASE_FILE = "seepodlessondatabase.xml";
      public static final String SP_CONFIG_FILE = "seepodconfig.xml";
      public static final String SP_LESSON_SETS_FILE = "seepodlessonsets.xml";
      public static final String SP_LAF_LIST_FILE = "seepodlookandfeel.xml";

      public static final String CEDICT_DICTIONARY_FILE = "cedict_ts.u8";

      public static final String DEFAULT_CHAR_ENCODING = "UTF-8";
      public static final String LINE_FEED = System.getProperty("line.separator");
      
      //public static final boolean DEBUG = true;
      
      // convenience index for drop down lists, collections etc
      public static final int ID3_ALBUM_NAME_KEY = 1;
      public static final int ID3_ARTIST_NAME_KEY = 2;
      public static final int ID3_GENRE_NAME_KEY = 3;
      public static final int ID3_YEAR_KEY = 4;
      public static final int ID3_COMMENT_KEY = 5;
      public static final int ID3_TRACK_NAME_KEY = 6;
      public static final int ID3_TRACK_NUMBER_KEY = 7;
      
      public static final int LESSON_MP3_FILE_KEY = 0;
      public static final int LESSON_REV_FILE_KEY = 1;
      public static final int LESSON_DIALOG_FILE_KEY = 2;
      public static final int LESSON_SIMP_PDF_FILE_KEY = 3;
      public static final int LESSON_WLCP_EXPANSION_FILE_KEY = 4;
      public static final int LESSON_WLCP_VOCAB_FILE_KEY = 5;
      public static final int LESSON_WLCP_LESSON_FILE_KEY = 6;
      public static final int LESSON_WLCP_DIALOG_FILE_KEY = 7;
      public static final int LESSON_WLCP_PDF_SIMP_KEY = 8;
      public static final int LESSON_WLCP_PDF_TRAD_KEY = 9;
      public static final int LESSON_WLCP_JPG_KEY = 10;
      public static final int LESSON_WLCP_REV_FILE_KEY = 11;
      public static final int LESSON_TRAD_PDF_FILE_KEY = 12;
             
      public static final int CPOD_LESSON_KEY = 1;
      public static final int WLCP_LESSON_KEY = 2;
      public static final int CSLPOD_LESSON_KEY = 3; //TODO
      public static final int POPUP_LESSON_KEY = 4;  //TODO
      public static final int VOA_LESSON_KEY = 5;    //TODO
      
      public static final String CPOD_LESSON_SUFFIX = "pb.mp3";
      public static final String CPOD_LESSON_CD_SUFFIX = "pr.mp3";
      public static final String CPOD_DIALOG_SUFFIX = "dg.mp3";
      public static final String CPOD_REVIEW_SUFFIX = "rv.mp3";
      public static final String CPOD_PDF_SUFFIX = ".pdf";
      public static final String WLCP_LESSON_SUFFIX = "lesson.mp3";
      public static final String WLCP_EXPANSION_SUFFIX = "expansion_sentences.mp3";
      public static final String WLCP_DIALOG_SUFFIX = "dialogue_sentences.mp3";
      public static final String WLCP_REVIEW_SUFFIX = "review.mp3";
      public static final String WLCP_VOCAB_SUFFIX = "vocabulary_sentences.mp3";
      public static final String WLCP_SIMPLIFIED_PDF_SUFFIX = "simp.pdf";
      public static final String WLCP_TRADITIONAL_PDF_SUFFIX = "trad.mp3";
      public static final String WLCP_JPG_SUFFIX = ".jpeg";
      
      public static final int LESSON_SAVE = 1;
      public static final int LESSON_OPEN = 2;
      
      public static final int LESSON_STATUS_NONE = 1;
      public static final int LESSON_STATUS_BOOKMARKED = 2;
      public static final int LESSON_STATUS_REVIEW = 3;
      public static final int LESSON_STATUS_COMPLETED = 4;

      public static final String LAF_DEFAULT = "Default";
      public static final String LAF_JAVA = "Java";
      public static final String LAF_QUAQUA = "QuaQua";
      public static final String LAF_SUBSTANCE_AUTUMN = "SubstanceAutumnSkin";
      public static final String LAF_SUBSTANCE_BUSINESSBLACKSTEEL = "SubstanceBusinessBlackSteelSkin";
      public static final String LAF_SUBSTANCE_BUSINESSBLUESTEEL = "SubstanceBusinessBlueSteelSkin";
      public static final String LAF_SUBSTANCE_BUSINESS = "SubstanceBusinessSkin";
      public static final String LAF_SUBSTANCE_CHALLENGERDEEP = "SubstanceChallengerDeepSkin";
      public static final String LAF_SUBSTANCE_CREMECOFFEE = "SubstanceCremeCoffeeSkin";
      public static final String LAF_SUBSTANCE_CREME = "SubstanceCremeSkin";
      public static final String LAF_SUBSTANCE_EMERALDDUSK = "SubstanceEmeraldDuskSkin";
      public static final String LAF_SUBSTANCE_MAGMA = "SubstanceMagmaSkin";
      public static final String LAF_SUBSTANCE_MISTAQUA = "SubstanceMistAquaSkin";
      public static final String LAF_SUBSTANCE_MISTSILVER = "SubstanceMistSilverSkin";
      public static final String LAF_SUBSTANCE_MODERATE = "SubstanceModerateSkin";
      public static final String LAF_SUBSTANCE_NEBULABRICKWALL = "SubstanceNebulaBrickWallSkin";
      public static final String LAF_SUBSTANCE_NEBULAR = "SubstanceNebulaSkin";
      public static final String LAF_SUBSTANCE_OFFICEBLUE = "SubstanceOfficeBlueSkin";
      public static final String LAF_SUBSTANCE_OFFICESILVER = "SubstanceOfficeSilverSkin";
      public static final String LAF_SUBSTANCE_RAVENGRAPHITEGLASS = "SubstanceRavenGraphiteGlassSkin";
      public static final String LAF_SUBSTANCE_RAVENGRAPHITE = "SubstanceRavenGraphiteSkin";
      public static final String LAF_SUBSTANCE_RAVEN = "SubstanceRavenSkin";
      public static final String LAF_SUBSTANCE_SAHARA = "SubstanceSaharaSkin";
      public static final String LAF_SUBSTANCE_FIELDOFWHEAT = "SubstanceFieldOfWheatSkin";
      public static final String LAF_SUBSTANCE_FINDINGNEMO = "SubstanceFindingNemoSkin";
      public static final String LAF_SUBSTANCE_GREENMAGIC = "SubstanceGreenMagicSkin";
      public static final String LAF_SUBSTANCE_MANGO = "SubstanceMangoSkin";
      public static final String LAF_SUBSTANCE_STREETLIGHTS = "SubstanceStreetLightsSkin";
      public static final String LAF_NIMROD = "NimrodSeePod";
      public static final String LAF_LIQUID = "Liquid";
      public static final String LAF_OYOAHA = "Oyoaha";
      public static final String LAF_TINY = "Tiny";
      public static final String LAF_JGOODIES = "JGoodies";
      public static final String LAF_SKIN = "Skin";
      public static final String LAF_SQUARENESS = "Squareness";
      public static final String LAF_LIPSTIK = "Lipstik";
      public static final String LAF_EASYNTH = "Easynth";
      public static final String LAF_JTATTOO_ACRYL = "JTattooAcryl";
      public static final String LAF_JTATTOO_AERO = "JTattooAero";
      public static final String LAF_JTATTOO_ALUMINIUM = "JTattooAluminium";
      public static final String LAF_JTATTOO_BERNSTEIN = "JTattooBernstein";
      public static final String LAF_JTATTOO_FAST = "JTattooFast";
      public static final String LAF_JTATTOO_HIFI = "JTattooHiFi";
      public static final String LAF_JTATTOO_MCWIN = "JTattooMcWin";
      public static final String LAF_JTATTOO_NOIRE = "JTattooNoire";
      public static final String LAF_JTATTOO_MINT = "JTattooMint";
      public static final String LAF_JTATTOO_SMART = "JTattooSmart";
      public static final String LAF_JTATTOO_LUNA = "JTattooLuna";
      public static final String LAF_EASYNTH_SEEPOD = "EasynthSeePod";
}