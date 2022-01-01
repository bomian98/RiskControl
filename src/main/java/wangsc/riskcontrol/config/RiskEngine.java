package wangsc.riskcontrol.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.entity.ResBody;
import wangsc.riskcontrol.service.FreqMetricService;

/**
 * 规则引擎服务
 */
@Service
public class RiskEngine {

    @Autowired
    private FreqMetricService freqMetricService; // 指标计算服务
    private Set<String> attrNames; // 存储所有操作涉及到的事件属性
    private List<Operation> ops; // 存储需要进行判断的操作
    private Map<String, Long> attrValues; // 存储事件属性对应的属性值


    public RiskEngine() {
        attrNames = new HashSet<>();
        ops = new ArrayList<>();
    }

    /** 根据所有的属性名，尝试获得对应属性，以此来检查该规则是否可行 */
    public RiskEngine build() {
        FreqEvent event = new FreqEvent();
        try {
            for (String attrName : attrNames) {
                event.getClass().getDeclaredField(attrName);
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
        return this;
    }

    /** 执行函数。执行配置的所有比较操作，判断结果。*/
    public ResBody execute(FreqEvent event) {
        ResBody resBody = new ResBody();
        try {
            for (Operation op : ops) {
                if (!op.doIt(event)) {
                    resBody.setWrongMessage("风控警报，访问频率超过当前限额");
                    return resBody;
                }
            }
        } catch (NoSuchFieldException e) {
            resBody.setWrongMessage("属性值出错，请检查当前规则配置情况");
        }
        return resBody;
    }

    /** 生成 redis-key */
    public String genKey(String attrName, String attrValue) {
        return new StringBuilder().append("::").append(attrName).append("::").append(attrValue)
                .toString();
    }

    /**
     * 使用 acquireEventValue 获得属性值
     * 根据属性值和名称，调用 genKey 生成 redis-key
     * 调用指标计算服务freqMetricService，获得当前属性给定时间段内的访问频率 freq
     * 将 freq 与 limit 进行比较
     */
    public RiskEngine lt(String attrName, long period, long limit) {
        attrNames.add(attrName);
        Operation op = (event) -> {
            String attrValue = acquireEventValue(event, attrName);
            String key = genKey(attrName, attrValue);
            long res = freqMetricService.getOneFreqMetric(key, event.getOperateTime(), period);
            return res < limit;
        };
        ops.add(op);
        return this;
    }

    public RiskEngine gt(String attrName, long period, long limit) {
        attrNames.add(attrName);
        Operation op = (event) -> {
            String attrValue = acquireEventValue(event, attrName);
            String key = genKey(attrName, attrValue);
            long res = freqMetricService.getOneFreqMetric(key, event.getOperateTime(), period);
            return res < limit;
        };
        ops.add(op);
        return this;
    }


    /** 使用反射的方法，获得Event事件某个属性的属性值*/
    public String acquireEventValue(FreqEvent event, String attrName) throws NoSuchFieldException {
        Field field = event.getClass().getDeclaredField(attrName);
        field.setAccessible(true);
        try {
            return (String) field.get(event);
        } catch (IllegalAccessException e) {
            return "";
        }
    }

    /**
     * 操作接口，根据 Event 事件的状况，获得当前事件对应的访问频率是否超过限额
     */
    @FunctionalInterface
    public interface Operation {
        boolean doIt(FreqEvent event) throws NoSuchFieldException;
    }
}
