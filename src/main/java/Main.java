import fab_data_connector.FabConsumer;
import fab_data_connector.FabDataStreamer;
import fab_data_connector.FabEvent;
import raw_data_connector.RawConsumer;
import raw_data_connector.RawDataStreamer;
import raw_data_connector.RawEvent;
import utils.TopicUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] agrs) throws Exception {
//        FabConsumer fabConsumer = new FabConsumer(12345, "test", Arrays.asList(FabConsumer.TOPIC));
        System.out.println("Started");
//        fabConsumer.run();

//        RawConsumer rawConsumer = new RawConsumer(12346, "test", Arrays.asList(RawConsumer.TOPIC));
//        rawConsumer.run();
//        RawDataStreamer rawDataStreamer = new RawDataStreamer(123457, RawConsumer.TOPIC);
//        rawDataStreamer.start();

        FabDataStreamer fabDataStreamer = new FabDataStreamer(123458, FabConsumer.TOPIC);
        fabDataStreamer.start();
//        TopicUtils.createTopic("testttt", 1, 1);
    }
}
