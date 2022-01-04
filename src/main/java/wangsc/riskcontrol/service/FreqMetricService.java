package wangsc.riskcontrol.service;

import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.config.FreqMetricConfig;
import wangsc.riskcontrol.mapper.RedisDao;

/**
 * 频率指标计算服务
 */
@Service
public class FreqMetricService {

    @Autowired
    private RedisDao redisDao;
    private final DefaultRedisScript script = new DefaultRedisScript(FreqMetricConfig.FREQ_OPERATION, Long.class);

    private String convertTime(long timestamp){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(timestamp);
    }

    public long getOneFreqMetric(String key, long timestamp, long period){
        String value = convertTime(timestamp);
        String member = String.valueOf(timestamp);
        String minLimit = convertTime(timestamp-period);
        String maxLimit = value;
        Long res = redisDao.executeLuaScript(script, key, value, member, minLimit, maxLimit);
//        System.out.println(res);
        return res;
    }
}
