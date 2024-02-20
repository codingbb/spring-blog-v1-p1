package shop.mtcoding.blog._core.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog._core.util.Script;


//응답 에러 컨트롤러 (view == 파일 리턴)
@ControllerAdvice   //이제 모든 throw를 애가 처리함!
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)  //상세 에러 적어주자 //에러 중에서도 모든 에러를 다 받음
    public @ResponseBody String error1(Exception e) {
        return Script.back(e.getMessage());
    }
}
