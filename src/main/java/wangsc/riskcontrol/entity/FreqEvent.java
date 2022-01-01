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
}
