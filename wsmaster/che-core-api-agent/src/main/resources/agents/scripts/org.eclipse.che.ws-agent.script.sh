#
# Copyright (c) 2012-2016 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Codenvy, S.A. - initial API and implementation
#

unset PACKAGES
unset SUDO
command -v tar >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" tar"; }
command -v curl >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" curl"; }
test "$(id -u)" = 0 || SUDO="sudo"

AGENT_BINARIES_URI=file:///mnt/che/ws-agent.tar.gz
CHE_DIR=$HOME/che
LINUX_TYPE=$(cat /etc/os-release | grep ^ID= | tr '[:upper:]' '[:lower:]')
LINUX_VERSION=$(cat /etc/os-release | grep ^VERSION_ID=)
MACHINE_TYPE=$(uname -m)

mkdir -p ${CHE_DIR}
${SUDO} mkdir -p /projects
${SUDO} sh -c "chown -R $(id -u -n) /projects"

########################
### Install packages ###
########################

# Red Hat Enterprise Linux 7
############################
if echo ${LINUX_TYPE} | grep -qi "rhel"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} yum install ${PACKAGES};
    }

# Ubuntu 14.04 16.04 / Linux Mint 17
####################################
elif echo ${LINUX_TYPE} | grep -qi "ubuntu"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} apt-get update;
        ${SUDO} apt-get -y install ${PACKAGES};
    }

# Debian 8
##########
elif echo ${LINUX_TYPE} | grep -qi "debian"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} apt-get update;
        ${SUDO} apt-get -y install ${PACKAGES};
    }

# Fedora 23
###########
elif echo ${LINUX_TYPE} | grep -qi "fedora"; then
    PACKAGES=${PACKAGES}" procps-ng"
    test "${PACKAGES}" = "" || {
        ${SUDO} dnf -y install ${PACKAGES};
    }

# CentOS 7.1 & Oracle Linux 7.1
###############################
elif echo ${LINUX_TYPE} | grep -qi "centos"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} yum -y install ${PACKAGES};
    }

# openSUSE 13.2
###############
elif echo ${LINUX_TYPE} | grep -qi "opensuse"; then
    test "${PACKAGES}" = "" || {
        ${SUDO} zypper install -y ${PACKAGES};
    }

# Alpine 3.3
############$$
elif echo ${LINUX_TYPE} | grep -qi "alpine"; then

  # Setup OpenJDK8 (not using glibc) if missing
  INSTALL_JDK=false
  command -v ${JAVA_HOME}/bin/java >/dev/null 2>&1 || { PACKAGES=${PACKAGES}" openjdk8"; INSTALL_JDK=true;}

    test "${PACKAGES}" = "" || {
        ${SUDO} apk update
        ${SUDO} apk add ${PACKAGES};
    }

    # Link OpenJDK to JAVA_HOME
    if [ ${INSTALL_JDK} = true ]; then
      export JAVA_HOME=${CHE_DIR}/jdk1.8
      ln -s /usr/lib/jvm/java-1.8-openjdk $JAVA_HOME
    fi


else
    >&2 echo "Unrecognized Linux Type"
    >&2 cat /etc/os-release
    exit 1
fi

####################
### Install java ###
####################
command -v ${JAVA_HOME}/bin/java >/dev/null 2>&1 || {
    export JAVA_HOME=${CHE_DIR}/jdk1.8
    command -v ${JAVA_HOME}/bin/java >/dev/null 2>&1 || {
        JDK_URL=http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jdk-8u45-linux-x64.tar.gz
        curl -s -j -k -L -H "Cookie: oraclelicense=accept-securebackup-cookie" "${JDK_URL}" | tar -C ${CHE_DIR} -xzf -
        mv ${CHE_DIR}/jdk1.8.0_45 ${CHE_DIR}/jdk1.8
    }
}

########################
### Install ws-agent ###
########################

rm -rf ${CHE_DIR}/ws-agent
mkdir -p ${CHE_DIR}/ws-agent
curl -s  ${AGENT_BINARIES_URI} | tar  xzf - -C ${CHE_DIR}/ws-agent

###############################################
### ws-agent run command will be added here ###
### ~/che/ws-agent/bin/catalina.sh run      ###
###############################################
