import fab_data_connector.FabConsumer;

import java.util.Arrays;

public class Main {
    public static void main(String[] agrs) throws Exception {
        FabConsumer fabConsumer = new FabConsumer(12345, "test", Arrays.asList(FabConsumer.TOPIC));
        System.out.println("Started");
        fabConsumer.run();
    }
}
