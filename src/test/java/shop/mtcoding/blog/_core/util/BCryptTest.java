package shop.mtcoding.blog._core.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {

    

    @Test
    public void gensalt_test() {
        String salt = BCrypt.gensalt();
        System.out.println(salt);
    }

    @Test
    public void hashpw_test() {
        String rawPassword = "1234";
        String encPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println(encPassword);
    }

}
