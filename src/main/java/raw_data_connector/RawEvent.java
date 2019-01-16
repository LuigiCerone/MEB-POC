package raw_data_connector;

public class RawEvent {
    //    private int PK_ID;

    private Long equipID;
    private String equipName;
    private Long recipeID;
    private String recipeName;
    private Long stepID;
    private String stepName;
    private char[] fakeData;

    // Don't remove, required by Jackson.
    public RawEvent() {
    }

    public RawEvent(Long equipID, String equipName, Long recipeID, String recipeName, Long stepID, String stepName, char[] fakeData) {
        this.equipID = equipID;
        this.equipName = equipName;
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.stepID = stepID;
        this.stepName = stepName;
        this.fakeData = fakeData;
    }

    public Long getEquipID() {
        return equipID;
    }

    public void setEquipID(Long equipID) {
        this.equipID = equipID;
    }

    public String getEquipName() {
        return equipName;
    }

    public void setEquipName(String equipName) {
        this.equipName = equipName;
    }

    public Long getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(Long recipeID) {
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Long getStepID() {
        return stepID;
    }

    public void setStepID(Long stepID) {
        this.stepID = stepID;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public char[] getFakeData() {
        return fakeData;
    }

    public void setFakeData(char[] fakeData) {
        this.fakeData = fakeData;
    }
}
