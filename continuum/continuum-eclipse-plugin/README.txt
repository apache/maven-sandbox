
0)  Overview
------------

This document briefly describes the notes to setup the plugin dev environment 
for Continuum-Eclipse-Plugin project.

1)  Setup
---------

1-1)  Run 'mvn eclipse:clean eclipse:eclipse osgi:osgi-bundle' on the checked 
      out Continuum-Eclipse-Plugin project. 

1-2)  Extract the 'continuum-eclipse-plugin-XXX.jar' created by the 
      'osgi:osgi-bundle' goal to a tmp directory.

1-3)  Manually copy the JARs that are extracted in the root folder to the 
      '<continuum-eclipse-plugin-project-root>/lib' folder. 


The required libs should now be available to Eclipse launcher. 


TODO: 
Hack the Eclipse plugin to be able to do the above steps automatically.


NOTE: 
Felix's Maven-OSGi plugin writes out 'Bundle-Classpath' header but this seems 
to be different in Eclipse, it is spelt as 'Bundle-ClassPath' (note how 
ClassPath is spelt)

