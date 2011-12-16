#!/bin/sh


#build controller
cd CrawlerController/src/
javac -d ../bin  *.java
rmic -v1.2 -d ../bin -classpath ../bin SearchHandler

cd ../../CrawlerClient/src
javac -d ../bin -classpath ../../CrawlerController/bin/  *.java

cd ../bin
java -cp ../../CrawlerController/bin/ WebCrawlerServer http://www.utu.fi/ &
sleep 10
java -cp ../../CrawlerController/bin/:./  CrawlerClient &
