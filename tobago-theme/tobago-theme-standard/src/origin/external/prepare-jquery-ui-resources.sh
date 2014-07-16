#! /bin/bash

# Please call this script in its directory

# Click "Download" on this site http://jqueryui.com/download/#!version=1.10.4&components=1111100000001000100000000000000000
# Unpack the ZIP in a directory and set it here:
SOURCE=~/Downloads/jquery-ui-1.10.4.custom
TARGET=../../main/resources/org/apache/myfaces/tobago/renderkit/html/standard/standard
VERSION=1.10.4

# Scripts

cp ${SOURCE}/js/jquery-ui-${VERSION}.custom.js ${TARGET}/script/contrib
cp ${SOURCE}/js/jquery-ui-${VERSION}.custom.min.js ${TARGET}/script/contrib

for FILE in $(find ${SOURCE}/development-bundle/ui/i18n -type file -name "jquery.ui.datepicker-*.js") ; do
  # echo ${FILE};
  # e.g. jquery.ui.datepicker-zh-TW.js -> jquery-ui-datepicker-$VERSION_zh_TW.js
  LOCALE=`basename ${FILE} | sed "s|jquery.ui.datepicker-||g" | sed "s|.js||g"| sed "s|-|_|g"`
  NAME=jquery-ui-datepicker-i18n-${VERSION}_${LOCALE}.js
  cp ${FILE} ${TARGET}/script/contrib/${NAME}
done

for FILE in $(find ${SOURCE}/development-bundle/ui/minified/i18n -type file -name "jquery.ui.datepicker-*.min.js") ; do
  # echo ${FILE};
  # e.g. jquery.ui.datepicker-zh-TW.js -> jquery-ui-datepicker-$VERSION_zh_TW.js
  LOCALE=`basename ${FILE} | sed "s|jquery.ui.datepicker-||g" | sed "s|.min.js||g"| sed "s|-|_|g"`
  NAME=jquery-ui-datepicker-i18n-${VERSION}_${LOCALE}.min.js
  cp ${FILE} ${TARGET}/script/contrib/${NAME}
done

# Styles

cp -r ${SOURCE}/css/ui-lightness ${TARGET}/style/contrib

# Check the svn diff now!