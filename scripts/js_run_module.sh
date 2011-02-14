#!/bin/bash

CLASSPATH="lib/js.jar:$(echo war/WEB-INF/lib/*.jar . | sed 's/ /:/g'):war/WEB-INF/classes:appengine-java-sdk/lib/shared/servlet-api.jar"
FILE="src/$(echo $1 | sed 's/\./\//g').js"

JS="load('$FILE');print(run());quit();"

java -cp $CLASSPATH org.mozilla.javascript.tools.shell.Main -e $JS
