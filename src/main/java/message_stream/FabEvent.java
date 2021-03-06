package message_stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;

public class FabEvent {
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

    public String getEquipID() {
        return Long.toHexString(equipID).toUpperCase();
    }

    public void setEquipID(String equipID) {
        this.equipID = Long.parseLong((String) equipID, 16);
    }

    public String getRecipeID() {
        return Long.toHexString(recipeID).toUpperCase();
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = Long.parseLong(String.valueOf(recipeID), 16);
    }

    public String getStepID() {
        return Long.toHexString(stepID).toUpperCase();
    }

    public void setStepID(String stepID) {
        this.stepID = Long.parseLong(String.valueOf(stepID), 16);
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

    public Long getEquipIDAsLong() {
        return this.equipID;
    }

    public Long getRecipeIDAsLong() {
        return this.recipeID;
    }

    public Long getStepIDAsLong() {
        return this.stepID;
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
