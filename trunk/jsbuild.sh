#!/bin/bash

# TODO: automate this process with ant

java -cp rhino1_7R2/js.jar:$(echo war/WEB-INF/lib/*.jar . | sed 's/ /:/g'):war/WEB-INF/classes:appengine-java-sdk/lib/shared/servlet-api.jar org.mozilla.javascript.tools.jsc.Main -extends javax.servlet.http.HttpServlet -nosource -package com.tnic.test src/com/tnic/test/A.js
# ; mkdir -p war/WEB-INF/classes/com/tnic/test/ ; mv src/com/tnic/test/com/tnic/test/Test.class war/WEB-INF/classes/com/tnic/test/ ; rm -rf src/com/tnic/test/com/
#echo war/WEB-INF/lib/*.jar . | sed 's/ /:/g'

