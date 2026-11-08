
package Mavenjava;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/users")
    public String users(Model model) {

        String sql =
            "SELECT userid, username " +
            "FROM gridpeaks.users " +
            "ORDER BY username";

        List<Map<String, Object>> users =
            jdbcTemplate.queryForList(sql);

        model.addAttribute("users", users);

        return "users";
    }
}
