# seepod-lesson-manager
Automatically exported from code.google.com/p/seepod-lesson-manager

SeePod Lesson Manager is an open source Java application designed to manage chinesepod.com lessons. The application includes an MP3 player, PDF reader, Chinese-English dictionary and progress tracking functions.

# 1. Quick Start Guide

Installation:

The application packages, source code and user guides are all available on the downloads page. SeePod has a ZIP package for all platforms and a Windows installer package. WeePod has platform specific packages for Windows, Linux and Mac OSX. Download the required package and unzip into any directory, or run the Windows installer. SeePod and WeePod do not set or modify any operating system data, so there is no requirement for an uninstaller. You can remove the application by manually deleting the application files or its home directory.

Requirements:

- SeePod and WeePod are Java applications and require the Sun Java Runtime Environment (JRE) version 6, also referred to as version 1.6. The JRE is available free for all major platforms and can be downloaded from: http://java.sun.com/javase/downloads/index.jsp

- The WLCP GUI can use any version of the WLCP python script, but the auto-tagging functions work best with newer versions. The latest version - 2009_09_12 of the WLCP script can be downloaded from http://code.google.com/p/wlcp.

- The WLCP script requires Python 2.5. Python can be downloaded from: http://www.python.org If you do not wish to use WLCP then you can ignore this requirement.

Configuration:

Start SeePod by double clicking on SeePod_Lesson_Manager.jar or use the command line:

java -jar SeePod_Lesson_Manager.jar

Start WeePod by double clicking on WeePod.jar or use the command line:

java -jar WeePod.jar

After SeePod or WeePod has launched go to the main menu item “Options”, open the "Settings" dialog and set the values for your main lesson directory, WLCP directory and Python directory. For faster configuration place the SeePod or WeePod application files in your WLCP directory and the application will automatically detect the script. SeePod/WeePod will also use its home directory as the default starting point for lesson scanning.

On startup SeePod/WeePod will also attempt to detect the location of the Python application. If the application cannot find Python it will set the Python path to “Unknown”. If this occurs you will have to enter the Python path manually, for example, on Windows: c:\python25\python.exe or on Linux: /usr/bin/python

For more detailed instructions read the the relevant user guides available on the downloads page.

# 2. Common Problems

If SeePod/WeePod does not start by clicking on the JAR file, then try running the application from the command line with the command:

java -jar SeePod_Lesson_Manager.jar

or

java -jar WeePod.jar

If you get an error message similar to the following, then your Java Runtime Environment needs to be updated to version 1.6 or later. See the above requirements section for the Java download site.

Exception in thread \"main\" java.lang.UnsupportedClassVersionError?: Bad version number in .class file

at java.lang.ClassLoader?.defineClass2(Native Method) at java.lang.ClassLoader?.defineClass(ClassLoader?.java:774) at java.security.SecureClassLoader?.defineClass(SecureClassLoader?.java:160) at java.net.URLClassLoader.defineClass(URLClassLoader.java:254) at java.net.URLClassLoader.access$100(URLClassLoader.java:56) at java.net.URLClassLoader$1.run(URLClassLoader.java:195) at java.security.AccessController?.doPrivileged(Native Method) at java.net.URLClassLoader.findClass(URLClassLoader.java:188) at java.lang.ClassLoader?.loadClass(ClassLoader?.java:316) at sun.misc.Launcher$AppClassLoader?.loadClass(Launcher.java:280) at java.lang.ClassLoader?.loadClass(ClassLoader?.java:251) at java.lang.ClassLoader?.loadClassInternal(ClassLoader?.java:374) 

# 3. Screenshots

image 1

image 2

(Thanks Google, FU)

