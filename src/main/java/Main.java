import fab_data_connector.FabConsumer;
import raw_data_connector.RawConsumer;
import utils.TopicUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {
    public static void main(String[] agrs) throws Exception {
//        FabConsumer fabConsumer = new FabConsumer(12345, "test", Arrays.asList(FabConsumer.TOPIC));
        System.out.println("Started");
//        fabConsumer.run();

        RawConsumer rawConsumer = new RawConsumer(12346, "test", Arrays.asList(RawConsumer.TOPIC));
        rawConsumer.run();

//        TopicUtils.createTopic("testttt", 1, 1);
    }
}
