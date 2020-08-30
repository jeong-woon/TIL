package com.studyolle.studyolle.settings;

import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.CurrentUser;
import com.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        // return 구문 생략하고, 리턴 타입 void로 하면 뷰 네임 트랜스레이터가 url 네임이랑 같겠지 하고 추측해서 해주는데, 혹시 모르니까 생략 하지 말자.
        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model) {
        // 폼에서 입력한 값들은 ModelAttribute 로 받아서 profile로 처리할꺼고, 바인딩 에러들은 바로 오른쪽의 errors로 받을꺼다. 꼭 이렇게 짝을 지어서 써야한다. errors가 받는 객체 오른쪽에 위치!
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);

        return "redirect:/settings/profile";
    }
}