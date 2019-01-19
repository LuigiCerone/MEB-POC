package fab_data_connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FabEvent {
    // DB Column names.
    private final String TABLE_NAME = "event";
    private final String ID = "id";
    private final String EQUIP_ID = "equip";
    private final String RECIPE_ID = "recipe";
    private final String STEP_ID = "step";
    private final String HOLD_TYPE = "holdtype";
    private final String HOLD_FLAG = "holdflag";
    private final String DATE_TIME = "datetime";


    //    private int PK_ID;

    private Long equipID;
    private Long recipeID;
    private Long stepID;
    private String holdType;
    private boolean holdFlag;
    private long dateTime;

    // Don't remove, required by Jackson.
    public FabEvent() {
    }

    public Long getEquipID() {
        return equipID;
    }

    public void setEquipID(Long equipID) {
        this.equipID = equipID;
    }

    public Long getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(Long recipeID) {
        this.recipeID = recipeID;
    }

    public Long getStepID() {
        return stepID;
    }

    public void setStepID(Long stepID) {
        this.stepID = stepID;
    }

    public String getHoldType() {
        return holdType;
    }

    public void setHoldType(String holdType) {
        this.holdType = holdType;
    }


    public boolean isHoldFlag() {
        return holdFlag;
    }

    public void setHoldFlag(boolean holdFlag) {
        this.holdFlag = holdFlag;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }


    @JsonSetter("payload")
    public void unwrap(Map<String, Object> payload) {
        HashMap<String, Object> after = (HashMap<String, Object>) payload.get("after");

        this.setEquipID(Long.parseLong((String) after.get(EQUIP_ID), 16));
        this.setRecipeID(Long.parseLong((String) after.get(RECIPE_ID), 16));
        this.setStepID(Long.parseLong((String) after.get(RECIPE_ID), 16));
        this.setHoldType((String) after.get(HOLD_TYPE));
        this.setHoldFlag(Boolean.parseBoolean(String.valueOf(after.get(HOLD_FLAG))));
        this.setDateTime(Long.parseLong(String.valueOf(after.get(DATE_TIME))));
    }

    @Override
    public String toString() {
        return "FabEvent{" +
                "equipID=" + equipID +
                ", recipeID=" + recipeID +
                ", stepID=" + stepID +
                ", holdType='" + holdType + '\'' +
                ", holdFlag=" + holdFlag +
                ", dateTime=" + dateTime +
                '}';
    }
}
