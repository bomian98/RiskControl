package wangsc.riskcontrol.entity;

import lombok.Data;

@Data
public class FreqEvent {
    String IP;
    String sessionID;
    String username;
    String phone;
    long operateTime;
    String operateType;
    int type;

    @Override
    public String toString() {
        return "FreqEvent{" +
                "IP='" + IP + '\'' +
                ", sessionID='" + sessionID + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", operateTime=" + operateTime +
                ", operateType='" + operateType + '\'' +
                ", type=" + type +
                '}';
    }
}
