package wangsc.riskcontrol.mapper;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

@Repository
public class RedisDao {

    @Autowired
    private RedisTemplate redisTemplate;

    public void setKeyValue(String key, String value, int period, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, period, unit);
    }

    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteKeyValue(String key) {
        redisTemplate.delete(key);
    }

    public void setExpireTime(String key, int period, TimeUnit unit) {
        redisTemplate.expire(key, period, unit);
    }

    public Object executeLuaScript(RedisScript script, String key, long... arg){
        return redisTemplate.execute(script, Arrays.asList(key), arg);
    }
}
