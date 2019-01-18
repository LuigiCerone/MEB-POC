import raw_data_connector.RawDataStreamer;

public class Main {
    public static void main(String[] agrs) throws Exception {
        System.out.println("Started");

        RawDataStreamer rawDataStreamer = new RawDataStreamer(123457, RawDataStreamer.INPUT_TOPIC);
        rawDataStreamer.start();

//        FabDataStreamer fabDataStreamer = new FabDataStreamer(123458, FabDataStreamer.INPUT_TOPIC);
//        fabDataStreamer.start();
    }
}
