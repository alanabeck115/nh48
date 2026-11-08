
package Mavenjava;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HikingLogController {

    private final JdbcTemplate jdbcTemplate;

    public HikingLogController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/hikes")
    public String hikes(Model model) {

        String sql =
            "SELECT " +
            "h.peakrank, " +
            "LPAD(h.monthclimbed::text, 2, '0') || '/' ||" +
            "LPAD(h.dayclimbed::text, 2, '0') || '/' ||" +
            "COALESCE(NULLIF(m.name, ''), h.peakname) AS mountain, " +
            "h.yearclimbed::text AS climb_date" +
            "h.dayclimbed, " +
            "h.monthclimbed, " +
            "h.yearclimbed, " +
            "u.username AS hiker, " +
            "h.companions, " +
            "h.trail, " +
            "h.comment " +
            "FROM gridpeaks.hikes h " +
            "LEFT JOIN gridpeaks.mountains m " +
            "ON h.peakrank = m.rank " +
            "LEFT JOIN gridpeaks.users u " +
            "ON h.userid = u.userid " +
            "ORDER BY h.yearclimbed DESC, " +
            "h.monthclimbed DESC, " +
            "h.dayclimbed DESC";

        List<Map<String, Object>> hikes =
            jdbcTemplate.queryForList(sql);

        model.addAttribute("hikes", hikes);
        model.addAttribute("totalHikes", hikes.size());

        return "hikes";
    }
}
