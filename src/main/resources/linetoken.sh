#!/bin/sh
#
#
#
# ./linetoken.sh -i '/data/ss' -o '/data/ss'  -silent -hold -statistic > statistik.txt
#

PARAM=""

while [ $# -ne 0 ]; do
    case $1 in
        -trim | -save | -empty | -intern | -statistic | -hold | -silent" )
                           ;;
        --input )          shift
                           PARAM="$PARAM -i"
                           ;;
        --output )         shift
                           PARAM="$PARAM -o"
                           ;;
        -gc                shift
                           GC=1
                           ;;
        -* )               shift
                           PARAM="$PARAM $1"
                           ;;
    esac
    PARAM="$PARAM '$1'"
    shift
done

echo $PARAM

HOME_LINETOKEN=~/linetoken
CURRENTDATE=`date +"%Y%m%d_%H%M%s"`
GC_LOG_FILE="$HOME_LINETOKEN/log/gc-linetoken-${CURRENTDATE}.log"
GC_LOG_OPTION="-verbosegc -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -Xloggc:$GC_LOG_FILE"
VM_OPTION="-server $GC_LOG_OPTION -XX:+UseParallelGC -XX:+UseParallelOldGC  -Xmx1g -Xms128m -XX:SurvivorRatio=6 "

java $VM_OPTION -jar $HOME_SQLCSV/linetoken.jar $PARAM
