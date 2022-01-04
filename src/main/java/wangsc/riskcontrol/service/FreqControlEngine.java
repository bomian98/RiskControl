package wangsc.riskcontrol.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.entity.ControlResult;
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.entity.Impl.FreqControlResult;
import wangsc.riskcontrol.entity.ResBody;

/**
 * 对频率控制的规则引擎
 * 使用Map的形式存储多个规则，前台可以向其中添加规则，也可以删除规则
 */
@Service
public class FreqControlEngine {

    @Autowired
    private FreqMetricService freqMetricService;
    Map<String, FreqControlRule> ruleMap;

    public FreqControlEngine() {
        ruleMap = new HashMap<>();
    }

    /** 内置一个简单的风控规则 */
    public void init() {
        // period 是毫秒单位
        // 10秒内访问5次、10次、15次
        FreqControlRule rule = new FreqControlRule(freqMetricService)
                .gt("IP", 10 * 1000, new long[]{5, 10, 15})
                .build();
        ruleMap.put("init_rule", rule);
    }

    /** 前台可以添加规则 */
    public void addRule(String ruleName, FreqControlRule rule) {
        ruleMap.put(ruleName, rule);
    }

    /** 前台可以删除规则 */
    public void removeRule(String ruleName) {
        ruleMap.remove(ruleName);
    }

    /** 判断是否出现重名的规则名称 */
    public ResBody judgeRuleExists(String ruleName) {
        ResBody resBody = new ResBody();
        if (ruleMap.containsKey(ruleName)) {
            resBody.setWrongMessage("出现重名的规则，是覆盖现有规则，还是重命名当前规则");
        }
        return resBody;
    }


    /** 规则引擎选择对应规则，然后执行该规则*/
    public ControlResult execute(String ruleName, FreqEvent event) {
        if (!judgeRuleExists(ruleName).isStatus()) {
            FreqControlRule rule = ruleMap.get(ruleName);
            return rule.execute(event);
        } else {
            ControlResult result = new FreqControlResult();
            result.setWarningMessage(ruleName + "对应的规则不存在");
            return result;
        }
    }


    /** 规则引擎选择对应规则，然后执行该规则*/
    public ControlResult execute(String[] ruleNames, FreqEvent event) {
        ControlResult result = new FreqControlResult();
        Set<String> rules_not_exist = new HashSet<>();
        for(String ruleName: ruleNames){
            ControlResult tmp = execute(ruleName, event);
            if(tmp.getLevel() != -1){
                result.setRiskLevel(tmp.getLevel());
            }else{
                rules_not_exist.add(ruleName);
            }
        }
        if(!rules_not_exist.isEmpty() && !result.isRisk()){
            result.setWarningMessage(rules_not_exist+"规则不存在，其余未发现异常");
        }
        System.out.println(result);
        return result;
    }
}
