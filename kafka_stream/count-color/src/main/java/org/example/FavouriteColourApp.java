package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class FavouriteColourApp {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-java");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        StreamsBuilder builder = new StreamsBuilder();

        // 1. create the topic of users' keys to colors
        KStream<String, String> textLines = builder.stream("favourite-colour-input");

        // 2. create <user, color> stream
        KStream<String, String> usersAndColors = textLines
                .filter((k, v) -> v.contains(","))
                .selectKey((k, v) -> v.split(",")[0].toLowerCase())
                .mapValues(v -> v.split(",")[1].toLowerCase())
                .filter((user, color) -> Arrays.asList("green", "blue", "red").contains(color));

        usersAndColors.to("user-keys-and-colours");

        // 3. read the topic as KTable to update
        KTable<String, String> usersAndColorsTable = builder.table("user-keys-and-colours");

        // 4. count colors
        KTable<String, Long> favoriteColors = usersAndColorsTable
                .groupBy((user, color) -> new KeyValue<>(color, color))
                .count(Materialized.as("CountsByColours"));

        // 5. output results to Kafka topic
        favoriteColors.toStream().to("favourite-colour-output", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        // only do this in dev - not in prod
        streams.cleanUp();
        streams.start();

        // print the topology
        streams.localThreadsMetadata().forEach(System.out::println);

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
