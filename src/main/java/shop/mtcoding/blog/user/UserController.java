package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog._core.util.Script;


@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어줌
@Controller
public class UserController {

    // 자바는 final 변수는 반드시 초기화가 되어야함.
    private final UserRepository userRepository;
    private final HttpSession session;

    // 왜 조회인데 post임? 민간함 정보는 body로 보낸다.
    // 로그인만 예외로 select인데 post 사용
    // select * from user_tb where username=? and password=?
@PostMapping("/login")
public String login(UserRequest.LoginDTO requestDTO){
    System.out.println(requestDTO); // toString -> @Data

    if(requestDTO.getUsername().length() < 3){
        throw new RuntimeException("유저 네임 길이가 너무 짧아요");
    }

    User user = userRepository.findByUsername(requestDTO.getUsername());

    //! = 패스워드 검증 실패 시
    // id가 없는 것도 따로 날릴 수 있고, password가 안 맞는 것도 따로 날릴 수 있따
    if (!BCrypt.checkpw(requestDTO.getPassword(), user.getPassword())) { //순수한 패스워드, db에 있는 유저 패스워드
        throw new RuntimeException("패스워드가 틀렸습니다");  //원래는 'ID나 PASSWORD가 틀렸습니다' 라고만 뜨겠지
    }
    session.setAttribute("sessionUser", user); // 락카에 담음 (StateFul)

    return "redirect:/"; // 컨트롤러가 존재하면 무조건 redirect 외우기
}

    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO){
        System.out.println(requestDTO);

        String rawPassword = requestDTO.getPassword();
        String encPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        //솔트 넣어야됨. 이제 비밀번호에 _jooho에 섞여서 들어감. 이래야 레인보우 테이블한테 안털림
        requestDTO.setPassword(encPassword);

        //ssar을 조회해보고 있으면 회원가입 x, 없으면 회원가입 o

        try {
            userRepository.save(requestDTO); // 모델에 위임하기
        } catch (Exception e) {
            throw new RuntimeException("아이디가 중복되었어요");
        }
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
    session.invalidate();
        return "redirect:/";
    }
}
