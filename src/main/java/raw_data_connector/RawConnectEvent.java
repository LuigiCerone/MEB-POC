package raw_data_connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.sun.deploy.security.ValidationState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RawEvent {

    // DB Column names.

    private transient String TABLE_NAME = "analytics";
    private transient String ID = "id";
    private transient String OID = "oid";
    private transient String NAME_TRANSLATION = "nameTranslation";
    private transient String TYPE = "type";
    private transient String FAKE_DATA = "fakeData";


    //    private int PK_ID;

    private Long oid;
    private String nameTranslation;
    private int type;
    private char[] fakeData;

    // Don't remove, required by Jackson.
    public RawEvent() {
    }

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public String getNameTranslation() {
        return nameTranslation;
    }

    public void setNameTranslation(String nameTranslation) {
        this.nameTranslation = nameTranslation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public char[] getFakeData() {
        return fakeData;
    }

    public void setFakeData(char[] fakeData) {
        this.fakeData = fakeData;
    }

    @JsonSetter("payload")
    public void unwrap(Map<String, Object> payload) {
        if (payload.get("after") != null) {
            HashMap<String, Object> after = (HashMap<String, Object>) payload.get("after");

            this.setOid(Long.parseLong((String) after.get(OID), 16));
            this.setNameTranslation((String) after.get(NAME_TRANSLATION));
            this.setType(Integer.parseInt(String.valueOf(after.get(TYPE))));
        }
//        this.setFakeData(Long.parseLong((String) after.get("step"), 16));
    }

    @Override
    public String toString() {
        return "RawEvent{" +
                "oid=" + oid +
                ", nameTranslation='" + nameTranslation + '\'' +
                ", type=" + type +
                ", fakeData=" + Arrays.toString(fakeData) +
                '}';
    }
}
