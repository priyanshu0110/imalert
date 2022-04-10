Is It Reachable
---------------

**Is It Reachable** job helps to check and alert if an address is
unreachable. This job can specially help if the machine is down or
change in the firewalls/securityGroups blocks the reachability.

**Configuration Parameters**

-  **Name:** Name of the reachability checker instance. (Any name can be
   given)
-  **Host:** Host of the server to connect to
-  **Port:** Port of the server to connect to
-  **Timeout:** Maximum time to wait for getting the response from the
   server. If the timeout occurs, an alert will be sent.
-  **Cron:** Frequency at which this job should be run. Note that
   frequency can not be lesser than 1 minute.
