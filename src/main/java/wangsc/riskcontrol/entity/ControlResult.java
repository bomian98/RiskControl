package wangsc.riskcontrol.entity;

import lombok.Data;

@Data
public abstract class ControlResult {
    protected int level;
    protected String message;

    public void setNoSuchFieldMessage(){
        level = 1;
        message = "属性值出错，请检查当前规则配置情况";
    }

    public boolean isRisk(){
        return level > 0;
    }

    public void setWarningMessage(String mess){
        level = -1;
        message = mess;
    }

    public void setRiskLevel(int level){
        this.level = Math.max(this.level, level);
    }


}
