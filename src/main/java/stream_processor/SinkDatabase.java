package stream_processor;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import message_stream.FabTranslatedEvent;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;

public class SinkDatabase {
    private String DATABASE_NAME = "connect";
    private String COLLECTION_NAME = "events";

    private MongoClient mongoClient;
    private MongoDatabase database;

    public SinkDatabase() {
        this.mongoClient = new MongoClient("localhost", 27017);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
    }


    public List<FabTranslatedEvent> getEventsByTool(String equipID) {
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        LinkedList<FabTranslatedEvent> result = new LinkedList<>();

        // Where clause of the query.
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("equipID", equipID);


        MongoCursor<Document> cursor = collection.find(searchQuery).iterator();
        try {
            while (cursor.hasNext()) {
                Document item = cursor.next();

                FabTranslatedEvent fabTranslatedEvent = new FabTranslatedEvent();
                fabTranslatedEvent.setEquipID(item.getString("equipID"));
                fabTranslatedEvent.setEquipName(item.getString("equipName"));
                fabTranslatedEvent.setRecipeID(item.getString("recipeID"));
                fabTranslatedEvent.setRecipeName(item.getString("recipeName"));
                fabTranslatedEvent.setStepID(item.getString("stepID"));
                fabTranslatedEvent.setStepName(item.getString("stepName"));
                fabTranslatedEvent.setHoldType(item.getString("holdType"));
                fabTranslatedEvent.setHoldFlag(item.getBoolean("holdFlag"));
                fabTranslatedEvent.setDateTime(item.getLong("dateTime"));

                result.add(fabTranslatedEvent);
            }
        } finally {
            cursor.close();
        }
        return result;

    }
}
