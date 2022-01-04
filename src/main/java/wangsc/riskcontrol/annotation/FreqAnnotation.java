package wangsc.riskcontrol.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FreqAnnotation {
    String value() default ""; // 页面的操作属性：注册 or 登录 or 其他
    String[] rulenames() default {"init_rule"};  // 规则名称

    /**
     * 同一个IP地址/手机号，短期内多次注册(可能是破解手机号或者注册n个账户)
     * 同一个IP地址/手机号/用户名，短期内多次登录(可能是爬虫,破解密码) - 限制IP，限制手机号，限制用户名
     * 同一个IP地址/用户，短期内大量访问页面
     *
     * 多次验证码出错(可能有问题)
     * 多次验证码过期(可能网太卡，不一定算异常)
     *
     * 同一手机号的非常多次注册注销(企图利用注册优惠)
     *
     * 同一账号的非常多不同IP的登录请求(共享账号问题，但也可能是VPN等问题)
     *
     * 多次点击发送短信验证码(不良人员对客户的恶意短信轰炸) 每一分钟发送一次短信验证码？ - 不太重要
     */
}
