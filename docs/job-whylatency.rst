Why Latency
-----------

**Why Latency** job helps to know if reaching a web/application server
takes longer than the expected.

**Configuration Parameters**

-  **Name:** Name of the LatencyChecker instance (any name can be given)
-  **Host:** domain/ip of the server to ping to
-  **Port:** port of the server to ping to
-  **Max_Latency:** Maximum accepted latency. If the actual latency
   exceeds this value, an alert will be triggered.
-  **Cron:** Frequency at which this job should be run. Note that the
   frequency can not be lesser than 1 minute.
