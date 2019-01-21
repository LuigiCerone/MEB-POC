package message_stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO Maybe we can box this class with FabEvent.
@JsonIgnoreProperties(ignoreUnknown = true)
public class FabTranslatedEvent {

    private Long equipID;
    private String equipName;
    private Long recipeID;
    private String recipeName;
    private Long stepID;
    private String stepName;
    private String holdType;
    private boolean holdFlag;
    private long dateTime;

    // Don't remove, required by Jackson.
    public FabTranslatedEvent() {
    }

    public FabTranslatedEvent(FabEvent fabEvent) {
        this.equipID = fabEvent.getEquipIDAsLong();
        this.recipeID = fabEvent.getRecipeIDAsLong();
        this.stepID = fabEvent.getStepIDAsLong();
        this.holdType = fabEvent.getHoldType();
        this.holdFlag = fabEvent.isHoldFlag();
        this.dateTime = fabEvent.getDateTime();

        this.equipName = null;
        this.recipeName = null;
        this.stepName = null;
    }

    public String getEquipName() {
        return equipName;
    }

    public void setEquipName(String equipName) {
        this.equipName = equipName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
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

    @Override
    public String toString() {
        return "FabTranslatedEvent{" +
                "equipID=" + equipID +
                ", equipName='" + equipName + '\'' +
                ", recipeID=" + recipeID +
                ", recipeName='" + recipeName + '\'' +
                ", stepID=" + stepID +
                ", stepName='" + stepName + '\'' +
                ", holdType='" + holdType + '\'' +
                ", holdFlag=" + holdFlag +
                ", dateTime=" + dateTime +
                '}';
    }


    public boolean isTranslated() {
        if (equipName != null && recipeName != null && stepName != null)
            return true;
        else return false;
    }
}
