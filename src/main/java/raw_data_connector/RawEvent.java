package raw_data_connector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RawEvent {

    // DB Column names.

    private transient String TABLE_NAME = "analytics";
    private transient String ID = "id";
    private transient String EQUIP_ID = "equipId";
    private transient String EQUIP_NAME = "equipName";
    private transient String RECIPE_ID = "recipeId";
    private transient String RECIPE_NAME = "recipeName";
    private transient String STEP_ID = "stepId";
    private transient String STEP_NAME = "stepName";
    private transient String FAKE_DATA = "fakeData";


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

    @JsonSetter("payload")
    public void unwrap(Map<String, Object> payload) {
        if (payload.get("after") != null) {
            HashMap<String, Object> after = (HashMap<String, Object>) payload.get("after");

            this.setEquipID(Long.parseLong((String) after.get(EQUIP_ID), 16));
            this.setEquipName((String) after.get(EQUIP_NAME));
            this.setRecipeID(Long.parseLong((String) after.get(RECIPE_ID), 16));
            this.setRecipeName((String) after.get(RECIPE_NAME));
            this.setStepID(Long.parseLong((String) after.get(STEP_ID), 16));
            this.setStepName((String) after.get(STEP_NAME));
        }
//        this.setFakeData(Long.parseLong((String) after.get("step"), 16));
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
