import fab_data_connector.FabDataStreamer;
import message_stream.StreamProcessor;
import raw_data_connector.RawDataStreamer;

public class Main {
    public static void main(String[] agrs) throws Exception {
        System.out.println("Started");

        try {
            RawDataStreamer rawDataStreamer = new RawDataStreamer(123457, RawDataStreamer.INPUT_TOPIC);
            rawDataStreamer.start();

            FabDataStreamer fabDataStreamer = new FabDataStreamer(123458, FabDataStreamer.INPUT_TOPIC);
            fabDataStreamer.start();

//            Testy testy = new Testy(12367776);
//            testy.start();

            StreamProcessor streamProcessor = new StreamProcessor(123459);
            streamProcessor.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
