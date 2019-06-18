# Crowley
Repo contains code for Geosparial demo, delta server, mirror and instructions how to setup instance. 
All projects are aimed on insightedge-space in group xap-14.0.0

# File description
Folders:
db2-delta-server <- application to move data from db2 to space
deploy			 <- contains space with analyticsXtreme for 14.0 and 14.2
HQ vm			 <- some configs from similar vm with analyticsXtreme
insightedge-geo-demo-master  <- demo version on maven
insightedge-geo-demo-old-master  <- demo version on sbt
lib   <- usefull libs if something's missing
mirror <- application to move data from space to db2 (old version)

Files:
build.sbt <- updated file to build 'insightedge-geo-demo-old-master'
extra table notes  <- usefull command for db2
fix sudoers file <- command to fix sudoers file
Run demo  <- how to run demo with db2 and analyticsxtreme
successful log MQM  <- logger output if mqm was configured correctly
kafka_2.11-2.1.1.tgz  <- kafka installer
analyticsXtreme_analytics-xtreme-jdbc-demo.json  <- interpreter for Zeppelin to query data
Setup instance.txt <- how to setup instance
Running example.doc <- how to run demo (older version, without analyticsxtreme)