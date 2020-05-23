package xyz.kingsword.course.controller;


import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @RequestMapping("/login")
    public String login(String username, String password) {
        boolean f1 = StrUtil.equals(username, "admin");
        boolean f2 = StrUtil.equals(password, "54648848abc");
        if (f1 && f2) {
            return "monitor";
        }
        return "login";
    }
}
