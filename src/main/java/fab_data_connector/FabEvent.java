package fab_data_connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FabEvent {
    //    private int PK_ID;

    private Long equipID;
    private Long recipeID;
    private Long stepID;
    private String holdType;
    private boolean holdFlag;
    private Date dateTime;

    // Don't remove, required by jackson.
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = new Date(dateTime);
    }


    @JsonSetter("payload")
    public void unwrap(Map<String, Object> payload) {
        HashMap<String, Object> after = (HashMap<String, Object>) payload.get("after");

        this.setEquipID(Long.parseLong((String) after.get("equip"), 16));
        this.setRecipeID(Long.parseLong((String) after.get("recipe"), 16));
        this.setStepID(Long.parseLong((String) after.get("step"), 16));
        this.setHoldType((String) after.get("holdtype"));
        this.setHoldFlag(Boolean.parseBoolean(String.valueOf(after.get("holdflag"))));
        this.setDateTime(Integer.parseInt(String.valueOf(after.get("datetime"))));
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
