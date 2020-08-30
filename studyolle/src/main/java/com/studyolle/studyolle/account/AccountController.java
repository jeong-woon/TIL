package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    //signUpForm 이라는 '타입'의 데이터를 받을때 validator 실행할 수 있다. 이름이 아니라 타입이라는 것이 중요.
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    /**
     * 회원가입 화면
     * @param model
     * @return
     */
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        // model.addAttribute("signUpForm", new SignUpForm());
        // 아래처럼 생략 가능. 클래스이름의 캐멀 케이스로 자동 맵핑 해준다.
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(
            @Valid  SignUpForm signUpForm // @Valid @ModelAttribute SignUpForm signUpForm 복합 객체vo, 또는 dto 형태의 복합객체를 받을때는 @ModelAttribute써야 하지만 생략 가능
            , Errors errors) {
        if(errors.hasErrors()) {
            return "account/sign-up";
        }
        // 커스텀 벨리데이터 이렇게 써도 되고,
//        signUpFormValidator.validate(signUpForm, errors);
//
//        if(errors.hasErrors()) {
//            return "account/sign-up";
//        }
        Account account = accountService.processNewAccount(signUpForm);
        accountService.autoLogin(account);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){
        // 리파지토리를 도메인계층으로 보느냐, 레이어드, 컨트롤러 서비스 다오 이런식으로 볼꺼냐에 따라서 컨트롤러에서 repository에 바로 접근 하나 마나에 대한 논쟁??
        // 여기서는 도메인계층으로 봄. 즉, 어카운트와 같은 레벨로 봄. 어카운트를 참조하듯 여러곳에서 참조해도 된다고 가정.
        // 반대로, 서비스나 컨트롤러를 리파지토리나 도메인 엔티티에서 참조하지 않겠다.
        // 여기 이 말자체가 뭔말인지 모르겟다.
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if(!account.isValidToken(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }
        accountService.completedSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(
            @CurrentUser Account account
            , Model model
    ){
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(
            @CurrentUser Account account
            , Model model
    ){
        if(!account.isOkSendConfirmMail()) {
            model.addAttribute("error", "인증메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String  nickname, Model model, @CurrentUser Account account) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if(byNickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

//        model.addAttribute("account", byNickname); 이렇게 들어감, 생략하면 '타입'의 캐멀케이스로 들어감.
        model.addAttribute(byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "account/profile";
    }
}