## Start / Stop Kafka
~~~shell
	brew services start zookeeper
	brew services start kafka
	brew services list

	brew services stop kafka
	brew services stop zookeeper
~~~


## Topics
* List:
~~~shell
    kafka-topics --bootstrap-server localhost:9092 --list
~~~

* Create: 
~~~shell
    kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic --partitions 3 --replication-factor 1
~~~

* Describe: 
~~~shell
    kafka-topics --bootstrap-server localhost:9092 --topic first_topic --describe
~~~

* Delete: 
~~~shell
    kafka-topics --bootstrap-server localhost:9092 --delete --topic first_topic
~~~


## Producers
* Produce:
~~~shell
    kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic 
~~~

* Produce with properties
~~~shell
    ... --producer-property acks=all
~~~

* Produce with key: 
~~~shell
    ... --property parse.key=true --property key.separator=:
~~~


## Consumers
* Consume
~~~shell
    kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
~~~

* Consume from beginning
~~~shell
    ... --from-beginning
~~~

* Consume display key, values and timestamp
~~~shell
    ... --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true
~~~


## Consumer Group
* Consume
~~~shell
    ... --group my-first-cg
~~~

* List
~~~shell
    kafka-consumer-groups --bootstrap-server localhost:9092 --list
~~~

* Describe
~~~shell
    kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-cg
~~~


## Consumer reset offset
* Reset to beginning of all partitions
~~~shell
    kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-cg --reset-offsets --to-earliest  --execute --all-topics
~~~

* Shift forward by 2
~~~shell
    kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-cg --reset-offsets --shift-by 2 --execute --all-topics
~~~

* Shift backward by 2: 
~~~shell
    ... --shift-by -2 ...
~~~