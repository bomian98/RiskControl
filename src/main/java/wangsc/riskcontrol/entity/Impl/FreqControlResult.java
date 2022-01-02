package wangsc.riskcontrol.entity.Impl;

import lombok.Data;
import wangsc.riskcontrol.entity.ControlResult;

@Data
public class FreqControlResult extends ControlResult {

    public FreqControlResult() {
        this.level = 0;
        message = "success";
    }

    public void setWarningMessage(String mess){
        level = 1;
        message = mess;
    }

    @Override
    public void setRiskLevel(int level){
        if(level == 0) return;
        super.setRiskLevel(level);
        this.message = "风控警报，访问频率超过当前限额";
    }

    @Override
    public String toString() {
        return "FreqControlResult{" +
                "level=" + level +
                ", message='" + message + '\'' +
                '}';
    }
}
