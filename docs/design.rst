Design
======

This projects consists of 3 modules.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-  **imalert-ui :** This module has been written in python-django and
   provides the user interface for configuring the alerts.
-  **imcontroller :** This module has been written in Java and works as
   a controller for the monitoring jobs to be executed. This module
   sends the jobs for execution at the time when they are supposed to
   be, as per the schedule configuration provided by the user.
-  **imworker :** This is another module written in Java which works as
   a worker. This module receives the jobs sent by the controller and
   executes them. There can be run N number of worker’s instances as per
   the need.

How do these modules communicate with one-another ?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This project has another component in place which can be considered as
the heart of this project. That component is a
`Redis <https://redis.io/>`__ server. The Redis server is logically in
the center of these modules and acts as a communication medium for them.
Let us understand it using the diagram below.

.. figure:: https://github.com/priyanshu0110/imalert/tree/master/docs/images/design.png?raw=true
   :alt: alt text

   alt text

-  The user adds a new monitoring job through the UI
-  imalert-ui module writes the job configuration and cron schedule to
   the Central Redis.
-  The controller fetches all the jobs and for each job, checks if the
   current time matches with the configured schedule time. If yes,
   pushes the job into the stream.
-  The worker instances consume the stream’s messages as a group (so
   that duplicate messages are not processed) and assigns each job to
   one of the worker threads. Note that the worker module uses the
   `ThreadpoolExecutor <https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html>`__
   and maintains a pool of threads. ``Number of core threads``,
   ``maximum     number of threads`` and ``worker queue size`` of the
   threadpool can be configured through the module’s property.
-  On execution of a job, the worker fetches the configuration of that
   particular job from the central-redis and runs the operation(s).
-  If the result is not meeting the expectations set by the user, the
   worker sends an alert on the configured slack channel.
