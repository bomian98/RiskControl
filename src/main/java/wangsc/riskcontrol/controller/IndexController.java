package wangsc.riskcontrol.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import wangsc.riskcontrol.entity.User;
import wangsc.riskcontrol.service.UserService;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    /**
     * 用户信息展示页面 - 根据sessionID查看用户信息
     * @param session
     * @return
     */
    @GetMapping("/user")
    public ModelAndView user(HttpSession session){
        String sessionID = session.getId();
        User user = userService.getUserBySessionIDInRedis(session);
        System.out.println(user);
        if(user == null){
            user = new User();
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("username", user.getUsername());
        mv.addObject("phoneNumber", user.getPhone());
        return mv;
    }

    /**
     * 主界面
     * @return
     */
    @GetMapping("/index")
    public String index(){
        return "index";
    }
}
