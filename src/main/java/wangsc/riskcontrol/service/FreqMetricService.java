package wangsc.riskcontrol.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.config.FreqMetricConfig;
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.mapper.RedisDao;

/**
 * 频率指标计算服务
 */
@Service
public class FreqMetricService {

    @Autowired
    private RedisDao redisDao;
    private final DefaultRedisScript script = new DefaultRedisScript(FreqMetricConfig.FREQ_OPERATION, Long.class);

    public void getAllFreqMetric(FreqEvent event){
        long periods[] = {};
        String[] dimentions = getAllDimensions(event);
        for(long period : periods){
            long res = getOneFreqMetric("", event.getOperateTime(), period);
        }


    }

    public long getOneFreqMetric(String key, long timestamp, long period){
        Long res = (Long) redisDao.executeLuaScript(script, key, timestamp, timestamp - period, timestamp);
        return (long) res;
    }

    private String[] getAllDimensions(FreqEvent event){
        List<String> list = new ArrayList<>();
        boolean ipEmpty = StringUtils.isEmpty(event.getIP());
        boolean sessionIDEmpty = StringUtils.isEmpty(event.getSessionID());
        boolean phoneEmpty = StringUtils.isEmpty(event.getPhone());
        boolean operateTypeEmpty = StringUtils.isEmpty(event.getOperateType());

        if(!ipEmpty){
            list.add(event.getIP()+"::");
        }
        if(!sessionIDEmpty){
            list.add(event.getIP());
        }
        if(!operateTypeEmpty){

        }


        return null;
    }
}
