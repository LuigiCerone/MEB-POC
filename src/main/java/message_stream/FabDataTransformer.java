package message_stream;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabDataTransformer implements Transformer<String, FabEvent, KeyValue<String, FabTranslatedEvent>> {
    final static Logger logger = LoggerFactory.getLogger(FabDataTransformer.class);

    private KeyValueStore<String, RawEvent> eqipState;
    private KeyValueStore<String, RawEvent> recipeState;
    private KeyValueStore<String, RawEvent> stepState;

    @Override
    public void init(ProcessorContext processorContext) {
        // Here we can access the StateStore of the kafka stream by using the name we gave to it in the StreamProcessor class.
        this.eqipState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.EQUIP_TRANSLATION_STATE);
        this.recipeState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.RECIPE_TRANSLATION_STATE);
        this.stepState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.STEP_TRANSLATION_STATE);

        //                        KeyValueIterator<String, RawEvent> iter = this.eqipState.all();

//                        while (iter.hasNext()) {
//                            KeyValue<String, RawEvent> entry = iter.next();
////                            context.forward(entry.key, entry.value.toString());
//                        }
    }

    @Override
    public KeyValue<String, FabTranslatedEvent> transform(String s, FabEvent fabEvent) {
        // Here we need to translate the event
//        System.out.println(fabEvent.toString());
        FabTranslatedEvent fabTranslatedEvent = new FabTranslatedEvent(fabEvent);
        System.out.println(fabTranslatedEvent);


//        if (fabTranslatedEvent.isTranslated())
//            return KeyValue.pair(s, fabTranslatedEvent);
//        else return null;

        return KeyValue.pair(s, fabTranslatedEvent);

    }

    @Override
    public void close() {
        // Empty.
    }
}
