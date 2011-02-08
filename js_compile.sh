#!/bin/bash

# compile javascript files into Java classes

CLASSPATH="lib/js.jar:$(echo war/WEB-INF/lib/*.jar . | sed 's/ /:/g'):war/WEB-INF/classes:appengine-java-sdk/lib/shared/servlet-api.jar"
COMPILER="org.mozilla.javascript.tools.jsc.Main"
OPTIONS="-opt 9 -nosource -extends java.lang.Object"
OUTPUTDIR=war/WEB-INF/classes

mkdir -p $OUTPUTDIR

for src in $(find src/ -iname *.js)
do
    PACKAGE=$(echo $src | sed 's/src\///g' | sed 's/\/*\w*\.js$//g' | sed 's/\//\./g')
    if [ "$PACKAGE" = "" ]
        then
            PACKAGE="nopkg"
    fi
    java -cp $CLASSPATH $COMPILER $OPTIONS -package $PACKAGE -d $OUTPUTDIR $src
done
