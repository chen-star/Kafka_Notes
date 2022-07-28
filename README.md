# Kafka

[Official Doc](https://kafka.apache.org/documentation/#gettingStarted)

---
---

## Kafka Theory

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
    * Producers consider message as 'written successfully' the moment the message was sent without waiting for the broker to accept it. 
  * acks=1, Producers will wait for leader's ACK
    * Default for Kafka v1.0 to v2.8.
  * acks=all / acks=-1, Leader + Replica's ACK
    * Default for Kafka 3.0+
    * The leader checks to see if there are enough in-sync replicas for safely writing the message 
      * controlled by the broker setting `min.insync.replicas`
        * eg. min.insync.replicas=2: at least leader and 1 replica need to ack


#### Producer Retries
* In case of transient failures, developers are expected to handle exceptions; otherwise data will be lost.
* There's a `retries` setting
  * defaults to 0 for Kafka <= 2.0
  * defaults to Integer.MAX_VALUE for Kafka >= 2.1
* The `retry.backoff.ms` is by default 100 ms
* Retries are bounded by a timeout, since Kafka 2.1, you can set: `delivery.timeout.ms=120000`(2 min)


#### Idempotent Producer
* The producer can introduce duplicate messages in Kafka due to network errors
* They are default since Kafka 3.0, `enable.idempotence=true`
* They come with:
  * retries=Integer.MAX_VALUE
  * max.in.flight.request=5 (Kafka >= 1.0)
  * acks=all

#### Message Compression at Producer
* Compression on a batch of messages.
* `compression.type` can be none(default), gzip, lz4, snappy, zstd
* Consider tweaking `linger.ms` and `batch.size` to have bigger batches.
  * By default, Kafka producers try to send records ASAP.
  * linger.ms (default 0): how long to wait till we send a batch.
  * batch.size (default 16KB): if a batch is filled before linger.ms, increase the batch size.

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

* Partition Re-balance
  * Re-balance happens when a consumer leaves or joins a group. Also happens if admin adds new partition into a topic.
  * Eager Re-balance
    * All consumer stops, give up their membership of partitions.
    * They re-join the consumer group and get a new partition assignment.
    * Consumers don't necessarily get back the same partitions as they used to.
  * Cooperative Re-balance (Incremental Re-balance)
    * Reassigning a small subset of partitions from one consumer to another.
    * Other consumers that don't have reassigned partitions can still process uninterrupted.
    * Can go through several iterations to find a 'stable' assignment.

* Static Membership
  * By default, when a consumer leaves a group, its partitions are revoked and re-assigned.
  * If it joins back, it will have a new 'member ID' and new partitions assigned.
  * If you specify group.instance.id, it makes the consumer a static member.
  * Upon leaving, the consumer has up to session.timeout.ms to join back and get back its partitions, without triggering a re-balance. 

#### Delivery Semantics
* At least once
  * Offsets are committed after the message is processed.

* At most once
  * Offsets are committed as soon as messages are received.

* Exactly once
  * Can be achieved for Kafka => Kafka workflows using the Transactional API.
  * For Kafka => Sink workflows, use an idempotent consumer.

---
---

## CLI Commands
[commands.md](CLI_commands.md)


## Kafka Connect
* Solves External Source => Kafka and Kafka => External Sink


## Kafka Stream
* Solves transformations Kafka => Kafka


## Kafka Schema Registry
* Using Schema in Kafka