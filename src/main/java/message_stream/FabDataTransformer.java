package message_stream;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabDataTransformer implements ValueTransformer<String, FabEvent> {
    final static Logger logger = LoggerFactory.getLogger(FabDataTransformer.class);

    private KeyValueStore<String, RawEvent> eqipState;
    private KeyValueStore<String, RawEvent> recipeState;
    private KeyValueStore<String, RawEvent> stepState;


    @Override
    public void init(ProcessorContext processorContext) {
        this.eqipState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.EQUIP_TRANSLATION_STATE);
        this.recipeState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.RECIPE_TRANSLATION_STATE);
        this.stepState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.STEP_TRANSLATION_STATE);

//        logger.debug("aa" + eqipState);
    }

    @Override
    public FabEvent transform(String s) {
        // can access this.state and use read-only key
//        logger.debug("String: " + s);
        return null;
    }

    @Override
    public void close() {

    }
}
