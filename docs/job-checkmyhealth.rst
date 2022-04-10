Check My Health
---------------

**Check My Health** job is responsible for checking the health of the
web/application server using the health_check url and alert if it
doesn’t receive the desired response.

**Configuration Parameters**

-  **Name:** Name of the healthcheck instance (Any name can be given)
-  **Url:** Url of the healthcheck endpoint. e.g:
   http://mysite.com/getStatus
-  **Response:** Response that is expected to be returned from the
   healthcheck endpoint. e.g: “OK Status”. An alert will be sent if the
   expected response is not returned.
-  **Cron:** Frequency at which this job should be run. Note that the
   frequency can not be lesser than 1 minute.
