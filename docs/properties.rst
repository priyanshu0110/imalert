Properties
----------

imcontroller.properties
~~~~~~~~~~~~~~~~~~~~~~~

-  module.redis.host=<ip/host address of central redis>
-  module.redis.port=<port address of central redis>
-  module.redis.password=<password of central redis>
-  module.redis.timeout=<timeout of redis query>
-  module.redis.pool.maxtotal=<Number of redis connections to be
   maintained in the pool>
-  module.redis.pool.close.after.ms=<Milliseconds to wait before
   destroying the Redis connection pool>

-  module.job.hash=H:IMALERT:JOB_HASH //Do not change 
-  module.job.queue=<name of the redis stream the messages are supposed to
be sent to> 
-  module.job.queue.max.length=<maximum number of messages to
keep in the queue of the stream>. If the queue gets full, older messages
will get evicted.

imworker.properties
~~~~~~~~~~~~~~~~~~~

-  module.redis.host=<ip/host address of central redis>
-  module.redis.port=<port address of central redis>
-  module.redis.password=<password of central redis>
-  module.redis.timeout=<timeout of redis query>
-  module.redis.pool.maxtotal=<Number of redis connections to be
   maintained in the pool>. Always set this value higher than the
   maximum number of threads (worker) you configure to run.
-  module.redis.pool.close.after.ms=<Milliseconds to wait before
   destroying the Redis connection pool>

-  module.job.queue=<name of the redis stream the messages are
supposed to be consumed from> // this should be same stream which
controller is sending to. 
-  module.job.consumer.group=<group id of the
consumer> 
-  module.job.consumer.id=<id of the consumer> 
-  module.job.stream.poll.count=<maximum number of messages to fetch at
once> 
-  module.job.stream.poll.delay.millis=<frequency in milliseconds
to poll the messages from the stream>

-  module.workers.core=<number of worker threads to run initially> 
-  module.workers.max=<maximum number of threads that the module can run> 
-  module.workers.queue.length=<maximum number of tasks can be pending in
the queue>. // If the queue gets full, **RejectedExecutionException**
will be thrown and the task execution will be discarded.

-  module.slack.webhook.url=<slack webhook url>
