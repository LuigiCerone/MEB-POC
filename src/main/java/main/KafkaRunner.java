package main;

import fab_data_connector.FabDataStreamer;
import message_stream.StreamProcessor;
import raw_data_connector.RawDataStreamer;
import stream_processor.PersistentTopicStreamer;

public class KafkaRunner {
    RawDataStreamer rawDataStreamer;
    FabDataStreamer fabDataStreamer;
    StreamProcessor streamProcessor;
    static PersistentTopicStreamer persistentTopicStreamer;

    public KafkaRunner() {

        this.rawDataStreamer = new RawDataStreamer(123457, RawDataStreamer.INPUT_TOPIC);
        rawDataStreamer.start();

        this.fabDataStreamer = new FabDataStreamer(123458, FabDataStreamer.INPUT_TOPIC);
        fabDataStreamer.start();

        this.streamProcessor = new StreamProcessor(123459);
        streamProcessor.start();

        persistentTopicStreamer = new PersistentTopicStreamer(123455);
        persistentTopicStreamer.start();

//        persistentTopicStreamer = new PersistentTopicStreamer(123455);
//        persistentTopicStreamer.start();
    }

    public static PersistentTopicStreamer getPersistentTopicStreamer() {
        return persistentTopicStreamer;
    }
}
