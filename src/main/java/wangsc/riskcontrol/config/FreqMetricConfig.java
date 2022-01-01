package wangsc.riskcontrol.config;

import org.springframework.context.annotation.Configuration;

/**
 * 频率指标配置类 存储指标计算函数
 */
@Configuration
public class FreqMetricConfig {

    /**
     * 请求后端时，对访问操作进行指标计算
     * 1. 访问页面的时候，将访问数据存储到redis中 2. 计算并返回访问频率
     * args【时间戳，时间范围min，时间范围max】
     */
    // TODO: 2022/1/1 可能不支持输入数字，要tonumber转换为数字比较
    public final static String FREQ_OPERATION = new StringBuilder()
            .append("redis.call('ZADD', KEYS[1], ARGV[1], 1);")
            .append("return redis.call('ZCOUNT', KEYS[1], ARGV[2], ARGV[3]);")
            .toString();

}
