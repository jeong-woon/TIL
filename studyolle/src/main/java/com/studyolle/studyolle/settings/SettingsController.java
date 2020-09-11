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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String updateProfile(
            @CurrentUser Account account
            , @Valid Profile profile
            // profile로 바인딩 받을때 null포인트 익셉션 난다.
            // 왜냐? 프로필 안에 생성자 하나 만들어서 기본 생성자 없음.
            // 이때 스프링mvc가 모델에트리뷰트로 받을때 인스턴스를 먼저 만들고 세터로 주입하려고 시도하는데,
            // 기본 생성자가 없어서 있는걸로 쓸려고 하는데 이때 어카운트 없음.
            // @NoArgs~ 롬복 에노테이션 쓰던지, 기본 생성자 만들어 주던지.
            , Errors errors
            , Model model
            , RedirectAttributes attributes) {
        // 폼에서 입력한 값들은 ModelAttribute 로 받아서 profile로 처리할꺼고,
        // 바인딩 에러들은 바로 오른쪽의 errors로 받을꺼다.
        // 꼭!! 이렇게 짝을 지어서 써야한다.
        // errors는 바인딩 받는 객체(profile) 뒤(오른쪽)에 위치!
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);
        // spring MVC에서 제공하는 기능, 리다이렉트 시키고 한번 쓰고 말 데이터.
        // 받는 쪽에서는 모델에 들어감.
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:/settings/profile";
    }
}