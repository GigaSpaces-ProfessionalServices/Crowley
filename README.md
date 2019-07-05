# Crowley
Repo contains code for Geosparial demo, delta server, mirror and instructions how to setup instance. 
All projects are aimed on insightedge-space in group xap-14.0.0

# File description
Folders:<br>
db2-delta-server <- application to move data from db2 to space<br>
deploy			 <- contains space with analyticsXtreme for 14.0 and 14.2<br>
HQ vm			 <- some configs from similar vm with analyticsXtreme<br>
insightedge-geo-demo-master  <- demo version on maven<br>
insightedge-geo-demo-old-master  <- demo version on sbt<br>
lib   <- usefull libs if something's missing<br>
mirror <- application to move data from space to db2 (old version)<br>
<br><br>
Files: <br>
build.sbt - updated file to build 'insightedge-geo-demo-old-master'<br>
extra table notes  - usefull command for db2<br>
fix sudoers file - command to fix sudoers file<br>
Run demo  - how to run demo with db2 and analyticsxtreme<br>
successful log MQM  - logger output if mqm was configured correctly<br>
kafka_2.11-2.1.1.tgz  - kafka installer<br>
analyticsXtreme_analytics-xtreme-jdbc-demo.json  - interpreter for Zeppelin to query data<br>
Setup instance.txt - how to setup instance<br>
Running example.doc - how to run demo (older version, without analyticsxtreme)<br>
