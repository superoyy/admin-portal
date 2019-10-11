#!/usr/bin/env bash
# Description:
#       Timanetworks Spring Cloud Boot Scripts.
#   @Date:    2017-11-23
#   @Version: 1.0
# History:
# 2017-12-13, modify scripts, such as log file,remove lib/bin etc..
# 2018-01-31, modify scripts, move check enviroment variable position, from 'init_env' to 'init_script'.
# Get base path
BASEDIR=$(cd $(dirname $0); pwd -P)
SCRIPTNAME=$(basename $0)
LIB_HOME=$BASEDIR

#define functions
#help info
Usage(){
    echo Usage:
    echo "$0 start|stop|restart|status"
    # echo "Extended Parameters:$Extended_Parameters"
}

# @info: init app opts
# @args：(none)
# @deps: init_env
# @return: (none)
app_opts() {
    [[ -z ${PROFILE} ]] && PROFILE=dev
    [[ -z ${LOG_LEVEL} ]] && LOG_LEVEL=debug
    [[ -z ${APPNAME} ]] && echo "appname must be set" && exit 1
    [[ -z ${EUREKA_SERVER} ]] && echo "eureka server must be set" && exit 1
    [[ -z ${APP_OPTS} ]] && echo "boot args must be set" && exit 1
}

# @info: init script
# @args：(none)
# @deps: (none)
# @return: (none)
init_script() {
    [[ ! -z ${ENV_HOME} && -f "${ENV_HOME}"/env.sh ]] && source "${ENV_HOME}"/env.sh
    [[ -f "${BASEDIR}"/env.sh ]] && source "${BASEDIR}"/env.sh
    if [[ -f ${BASEDIR}/run.conf ]]; then
        source ${BASEDIR}/run.conf
    else
        echo "no such file of directory ${BASEDIR}/run.conf"
        exit 1
    fi
}

# @info: init env
# @args：(none)
# @deps: (none)
# @return: (none)
init_env() {
    #[[ ! -z ${ENV_HOME} && -f "${ENV_HOME}"/env.sh ]] && source "${ENV_HOME}"/env.sh
    [[ -z ${LOG_HOME} ]] && LOG_HOME=${BASEDIR}/logs
    if [[ ! -d ${LOG_HOME} ]]; then
        mkdir -p ${LOG_HOME} 2>/dev/null || { echo "failed to create ${LOG_HOME}" && exit 1; }
    fi
    [[ -z ${CONSOLE_LOG} ]] && CONSOLE_LOG="${LOG_HOME}/${APPNAME}_console.log"
    [[ -z ${RUN_AS} ]] && RUN_AS=admin
    #fix permission
    #chown ${RUN_AS}. $BASEDIR ${LOG_HOME} 2>/dev/null
    #get jar name
    jarName=$(get_jarName)
    [[ $? = 1 ]] && echo "$jarName" && exit 1
    [[ -z ${MAX_WAIT_TIME} ]] && MAX_WAIT_TIME=60

    #JVM OPTS
    # Which java to use
    [[ -z ${JAVA_HOME} ]] && JAVA="java" || JAVA="${JAVA_HOME}/bin/java"
    # Generic jvm settings you want to add
    [[ -z ${CUSTOM_JAVA_OPTS} ]] && CUSTOM_JAVA_OPTS=""
    # Memory options
    [[ -z ${JAVA_HEAP_OPTS} ]] && JAVA_HEAP_OPTS="-Xms512M -Xmx512M"
    # JVM performance options
    if [[ -z ${JVM_PERFORMANCE_OPTS} ]]; then
        JVM_PERFORMANCE_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"
    fi
    #JMX Settings
    # JMX port to use
    if [[ ! -z ${JMX_REMOTE_PORT} ]]; then
        JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
        JMX_OPTS="-Dcom.sun.management.jmxremote.port=${JMX_REMOTE_PORT} ${JMX_OPTS}"
    fi
    #spring log options
    SPRINGBOOT_OPTS=''
    SPRING_LOG_HOME="${LOG_HOME}"
    SPRING_LOG_FILE="${LOG_HOME}/${APPNAME}_spring.log"
    if [[ "x${SPRING_LOG}" == "xtrue" ]]; then
        SPRINGBOOT_OPTS=" --logging.path=${SPRING_LOG_HOME} --logging.file=${SPRING_LOG_FILE}"
    fi
    # GC options
    GC_FILE_SUFFIX="-gc-$(date '+%F_%H-%M-%S').log"
    GC_LOG_FILE_NAME=''
    if [[ "x${GC_LOG_DISABLED}" != "xtrue" ]]; then
        GC_LOG_FILE_NAME=${APPNAME}${GC_FILE_SUFFIX}
        GC_LOG_OPTS="-Xloggc:${LOG_HOME}/${GC_LOG_FILE_NAME} -verbose:gc -XX:+PrintGCDetails \
      -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps"
    fi
    # Debug Options
    if [[ "x${JAVA_DEBUG}" == "xtrue" && ! -z "${JAVA_DEBUG_PORT}" ]]; then
        JAVA_DEBUG_OPTS="${JAVA_DEBUG_OPTS} -Xdebug -Xnoagent -Djava.compiler=NONE \
        -Xrunjdwp:transport=dt_socket,address=${JAVA_DEBUG_PORT},server=y,suspend=n"
    fi
    # Other options
    OTHER_JAVA_OPTS="-Dfile.encoding=UTF-8 -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=${LOG_HOME}"

    JAVA_OPTS="${JAVA_HEAP_OPTS} ${JVM_PERFORMANCE_OPTS} ${JMX_OPTS} \
    ${GC_LOG_OPTS} ${JAVA_DEBUG_OPTS} ${OTHER_JAVA_OPTS} ${CUSTOM_JAVA_OPTS}"
}

