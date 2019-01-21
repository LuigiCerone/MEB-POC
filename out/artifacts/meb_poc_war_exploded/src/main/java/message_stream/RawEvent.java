package message_stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RawEvent {
    private Long oid;
    private String nameTranslation;
    private int type;
    private char[] fakeData;

    // Don't remove, required by Jackson.
    public RawEvent() {
    }

    public String getOid() {
        return Long.toHexString(oid).toUpperCase();
    }

    public void setOid(String oid) {
        this.oid = Long.parseLong(String.valueOf(oid), 16);
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

    @Override
    public String toString() {
        return "RawConnectEvent{" +
                "oid=" + oid +
                ", nameTranslation='" + nameTranslation + '\'' +
                ", type=" + type +
                ", fakeData=" + Arrays.toString(fakeData) +
                '}';
    }
}
