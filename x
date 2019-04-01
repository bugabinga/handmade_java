# Settings intended to make startup of 'java build.build' faster
# It is purely optional and still under investigation, whether wall time is improved.
# To use these settings, include this parameter file like so: 'java @x build.java'

# Turn off validation of class file format.
-Xverify:none

# Set minimum and maximum heap in order to prevent to many round trips with OS requesting memory.
-Xmx128m
-Xms128m

# Turn off garbage collection. GC is probalby unnecessary for one-off scripts.
-XX:+UnlockExperimentalVMOptions
-XX:+UseEpsilonGC
