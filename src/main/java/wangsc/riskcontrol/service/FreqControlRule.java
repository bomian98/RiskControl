package wangsc.riskcontrol.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wangsc.riskcontrol.entity.ControlResult;
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.entity.Impl.FreqControlResult;

/**
 * 频率控制规则
 * 使用设计模式中的建造者模式来创建频率控制规则
 * 通过内置的gt、lt等函数增加比较规则
 * 通过build方法验证是否构建成功
 */

public class FreqControlRule {
    private FreqMetricService freqMetricService; // 指标计算服务
    private Set<String> attrNames; // 存储所有操作涉及到的事件属性
    private List<Operation> ops; // 存储需要进行判断的操作
    private Map<String, String> attrValues; // 存储事件属性对应的属性值

    public FreqControlRule(FreqMetricService freqMetricService) {
        attrNames = new HashSet<>();
        ops = new ArrayList<>();
        attrValues = new HashMap<>();
        this.freqMetricService = freqMetricService;
    }

    /** 频率控制规则构建函数。遍历所有需要检查的维度属性，判断事件中是否包含对应属性 */
    public FreqControlRule build() {
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


    /** 使用反射的方法，获得Event事件某个维度属性的属性值 */
    private String acquireEventValue(FreqEvent event, String attrName) throws NoSuchFieldException {
        Field field = event.getClass().getDeclaredField(attrName);
        field.setAccessible(true);
        try {
            return (String) field.get(event);
        } catch (IllegalAccessException e) {
            return "";
        }
    }


    /**
     * 执行函数。执行配置的所有比较操作，判断风险等级
     * 1. 首先获取待操作的所有属性对应的属性值（操作数一定大于等于操作属性数，提前获取降低反射次数）
     * 2. 执行设定的多个比较规则
     * 3. 返回频率控制结果
     */
    public ControlResult execute(FreqEvent event) {
        ControlResult freqControlResult = new FreqControlResult();
        try {
            for (String attrName : attrNames) {
                String attrValue = acquireEventValue(event, attrName);
                attrValues.put(attrName, attrValue);
            }
        } catch (NoSuchFieldException e) {
            freqControlResult.setNoSuchFieldMessage();
            return freqControlResult;
        }
        for (Operation op : ops) {
            int res = op.getLevel(event);
            freqControlResult.setRiskLevel(res);
        }
        return freqControlResult;
    }


    /** 获取某个事件的某个维度属性在给定时间范围内的访问频率指标 */
    private long getOneFreqMetric(FreqEvent event, String attrName, long period) {
        String attrValue = attrValues.get(attrName);
        String key = new StringBuilder()
                .append("::").append(attrName)
                .append("::").append(attrValue)
                .toString();
        return freqMetricService.getOneFreqMetric(key, event.getOperateTime(), period);
    }


    /** 增加判断事件 event 对应的维度属性 attrName 在 period 时间段内访问频率，是否小于 limit  */
    public FreqControlRule lt(String attrName, long period, long limit) {
        Operation op = (event) -> getOneFreqMetric(event, attrName, period) < limit ? 0 : 1;
        attrNames.add(attrName);
        ops.add(op);
        return this;
    }

    /** 增加判断事件 event 对应的维度属性 attrName 在 period 时间段内访问频率，是否大于 limit  */
    public FreqControlRule gt(String attrName, long period, long limit) {
        Operation op = (event) -> getOneFreqMetric(event, attrName, period) > limit ? 0 : 1;
        attrNames.add(attrName);
        ops.add(op);
        return this;
    }

    /** 一系列阈值，获得危险等级  */
    public FreqControlRule gt(String attrName, long period, long[] limits) {
        attrNames.add(attrName);
        Operation op = (event) -> {
            long freq = getOneFreqMetric(event, attrName, period);
            int len = limits.length;
            for (int i = len - 1; i >= 0; i--) {
                if (freq > limits[i]) {
                    return i + 1;
                }
            }
            return 0;
        };
        ops.add(op);
        return this;
    }

    // TODO: 2022/1/4 规则的保存和读取
    /**
     * 规则的保存和读取，将规则保存到数据库 or 文件中，方便之后直接使用
     * 1. Serializable 序列化和反序列化 - 还不了解
     * 2. 自己实现序列化和反序列化
     *
     * 当前的问题：
     * 1. freqMetricService 是自动装配的，序列化的时候该怎么处理？
     *
     */
    /** 规则的保存 */
    public void saveToFile(){
    }

    /** 规则的读取 */
    public FreqControlRule readFromFile(){
        return this;
    }


    /** 函数式接口
     * 操作接口，根据 Event 事件的状况，获得当前事件对应的访问频率是否超过限额
     */
    @FunctionalInterface
    public interface Operation {
        int getLevel(FreqEvent event);
    }
}
