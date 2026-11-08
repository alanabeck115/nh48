
package Mavenjava;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MountainController {

    private final JdbcTemplate jdbcTemplate;

    public MountainController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/mountains")
    public String mountains(
            @RequestParam(required = false) String list,
            Model model) {

        String sqlLists =
            "SELECT DISTINCT trim(value) AS listname " +
            "FROM gridpeaks.mountains m, " +
            "unnest(string_to_array(m.list, ',')) AS values_list(value) " +
            "WHERE m.list IS NOT NULL AND trim(value) <> '' " +
            "ORDER BY listname";

        List<String> lists = jdbcTemplate.queryForList(
            sqlLists, String.class);

        List<Map<String, Object>> peaks;

        if (list == null || list.isBlank()) {
            peaks = List.of();
        } else {
            String sqlPeaks =
                "SELECT rank, name, elevation, state, list, trail " +
                "FROM gridpeaks.mountains " +
                "WHERE EXISTS (" +
                "SELECT 1 FROM unnest(string_to_array(list, ',')) v " +
                "WHERE lower(trim(v)) = lower(?)" +
                ") ORDER BY rank";

            peaks = jdbcTemplate.queryForList(sqlPeaks, list.trim());
        }

        model.addAttribute("lists", lists);
        model.addAttribute("peaks", peaks);
        model.addAttribute("selectedList", list);

        return "mountains";
    }
}
