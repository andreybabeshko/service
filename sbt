#!/bin/sh
#rem DEBUG=-Djava.compiler=NONE -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=7777
java $DEBUG $JREBEL -XX:MaxPermSize=1024m -Xmx2024M -Xss4M -jar sbt-launch-0.13.9.jar $*
