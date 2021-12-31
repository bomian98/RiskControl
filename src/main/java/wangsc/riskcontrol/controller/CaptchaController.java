package wangsc.riskcontrol.controller;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wangsc.riskcontrol.entity.ResBody;
import wangsc.riskcontrol.service.CaptchaService;

@RestController
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @ResponseBody
    @GetMapping("/getCaptchaNum")
    public String getCaptchaNum(String phone) {
        String code = captchaService.genNumCode(phone);
        return code;
    }

    // TODO: 2021/12/31 日后增加验证码通过后降低风险敏感度的问题

    @PostMapping("/verifyCaptchaNum")
    public ResBody verifyCaptchaNum(HttpServletRequest request, String phone, String code) {
        ResBody resBody = captchaService.verifyNumCode(phone, code);
        // 设置转发，跳转到填写用户名和密码页面
        // TODO: 2021/12/31 还没有测试转发功能是否成功
        request.getRequestDispatcher("useraccount.html");
        return resBody;
    }

    @ResponseBody
    @GetMapping("/getCaptchaImg")
    public void getCaptchaImg(HttpServletResponse response, HttpSession session) {
        try {
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "non-cache");
            response.setHeader("Pragma", "non-cache");
            response.setDateHeader("Expire", 0);
            String sessionId = session.getId();
            BufferedImage image = captchaService.genPicCode(sessionId);
            ImageIO.write(image, "PNG", response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/verifyCaptchaImg")
    public ResBody verifyCaptchaImg(HttpSession session, String code) {
        ResBody resBody = captchaService.verifyPicCode(session.getId(), code);
        return resBody;
    }
}
