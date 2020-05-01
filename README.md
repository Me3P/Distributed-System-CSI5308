# Distributed Computing Systems Project

First folder ```Experimental Analysis\``` contains visualization and ```visualizing\``` folder contains analysis

### Visualizing 

input file gets a graph
    in first-line gets the number of nodes(n) and number edges(m)
    next m lines are edges from the first node to the second node 
    Note: I assume that nodes are between [0,n-1]
then by running run.bat, you will see the visualization

An edge becomes blue when in yo-down a source node sends a message to its target.
An edge becomes red when in yo-up a source node sends no-message to its target.
An edge becomes green when in yo-up a source node sends yes-message to its target.
### set up
If you want to change the java code manually you should either download graphstream packaged and add them to your CLASSPATH or you can use maven. you can find the tutorial in their web site.<a href='http://graphstream-project.org/'> Graph Stream </a>

For analysis, open the analysis folder, it contains one HTML file which contains python code besides, comments and figures as well.
