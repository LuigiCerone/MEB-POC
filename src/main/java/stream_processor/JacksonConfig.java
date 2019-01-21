package stream_processor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import message_stream.FabTranslatedEvent;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private ObjectMapper objectMapper;

    public JacksonConfig() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MyModule", new Version(1, 0, 0, null));
        module.addSerializer(FabTranslatedEvent.class, new JsonSerializer<FabTranslatedEvent>() {
            @Override
            public void serialize(FabTranslatedEvent fabTranslatedEvent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            }
        });

        objectMapper.registerModule(module);
    }

    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }

}