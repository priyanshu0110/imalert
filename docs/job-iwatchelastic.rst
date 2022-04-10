I Watch Elastic
---------------

**I Watch Elastic** is the job responsible for monitoring the
Elasticsearch cluster. It supports the monitoring of health and
unassigned shards.

**Configuration Parameters**

-  **Name:** Name of the cluster (any name can be given)
-  **Url:** url to reach the cluster. e.g: http://192.168.0.1:9200
-  **User:** Username to be submitted when asked for the HTTP
   authentication
-  **Auth:** Password to be submitted when asked for the HTTP
   authentication
-  **Auth_Required:** Check this box if http auth is enabled for the
   cluster
-  **Health Monitoring :** Check this box if elastic health is to be
   monitored. An alert will be triggered if cluster goes into Red or
   Yellow state. This internally checks the unassigned shards as well.
-  **Cron:** Frequency at which this job should be run. Note that the
   frequency can not be lesser than 1 minute.
