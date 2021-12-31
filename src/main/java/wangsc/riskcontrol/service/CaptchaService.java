package wangsc.riskcontrol.service;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wangsc.riskcontrol.entity.ResBody;
import wangsc.riskcontrol.mapper.RedisDao;
import wangsc.riskcontrol.utils.CaptchaUtil;

@Service
public class CaptchaService {

    @Autowired
    private RedisDao reidsDao;

    /**
     * 验证码的key的形式
     */
    private String genKey(String prefix, String suffix) {
        return prefix + "::captcha::" + suffix;
    }


    /**
     * 生成数字验证码
     */
    public String genNumCode(String phone) {
        int cnt = ThreadLocalRandom.current().nextInt(1000000);
        String key = genKey(phone, "num");
        String value = String.format("%6d", cnt);
        reidsDao.setKeyValue(key, value, 60, TimeUnit.SECONDS);
        System.out.println(value);
        return value;
    }


    /**
     * 生成图像验证码
     */
    public BufferedImage genPicCode(String sessionId) {
        Map<String, Object> map = CaptchaUtil.getRandomCodeImage();
        String code = (String) map.get("code");
        BufferedImage image = (BufferedImage) map.get("image");
        String key = genKey(sessionId, "pic");
        reidsDao.setKeyValue(key, code.toLowerCase(), 120, TimeUnit.SECONDS);
        return image;
    }


    /**
     * 验证数字验证码是否正确
     */
    public ResBody verifyNumCode(String phone, String code) {
        String key = genKey(phone, "num");
        return verifyCode(key, code);
    }

    /**
     * 验证图片验证码是否正确
     */
    public ResBody verifyPicCode(String sessionId, String code){
        String key = genKey(sessionId, "pic");
        return verifyCode(key, code);
    }

    /**
     * 从Redis中读取key对应的验证码，并比较是否正确
     */
    private ResBody verifyCode(String key, String codeGuess){
        ResBody resBody = new ResBody();
        String codeReal = reidsDao.getValue(key);
        if(StringUtils.isEmpty(codeReal)){
            resBody.setWrongMessage("验证码过期");
        }else if(!codeReal.equals(codeGuess)){
            resBody.setWrongMessage("验证码错误");
        }
        return resBody;
    }
}