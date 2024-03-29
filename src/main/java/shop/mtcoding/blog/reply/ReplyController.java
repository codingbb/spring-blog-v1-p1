package shop.mtcoding.blog.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.mtcoding.blog.user.User;

//댓글 쓰기, 댓글 삭제, 댓글 목록보기
@RequiredArgsConstructor
@Controller
public class ReplyController {

    private final HttpSession session;
    private final ReplyRepository replyRepository;

    @PostMapping("/reply/{id}/delete")
    public String delete(@PathVariable int id) {
        //인증 검사. 로그인 안하면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        //게시글의 주인 여부를 확인하려면 조회 필요
        Reply reply = replyRepository.findById(id);

        // 댓글 없거나, 댓글 주인이 아니거나, 댓글 주인이거나
        if (reply == null) {
            return "error/404";
        }

        if (reply.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        replyRepository.deleteById(id);

        return "redirect:/board/"+reply.getBoardId();
    }

    @PostMapping("/reply/save")
    public String write(ReplyRequest.WriteDTO requestDTO) {
        System.out.println(requestDTO);

        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        //유효성 검사 (님들이 하세요 ㅎ) ex.코멘트의 길이는 10자를 넘을 수 없다


        //모델 위임 (핵심 코드)
        replyRepository.save(requestDTO, sessionUser.getId());
        return "redirect:/board/"+requestDTO.getBoardId();  //그 상세보기 화면으로 돌아가기
    }

}
