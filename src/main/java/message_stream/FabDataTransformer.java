package message_stream;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class FabDataTransformer implements Transformer<String, FabEvent, KeyValue<String, FabTranslatedEvent>> {
    final static Logger logger = LoggerFactory.getLogger(FabDataTransformer.class);

    private KeyValueStore<String, RawEvent> eqipState;
    private KeyValueStore<String, RawEvent> recipeState;
    private KeyValueStore<String, RawEvent> stepState;

    private KeyValueStore<String, FabTranslatedEvent> failedTranslationsState;

    private ProcessorContext context;

    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;

        // Here we can access the StateStore of the kafka stream by using the name we gave to it in the StreamProcessor class.
        this.eqipState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.EQUIP_TRANSLATION_STATE);
        this.recipeState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.RECIPE_TRANSLATION_STATE);
        this.stepState = (KeyValueStore<String, RawEvent>) processorContext.getStateStore(StreamProcessor.STEP_TRANSLATION_STATE);

        this.failedTranslationsState = (KeyValueStore<String, FabTranslatedEvent>) processorContext.getStateStore(StreamProcessor.FAILED_TRANSLATION);

        // Schedule a punctuate method.
        this.context.schedule(Duration.ofSeconds(30), PunctuationType.WALL_CLOCK_TIME, new FabTransformerPunctuator());
//        this.context.schedule(Duration.ofSeconds(30), PunctuationType.STREAM_TIME, new FabTransformerPunctuator());

        System.out.println("Init");
    }

    @Override
    public KeyValue<String, FabTranslatedEvent> transform(String key, FabEvent fabEvent) {
        // Here we need to translate the event
        FabTranslatedEvent fabTranslatedEvent = new FabTranslatedEvent(fabEvent);

        tryToTranslate(0, fabTranslatedEvent);
        tryToTranslate(1, fabTranslatedEvent);
        tryToTranslate(2, fabTranslatedEvent);

        if (fabTranslatedEvent.isTranslated())
            return KeyValue.pair(key, fabTranslatedEvent);
        else {
            // Add this untranslated row in the state.
            failedTranslationsState.put(key, fabTranslatedEvent);

            return null;
        }

//        return KeyValue.pair(s, fabTranslatedEvent);

    }

    // type=0 -> equip, type=1 -> recipe, type=2 -> step.
    private void tryToTranslate(int type, FabTranslatedEvent fabTranslatedEvent) {
        KeyValueStore<String, RawEvent> kTable = null;
        String oidToTranslate = null;

        if (type == 0) {
            kTable = this.eqipState;
            oidToTranslate = fabTranslatedEvent.getEquipID();
        } else if (type == 1) {
            kTable = this.recipeState;
            oidToTranslate = fabTranslatedEvent.getRecipeID();
        } else if (type == 2) {
            kTable = this.stepState;
            oidToTranslate = fabTranslatedEvent.getStepID();
        }

        KeyValueIterator<String, RawEvent> iter = kTable.all();

        String nameTranslated = null;
        while (iter.hasNext()) {
            KeyValue<String, RawEvent> entry = iter.next();
            if (entry.value.getOid().toUpperCase().equals(oidToTranslate.toUpperCase())) {
                nameTranslated = entry.value.getNameTranslation();
            }
//                            context.forward(entry.key, entry.value.toString());
        }

        if (nameTranslated != null) {
            if (type == 0) {
                fabTranslatedEvent.setEquipName(nameTranslated);
            } else if (type == 1) {
                fabTranslatedEvent.setRecipeName(nameTranslated);
            } else if (type == 2) {
                fabTranslatedEvent.setStepName(nameTranslated);
            }
        }
    }

    @Override
    public void close() {
        // Empty.
    }

    private class FabTransformerPunctuator implements Punctuator {
        @Override
        public void punctuate(long l) {
            KeyValueIterator<String, FabTranslatedEvent> iter = failedTranslationsState.all();
            while (iter.hasNext()) {
                KeyValue<String, FabTranslatedEvent> entry = iter.next();
                FabTranslatedEvent fabTranslatedEvent = entry.value;
                System.out.println("FabTransformerPunctuator: " + entry.value.toString());

                if (fabTranslatedEvent.getEquipName() == null)
                    tryToTranslate(0, fabTranslatedEvent);

                if (fabTranslatedEvent.getRecipeName() == null)
                    tryToTranslate(1, fabTranslatedEvent);

                if (fabTranslatedEvent.getStepName() == null)
                    tryToTranslate(2, fabTranslatedEvent);

                if (fabTranslatedEvent.isTranslated()) {
                    // Remove it from the state and forward to the output topic.
                    failedTranslationsState.delete(entry.key);
                    context.forward(entry.key, entry.value);
                } else {
                    // Update in the state.
                    failedTranslationsState.put(entry.key, fabTranslatedEvent);
                }
            }
            iter.close();

            // commit the current processing progress
            context.commit();
        }
    }
}
