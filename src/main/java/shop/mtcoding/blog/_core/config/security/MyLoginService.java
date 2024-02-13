package shop.mtcoding.blog._core.config.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRepository;

//조건 : POST, /login, x-from데이터 일 것. 또한 키 값이 반드시 username, password 일 것
@RequiredArgsConstructor
@Service
public class MyLoginService implements UserDetailsService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("loadUserByUsername : " + username);
        User user = userRepository.findByUsername(username);

        if (user == null) { //null이면 login 실패
            System.out.println("user는 null");
            return null;
        } else {
            System.out.println("찾았어요");
            session.setAttribute("sessionUser", user);  //머스태치에서만 가져오자!!
            return new MyLoginUser(user);   //SecurityContextHolder에 저장
        }

    }
}
