package stream_processor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import message_stream.FabTranslatedEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JSONDateSerializer extends JsonSerializer<FabTranslatedEvent> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");


    @Override
    public void serialize(FabTranslatedEvent fabTranslatedEvent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String formattedDate = dateFormat.format(fabTranslatedEvent.getDateTime());
        jsonGenerator.writeString(formattedDate);
    }
}
