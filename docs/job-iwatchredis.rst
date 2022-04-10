I Watch Redis
-------------

**I Watch Redis** is the job responsible for monitoring the Redis
instances. It supports the monitoring of keys, IOPs and status. Both
standalone and cluster Redis can be added.

**Configuration parameters**

-  **Name:** Name of the Redis instance (Any name can be given)
-  **Host:** Host/IP/Service of the Redis instance to connect to
-  **Port:** Port of the Redis instance
-  **Auth:** Password of the Redis instance
-  **Type:** Choose the type of Redis instance (Standalone/Cluster)
-  **Health Monitoring:** Check this field to enable health monitoring
-  **Keys Monitoring:** Check this field to enable keys monitoring
-  **IOPS Monitoring:** Check this field to enable IOPs monitoring
-  **Min Keys:** Minimum number of keys to be present in Redis at any
   point in time. If number of keys is lesser than this value, an alert
   will be sent. This check will be done only if keys monitoring is
   enabled.
-  **Max Keys:** Maximum number of keys to be present in Redis at any
   point in time. If number of keys is higher than this value, an alert
   will be sent. This check will be done only if keys monitoring is
   enabled.
-  **Min IOPS:** Minimum IOPS on Redis at any point in time. If IOPS is
   lesser than this value, an alert will be sent. This check will be
   done only if IOPS monitoring is enabled.
-  **Max IOPS:** Maximum IOPS on Redis at any point in time. If IOPS is
   higher than this value, an alert will be sent. This check will be
   done only if IOPS monitoring is enabled.
-  **Cron:** Frequency at which this job should be run. Note that
   frequency can not be lesser than 1 minute.
