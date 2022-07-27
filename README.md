# Kafka

[Official Doc](https://kafka.apache.org/documentation/#gettingStarted)


## Kafka Theory

![](images/IMG_4007 2.jpeg)

### Broker & Topic

#### Broker
* A Kafka cluster is composed of multiple brokers (servers).
* Each broker contains certain topic partitions.
* After connecting to any broker (called a bootstrap broker), you will be connected to the entire cluster.
* Each broker knows about all brokers, topics and partitions. (metadata)

#### Topic
* A particular stream of data.
* A topic is identified by its name. Any kind of message format.
* The sequence of messages is called a data stream.
* Immutable: once data is written to a partition, it cannot be changed. 
* Data is kept only for a limited time (default is 1 week - configurable).

#### Partition & Offset
* A topic is split into partitions. 
* Messages within each partition are ordered. 
* Each message within a partition gets an incremental id, called offset.
    * Offsets are not re-used even if previous messages have been deleted.
    * Order is guaranteed only within a partition.

#### Replication
* Leader-Follower model
  * At any time only 1 broker can be a leader for a given partition.
  * Producers can only send data to the broker that is leader of a partition.
  * By default:
    * Producers only write to the leader broker.
    * Consumers will read from the leader broker.
  * Consumer Replica Fetching
    * It's possible to config consumers to read from the closest replica.

---

### Producer
* Producers write data to topics.
* Producers know to which partition to write to (and which Kafka broker has it).
* In case of Kafka broker failures, producers will automatically recover.

#### Message Key
* Producers can optionally choose to send a key with the message.
* If key == null, data is sent round-robin to partitions; Else, all messages for that key will always go to the same partition(hashing)

#### Message Serializer
* kafka only accepts bytes as input from producers, and sends bytes out as an output to consumers.
* Serializers are only used on the value and the key.

#### ACKs
* Producers can choose to receive ACK of data writes:
  * acks=0, Producers won't wait for ACK
  * acks=1, Producers will wait for leader's ACK
  * acks=all, Leader + Replica's ACK

---

### Consumer
* Consumers read data from a topic - pull model.
* Consumers automatically know which broker to read from.
* In case of broker failures, consumers know how to recover.
* Data is read in order from low to high offset within each partition.

#### Message Deserializer
* Transform bytes into objects / data.
* Deserializers are only used on the value and the key.

#### Consumer Offset
* Kafka stores the offset at which a consumer group has been reading.
* When a consumer in a group has processed data received from Kafka, it should be periodically committing the offsets.
* If a consumer dies, it will be able to read back from where it left off thanks to the committed consumer offsets. 

#### Consumer Group
* Each consumer within a group reads from exclusive partitions of a topic. 
* If you have more consumers than partitions, some consumers will be inactive.
* Can have multiple consumer groups on the same topic.

#### Delivery Semantics
* At least once
  * Offsets are committed after the message is processed.

* At most once
  * Offsets are committed as soon as messages are received.

* Exactly once


## CLI Commands
[commands.md](CLI_commands.md)