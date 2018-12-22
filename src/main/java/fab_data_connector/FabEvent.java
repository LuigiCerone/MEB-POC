package fab_data_connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FabEvent {
    //    private int PK_ID;
    private int equipID;
    private int recipeID;
    private int stepID;
    private String holdType;
    private boolean holdFlag;
    private Date dateTime;

    public FabEvent(int equipID, int recipeID, int stepID, String holdType, boolean holdFlag, Date dateTime) {
        this.equipID = equipID;
        this.recipeID = recipeID;
        this.stepID = stepID;
        this.holdType = holdType;
        this.holdFlag = holdFlag;
        this.dateTime = dateTime;
    }

    // Don't remove, required by jackson.
    public FabEvent() {
    }

    public int getEquipID() {
        return equipID;
    }

    public void setEquipID(int equipID) {
        this.equipID = equipID;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public int getStepID() {
        return stepID;
    }

    public void setStepID(int stepID) {
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

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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
