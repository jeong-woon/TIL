package com.studyolle.studyolle.main;

import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(
            @CurrentUser Account account    // @CurrentUser 이런 어노테이션이 있나? 어떻게 쓰는거지?? --> account/CurrentUser.class로 이동!!
            , Model model
    ){
        if(account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    // 스프링 부트 뷰 컨트롤러로 줄일 수 있다.
    @GetMapping("/login")
    public String login(){
        return "login";
    }

}