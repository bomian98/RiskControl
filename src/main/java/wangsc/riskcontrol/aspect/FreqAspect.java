package wangsc.riskcontrol.aspect;

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
import wangsc.riskcontrol.entity.FreqEvent;
import wangsc.riskcontrol.entity.User;
import wangsc.riskcontrol.service.UserService;
import wangsc.riskcontrol.utils.IPUtil;

@Aspect
@Component
@Order(1)
public class FreqAspect {
    @Autowired
    private UserService userService;

    @Pointcut("@annotation(wangsc.riskcontrol.annotation.FreqAnnotation)")
    public void freqPointCut(){}

    @Around("freqPointCut() && @annotation(anno)")
    public Object doAround(ProceedingJoinPoint pjp, FreqAnnotation anno) throws Throwable {
        String operateType = anno.value(); // 获取页面的操作属性：注册 or 登录 or 其他
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String Ip = IPUtil.getIpAddr(request);
        HttpSession session = request.getSession();
        User user = userService.getUserBySessionIDInRedis(session);
        String sessionID = session.getId();

        FreqEvent event = new FreqEvent();
        event.setIP(Ip);
        event.setSessionID(sessionID);
        event.setOperateTime(new Date().getTime());
        event.setOperateType(operateType);
        if(user != null){
            event.setPhone(user.getPhone());
            event.setUsername(user.getUsername());
        }



        Object[] args = pjp.getArgs();

        // 如果访问频率较低，则直接执行
        Object obj = pjp.proceed(args);
        // Object obj = pjp.proceed(args); 需要增加参数吗？还是默认就有
//        return obj;

        return null;
    }

    private int judgeStatus(String Ip, String operation){
        return 0;
    }

}
