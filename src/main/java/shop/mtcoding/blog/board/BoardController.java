package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final HttpSession session;
    private final BoardRepository boardRepository;

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 권한 체크
        Board board = boardRepository.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 핵심 로직
        // update board_tb set title = ?, content = ? where id = ?;
        boardRepository.update(requestDTO, id);

        return "redirect:/board/"+id; //상세 보기 화면으로 돌아가기
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        // 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 권한 체크 (로그인한 유저의 아이디와 작성자 아이디 비교)
        // 조회 없이 권한 체크를 못한다. 게시글 누가 썼는지 모르잖아. 그래서 조회 먼저
        // 모델 위임 (id로 board를 조회)
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 가방에 담기
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) {
        // 1. 인증 안되면 나가
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {  //401
            return "redirect:/loginForm";
        }

        // 2. 권한 없으면 나가
        Board board = boardRepository.findById(id);
        if (board.getUserId() != sessionUser.getId()){
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다");
            return "error/40x";
        }

        boardRepository.deleteById(id);

        return "redirect:/";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);

        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안돼요");
            return "error/40x"; //BadRequest
        }

        // 3. 모델 위임
        //insert into board_tb(title, content, user_id, created_at) value(?,?,?, now());
        boardRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/";
    }

    @GetMapping({"/", "/board"})
    public String index(HttpServletRequest request) {

        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {

        // session 영역에 sessionUser 키 값에 user 객체 있는지 체크
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 값이 null이면 로그인 페이지로 리다이렉션
        // 값이 null이 아니면 /board/saveForm 으로 이동
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {
        // 바디 데이터가 없으면 유효성 검사가 필요없지 ㅎ

        // 1. 모델 진입 - 상세보기 데이터 가져오기
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdWithUser(id);

        // 2. 페이지 주인 여부 체크 (board의 userId와 sessionUser의 id를 비교)
        int boardUserId = responseDTO.getUserId();  //게시글 작성자 번호
        User sessionUser = (User) session.getAttribute("sessionUser");

        boolean pageOwner;
        if(sessionUser == null){
            pageOwner = false;
        }else{
            int 게시글작성자번호 = responseDTO.getUserId();
            int 로그인한사람의번호 = sessionUser.getId();
            pageOwner = 게시글작성자번호 == 로그인한사람의번호;
        }

        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }
}
