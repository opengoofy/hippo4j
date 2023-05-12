cd `dirname $0`/../target
target_dir=`pwd`

pid=`ps ax | grep -i 'hippo4j.hippo4j' | grep ${target_dir} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No hippo4jServer running."
        exit -1;
fi

echo "The hippo4jServer(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to hippo4jServer(${pid}) OK"