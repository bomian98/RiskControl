package wangsc.riskcontrol.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wangsc.riskcontrol.annotation.FreqAnnotation;
import wangsc.riskcontrol.entity.ResBody;
import wangsc.riskcontrol.service.CaptchaService;
import wangsc.riskcontrol.service.UserService;

@Controller("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;


    /*  用户注册   */
    @FreqAnnotation()
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }


    @FreqAnnotation("register")
    @PostMapping("/register")
    public ResBody userRegister(HttpSession session, String username, String password, String phone) {
        ResBody resBody = userService.userRegister(session, username, password, phone);
        return resBody;
    }

    // TODO: 2021/12/31 注册时增加验证码模块

    /*  用户登录   */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @FreqAnnotation("login")
    @ResponseBody
    @PostMapping("/loginByPassword")
    public ResBody userLoginByPassword(HttpSession session, String username, String password) {
        ResBody resBody = userService.userLoginByUsernameAndPassword(session, username, password);
        return resBody;
    }


    @FreqAnnotation("login")
    @ResponseBody
    @PostMapping("/loginByPhone")
    public ResBody userLoginByPhone(HttpSession session, String phone, String code) {
        ResBody resBody = captchaService.verifyNumCode(phone, code);

        try {
            captchaService.verifyNumCode(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resBody= userService.userLoginByPhone(session, phone);
        return resBody;
    }    

    @FreqAnnotation()
    @ResponseBody
    @PostMapping("/logout")
    public String userLogout() {
        return null;
    }

    @FreqAnnotation()
    @ResponseBody
    @PostMapping("/delete")
    public String userDelete() {
        return null;
    }
}
