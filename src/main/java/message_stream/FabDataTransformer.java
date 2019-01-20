package message_stream;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabDataTransformer implements Transformer<String, FabEvent, KeyValue<String, FabEvent>> {
    final static Logger logger = LoggerFactory.getLogger(FabDataTransformer.class);

    private KeyValueStore<String, RawEvent> eqipState;
    private KeyValueStore<String, RawEvent> recipeState;
    private KeyValueStore<String, RawEvent> stepState;

    @Override
    public void init(ProcessorContext processorContext) {
        this.eqipState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.EQUIP_TRANSLATION_STATE);
//                        KeyValueIterator<String, RawEvent> iter = this.eqipState.all();

//                        while (iter.hasNext()) {
//                            KeyValue<String, RawEvent> entry = iter.next();
////                            context.forward(entry.key, entry.value.toString());
//                        }
        this.recipeState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.RECIPE_TRANSLATION_STATE);
        this.stepState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.STEP_TRANSLATION_STATE);
    }

    @Override
    public KeyValue<String, FabEvent> transform(String s, FabEvent fabEvent) {
        System.out.println(fabEvent.toString());
        return KeyValue.pair(s, fabEvent);
    }

    @Override
    public void close() {

    }
}
