#!/bin/bash

if [ $# -lt 2 ] ; then

    which xpath &>/dev/null
    if [ $? -eq 0 ] ; then
        CURRENT_V=`xpath -q -e '/project/version/text()' pom.xml | perl -pwe 's#-SNAPSHOT##'`
        NEXT_V=`echo ${CURRENT_V} |  perl -nwe 'm#^(.+?\..+?\.)(.+)#; my $pr=$1; $s = eval($2+1); print "$pr$s"'`
    else
        echo "This script requires the 'xpath' executable to be installed (sudo apt install libxml-xpath-perl)."
        exit -2
    fi
    echo "Usage: ${0} <release_version> <development_version>"
    echo -e "Example: \n${0} ${CURRENT_V:-1.0.0} ${NEXT_V:-1.0.1}-SNAPSHOT"
    exit -1
fi

RELEASE=${1}
NEXT=${2}

mvn release:clean release:prepare release:perform -Darguments="-DskipTests -Dmaven.javadoc.skip=true" -Dtag=release-${RELEASE} -DreleaseVersion=${RELEASE} -DdevelopmentVersion=${NEXT}
