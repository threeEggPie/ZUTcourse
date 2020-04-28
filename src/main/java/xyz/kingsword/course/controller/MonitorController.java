package xyz.kingsword.course.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/monitor")
public class MonitorController {
    @Resource(name = "config")
    private Cache cache;

    @RequestMapping("/login")
    public String login(String username, String password) {
        boolean f1 = StrUtil.equals(username, "admin");
        boolean f2 = StrUtil.equals(password, "54648848abc");
        if (f1 && f2) {
            return "monitor";
        }
        return "login";
    }

    @RequestMapping("/setPurchaseSwitch")
    @ResponseBody
    public void setPurchaseSwitch( boolean status) {
        System.out.println(status);
        cache.put("purchaseStatus", status);
        System.out.println(cache.get("purchaseStatus", Boolean.class));
    }

    @RequestMapping("/getPurchaseSwitch")
    @ResponseBody
    public Object getPurchaseSwitch() {
        boolean flag = Optional.ofNullable(cache.get("purchaseStatus", Boolean.class)).orElse(false);
        Dict dict = Dict.create();
        dict.put("status", flag);
        System.out.println(flag);
        return dict;
    }
}