# @info:    get the name of jar in lib.
# @args:    (none)
# @deps:    (none)
# @return:  the name of jar.
get_jarName() {
        if [[ $(find ${LIB_HOME} -type l -name '*.jar' 2>/dev/null | wc -l) != 1 ]]; then
            echo "there must be one jar file in ${LIB_HOME}."
            return 1
        else
            local jarName=$(find ${LIB_HOME} -type l -name '*.jar' | xargs basename)
            echo "${jarName%.*}"
            return 0
        fi
}

# @info:    get the pid of app.
# @args:    (none)
# @deps:    (none)
# @return:  pid. exit code: 1/2/0
get_appPid() {
    local appName="${APPNAME}"
    local matchCount=$(ps -ef | egrep -w "java .*\s+${appName}\$" | wc -l)
    if [[ ${matchCount} > 1 ]]; then
        #there are more than one process match ${appName}
        return 2
    elif [[ ${matchCount} = 0 ]]; then
        #can't get the pid match ${appName}
        return 1
    else
        local pid=$(ps -ef | egrep -w "java .*\s+${appName}\$" | awk '{print $2}')
        echo $pid
        return 0
    fi
}

# @info:    start app.
# @args:    (none)
# @deps:    init_env app_opts get_appPid
# @return:  0 for successful, 1 for failed.
start_app(){
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? != 1 ]];then
        echo "${APPNAME} already running!(pid:${app_pid})"
        return 0
    fi

    #rolling console log
    if [[ -f "${CONSOLE_LOG}" ]]; then
        mv "${CONSOLE_LOG}" "${CONSOLE_LOG}.$(date '+%F_%H-%M-%S')"
    fi

    # start now
    nohup $JAVA ${JAVA_OPTS} -jar ${LIB_HOME}/${jarName}.jar ${SPRINGBOOT_OPTS} ${APP_OPTS} \
    ${APPNAME} >> "${CONSOLE_LOG}" 2>&1 &
    sleep 2
    app_pid=$(get_appPid)
    if [[ $? = 0 ]];then
        echo "${APPNAME} start successfully, running with pid: ${app_pid}."
        return 0
    else
        echo "can not find pid, failed to start ${APPNAME}."
        return 1
    fi
}

# @info:    stop app.
# @args:    (none)
# @deps:    get_appPid
# @return:  0 for successful, 1 for failed.
stop_app(){
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? = 1 ]];then
        echo "${APPNAME} is not running!"
        return 0
    elif [[ $? = 2 ]]; then
        echo "there are more than one process match ${APPNAME}."
        return 1
    fi
    # stop now
    echo "stop ${APPNAME} now..."
    kill ${app_pid}

    local timer=0
    until [[ $(get_appPid 1>/dev/null 2>&1; echo $?) != 0 || $timer = ${MAX_WAIT_TIME} ]]; do
        sleep 1
        ((timer=$timer+1))
    done

    if [[ $timer = ${MAX_WAIT_TIME} ]]; then
        echo "stop ${APPNAME} failed after ${MAX_WAIT_TIME} seconds."
        echo "stop ${APPNAME} by force now."
        kill -9 ${app_pid}
    fi
    sleep 1
    app_pid=$(get_appPid)
    if [[ $? != 1 ]]; then
        echo "stop ${APPNAME} failed with pid: ${app_pid}."
        return 1
    else
        echo "stop ${APPNAME} successfully."
    fi
}

# @info:    check app running status.
# @args:    (none)
# @deps:    get_appPid
# @return:  the app running status info.
status_app() {
    local app_pid
    app_pid=$(get_appPid)
    if [[ $? = 0 ]];then
        echo "${APPNAME} is running with pid: ${app_pid}."
    elif [[ $? = 2 ]]; then
        echo "there are more than one process match ${APPNAME}."
    else
        echo "${APPNAME} is not running."
    fi
}


#end function define
#main
init_script
init_env
app_opts

if [[ -n "${RUN_AS}" && "${RUN_AS}" != "root" && "`id -u`" = "0" ]];then
    su - ${RUN_AS} -c "bash $BASEDIR/$SCRIPTNAME $*"
    exit $?
fi

case "$1" in
start)
    start_app
    ;;
stop)
    stop_app
    ;;
restart)
    stop_app
    start_app
    ;;
status)
    status_app
    ;;
*)
    Usage
    exit 1
    ;;
esac
exit 0

