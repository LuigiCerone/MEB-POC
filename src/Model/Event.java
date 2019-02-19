package Model;

public class Event {
    private Tool tool;
    private String holdType;
    private boolean holdFlag;
    private long dateTime;

    public Event(Tool tool, String holdType, boolean holdFlag, long dateTime) {
        this.tool = tool;
        this.holdType = holdType;
        this.holdFlag = holdFlag;
        this.dateTime = dateTime;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
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
        this.tool.setOnHold(holdFlag);

    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
