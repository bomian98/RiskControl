package wangsc.riskcontrol.aspect;

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import wangsc.riskcontrol.annotation.FreqAnnotation;
import wangsc.riskcontrol.entity.ControlResult;
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.entity.User;
import wangsc.riskcontrol.service.FreqControlEngine;
import wangsc.riskcontrol.service.UserService;
import wangsc.riskcontrol.utils.IPUtil;

@Aspect
@Component
@Order(1)
public class FreqAspect {
    @Autowired
    private FreqControlEngine engine;
    @Autowired
    private UserService userService;

    @Pointcut("@annotation(wangsc.riskcontrol.annotation.FreqAnnotation)")
    public void freqPointCut(){}

    @Around("freqPointCut() && @annotation(anno)")
    public Object doAround(ProceedingJoinPoint pjp, FreqAnnotation anno) throws Throwable {
        engine.init();

        // 获取事件属性
        String operateType = anno.value(); // 获取页面的操作属性：注册 or 登录 or 其他
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String Ip = IPUtil.getIpAddr(request);
        HttpSession session = request.getSession();
        User user = userService.getUserBySessionIDInRedis(session);
        String sessionID = session.getId();

        // 生成事件对象
        // 之后重新写事件对象，转换为map的形式存储
        FreqEvent event = new FreqEvent();
        event.setIP(Ip);
        event.setSessionID(sessionID);
        event.setOperateTime(System.currentTimeMillis());
        event.setOperateType(operateType);
        if(user != null){
            event.setPhone(user.getPhone());
            event.setUsername(user.getUsername());
        }

        // 调用风控引擎，查看频率高低
        String[] rulenames = anno.rulenames();
        ControlResult result = engine.execute(rulenames, event);
        if(!result.isRisk()){
            // TODO: 2022/1/4 跳转到验证码界面，先进行验证码测试 
            return result;
        }else{
            return pjp.proceed(pjp.getArgs());
        }
    }
}
