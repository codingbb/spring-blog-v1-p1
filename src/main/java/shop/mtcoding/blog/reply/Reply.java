package shop.mtcoding.blog.reply;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name="reply_tb")
@Data
@Entity // 테이블 생성하기 위해 필요한 어노테이션
public class Reply {
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 전략
    private int id;

    private String comment;
    private int userId;
    private int boardId;
    private LocalDateTime createdAt;

}
