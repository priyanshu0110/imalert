# imAlert

### What is imAlert ?

imAlert is a free and open source project developed under Yesfoss. The core purpose behind developing this project is to provide a free and reliable solution for monitoring the infrastructure and alert for the action to be taken.

### Who can use this tool ?

* A small or large scale organization who are using the components like Redis, Elasticsearch etc. in the backend and wants to monitor and get alert if there seems to be something abnormal.
* An individual who runs/maintains a website/server and wants to know the latency and/or reachability.
* An individual/organization who wants to keep an eye on the health of their website/application and alert if it’s down.
* An individual or a team who wants to keep getting the reminder of the deadline time-to-time so that they do not miss it. A reminder can be like `a project release date` or `a domain renewal date` etc.

### What can be monitored using imAlert ?

As of now, the following 6 monitoring modules have been added:

* `I Watch Redis :` This module is responsible for monitoring Redis instances and alert for them if there seems to be something unexpected. One can monitor the IOPS. Keys and Health of the Redis instances. Both Standalone and Cluster Redis are supported.

* `I Watch Elastic :` This module is responsible for monitoring the Elasticsearch cluster and alert if the cluster doesn’t seem to be in a good state. As of now, Only health can be monitored which internally checks the cluster’s health status (Red/Yellow/Green) and unassigned shards.

* `Remind Me :` This module works as a reminder. One can configure a reminder with the message which he wants to get periodically.

* `Check My Health :` This module is responsible for checking the health of the web/application server using the health_check url (if exists) and alert if it doesn’t receive the desired response.

* `Is It Reachable :` This module helps to check and alert if an address is unreachable. This module can specially help if the machine is down or change in the firewalls/securityGroups blocks the reachability.

* `Why Latency :` This module helps to know if reaching a web/application server takes longer than the expected.

### Where do I get the alert ?

As of now, the only supported medium of alerting is `Slack`. You need to have a Slack channel and an incoming webhook url for that.

### How to Install
Please follow through the steps described on [https://www.yesfoss.org/imalert-installation/](https://www.yesfoss.org/imalert-installation/)

### To know more about this project, please visit [https://www.yesfoss.org/](https://www.yesfoss.org/)

## Contributing
Pull requests are welcome. Feature improvements or addition of a new job is highly appreciated. For major changes in the existing feature, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)
