package smart2g.dyx.com.a2gplug;

import org.json.JSONException;
import org.json.JSONObject;

public class TimerInstance {

    private String Status;

    private String STime;

    private String ETime;

    private String Mode;

    private int UserDaySet;

    private String SwitchAction;

    private int Index;

    public TimerInstance(String Status , String STime , String ETime , String Mode , int UserDaySet , String SwitchAction , int Index){
        this.Status = Status;
        this.STime = STime;
        this.ETime =ETime;
        this.Mode = Mode;
        this.UserDaySet = UserDaySet;
        this.SwitchAction = SwitchAction;
        this.Index = Index;
    }

    public String toString(){
        JSONObject object = new JSONObject();
        try {
            object.put("Status" , Status);
            object.put("STime" , STime);
            object.put("ETime" , ETime);
            object.put("Mode" , Mode);
            object.put("UserDaySet" , UserDaySet);
            object.put("SwitchAction" , SwitchAction);
            object.put("Index" , Index);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();

    }

    public String getStatus() {
        return Status;
    }

    public String getSTime() {
        return STime;
    }

    public String getETime() {
        return ETime;
    }

    public String getMode() {
        return Mode;
    }

    public int getUserDaySet() {
        return UserDaySet;
    }

    public String getSwitchAction() {
        return SwitchAction;
    }

    public int getIndex() {
        return Index;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setSTime(String STime) {
        this.STime = STime;
    }

    public void setETime(String ETime) {
        this.ETime = ETime;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public void setUserDaySet(int userDaySet) {
        UserDaySet = userDaySet;
    }

    public void setSwitchAction(String switchAction) {
        SwitchAction = switchAction;
    }

    public void setIndex(int index) {
        Index = index;
    }
}
