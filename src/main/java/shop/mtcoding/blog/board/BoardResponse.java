package shop.mtcoding.blog.board;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BoardResponse {

    @Data
    public static class DetailDTO {
        //null이 있을 수도 있으니까 Integer
        private Integer id;
        private String title;
        private String content;
        private Integer userId;
        private String username;
        private Timestamp createdAt;

        // Reply
        private Integer rId;
        private Integer rUserId;
        private Integer rUsername;
        private String rComment;

    }

}
