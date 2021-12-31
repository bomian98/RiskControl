package wangsc.riskcontrol.entity;

import lombok.Data;

@Data
public class ResBody {

    private boolean status;
    private String message;

    public ResBody() {
        status = true;
        message = "success";
    }
    public void setWrongMessage(String mess){
        status = false;
        message = mess;
    }
}