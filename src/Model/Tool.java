package Model;

public class Tool {
    private String equipOID;
    private String recipeOID;
    private String stepOID;
    private boolean onHold;

    public Tool(String equipOID, String recipeOID, String stepOID) {
        this.equipOID = equipOID;
        this.recipeOID = recipeOID;
        this.stepOID = stepOID;
        this.onHold = false;
    }

    public String getEquipOID() {
        return equipOID;
    }

    public void setEquipOID(String equipOID) {
        this.equipOID = equipOID;
    }

    public String getRecipeOID() {
        return recipeOID;
    }

    public void setRecipeOID(String recipeOID) {
        this.recipeOID = recipeOID;
    }

    public String getStepOID() {
        return stepOID;
    }

    public void setStepOID(String stepOID) {
        this.stepOID = stepOID;
    }

    public boolean isOnHold() {
        return onHold;
    }

    public void setOnHold(boolean onHold) {
        this.onHold = onHold;
    }
}
