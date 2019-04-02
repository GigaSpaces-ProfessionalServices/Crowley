# @(#) com.ibm.mq/src/native/jdbc/amd64_linux_2/Makefile, java.classes, k701 1.1 09/12/11 10:26:48
# Description:   Linux x86 makefile for XA switch files
#
#
# <N_OCO_COPYRIGHT>
# Licensed Materials - Property of IBM
#
# 5724-H72, 5655-R36, 5724-L26, 5655-L82
#
# (c) Copyright IBM Corp. 2008, 2011 All Rights Reserved.
#
# US Government Users Restricted Rights - Use, duplication or
# disclosure restricted by GSA ADP Schedule Contract with
# IBM Corp.
# <NOC_COPYRIGHT>
#

# These flags allow us to pick up the Oracle libraries
# This makefile assumes that ORACLE_HOME is set appropriately

ORALIBS=-lclntsh
ORALIBPATH=-L$(ORACLE_HOME)/lib32 -Xlinker "-rpath=${ORACLE_HOME}/lib32"

# The flags allow us to pick up the DB/2 libraries
# This is DB/2 8.1's standard installation directory. If you're using a
# different level of DB/2, override DB2LIBPATH accordingly

DB2LIBS=-ldb2
DB2LIBPATH=-L$(DB2_HOME)/lib -Xlinker "-rpath=$(DB2_HOME)/lib"

# This is modified at install time if MQ is not installed in
# the default directory.
MQ_INSTALLATION_PATH=/opt/mqm

# We need to specify the location of the WMQ libraries to build
# against
MQLIBS=-lmqe_r
MQLIBPATH=-L$(MQ_INSTALLATION_PATH)/lib -Xlinker "-rpath=$(MQ_INSTALLATION_PATH)/lib"


LINK_OPTIONS = -m32 -fpic -e MQStart ${MQLIBPATH} ${MQLIBS}
all:
	@echo 'Usage: make [JAVA_HOME=jdk_path] [db2|oracle]'

db2:
	gcc -shared ${LINK_OPTIONS} ${DB2LIBPATH} ${DB2LIBS} -o /var/mqm/exits/jdbcdb2 jdbcdb2.o

oracle:
	gcc -shared ${LINK_OPTIONS} ${ORALIBPATH} ${ORALIBS} -o /var/mqm/exits/jdbcora jdbcora.o
