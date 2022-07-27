*** Start / Stop Kafka
~~~shell
	brew services start zookeeper
	brew services start kafka
	brew services list

	brew services stop kafka
	brew services stop zookeeper
~~~

*** Topics
~~~shell
	List: 
	  kafka-topics --bootstrap-server localhost:9092 --list
	Create: 
	  kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic --partitions 3 --replication-factor 1
	Describe: 
	  kafka-topics --bootstrap-server localhost:9092 --topic first_topic --describe
	Delete: 
	  kafka-topics --bootstrap-server localhost:9092 --delete --topic first_topic
~~~

*** Producers
~~~shell
	Produce: 
	  kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic 
	Produce with properties: 
	  ... --producer-property acks=all
	Produce with key: 
	  ... --property parse.key=true --property key.separator=:
~~~

*** Consumers
~~~shell
	Consume: 
	  kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
	Consume from beginning: 
	  ... --from-beginning
	Consume display key, values and timestamp: 
	  ... --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true
~~~

*** Consumer Group
~~~shell
	Consume: 
	  ... --group my-first-cg
	List: 
	  kafka-consumer-groups --bootstrap-server localhost:9092 --list
	Describe: 
	  kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-cg
~~~

*** Consumer reset offset
~~~shell
	Reset to beginning of all partitions: 
	  kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-cg --reset-offsets --to-earliest  --execute --all-topics
	Shift forward by 2: 
	  kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-cg --reset-offsets --shift-by 2 --execute --all-topics
	Shift backward by 2: 
	  ... --shift-by -2 ...
~~~