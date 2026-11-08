package Mavenjava;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HikeEntryController {

    private final JdbcTemplate jdbcTemplate;

    public HikeEntryController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/hike-entry")
    public String showHikeEntry(
            @RequestParam(required = false) String list,
            Model model) {

        // Mountain lists
        String sqlLists =
            "SELECT DISTINCT trim(value) AS listname " +
            "FROM gridpeaks.mountains m, " +
            "unnest(string_to_array(m.list, ',')) AS x(value) " +
            "WHERE m.list IS NOT NULL AND trim(value) <> '' " +
            "ORDER BY listname";

        List<String> lists =
            jdbcTemplate.queryForList(sqlLists, String.class);

        // Peaks belonging to selected list
        List<Map<String, Object>> peaks = List.of();

        if (list != null && !list.isBlank()) {
            String sqlPeaks =
                "SELECT DISTINCT m.rank, m.name " +
                "FROM gridpeaks.mountains m " +
                "WHERE EXISTS (" +
                "SELECT 1 FROM unnest(string_to_array(m.list, ',')) AS x(value) " +
                "WHERE lower(trim(x.value)) = lower(?)" +
                ") ORDER BY m.rank";

            peaks = jdbcTemplate.queryForList(sqlPeaks, list.trim());
        }

        // Users
        List<Map<String, Object>> users =
            jdbcTemplate.queryForList(
                "SELECT userid, username FROM gridpeaks.users ORDER BY username"
            );

        // Individual companions from existing hike records
        String sqlCompanions =
            "SELECT DISTINCT trim(x.value) AS companion " +
            "FROM gridpeaks.hikes h " +
            "CROSS JOIN LATERAL " +
            "unnest(string_to_array(coalesce(h.companions, ''), ',')) AS x(value) " +
            "WHERE trim(x.value) <> '' " +
            "ORDER BY companion";

        List<String> companions =
            jdbcTemplate.queryForList(sqlCompanions, String.class);

        // Existing trails
        String sqlTrails =
            "SELECT DISTINCT trim(trail) " +
            "FROM gridpeaks.hikes " +
            "WHERE trail IS NOT NULL AND trim(trail) <> '' " +
            "ORDER BY trim(trail)";

        List<String> trails =
            jdbcTemplate.queryForList(sqlTrails, String.class);

        model.addAttribute("lists", lists);
        model.addAttribute("peaks", peaks);
        model.addAttribute("users", users);
        model.addAttribute("companions", companions);
        model.addAttribute("trails", trails);
        model.addAttribute("selectedList", list);

        return "hike-entry";
    }

    @PostMapping("/hike-entry/save")
    public String saveHike(
            @RequestParam String list,
            @RequestParam Integer peakrank,
            @RequestParam Integer userid,
            @RequestParam String climbDate,

            @RequestParam(required = false) String companion1,
            @RequestParam(required = false) String newCompanion1,
            @RequestParam(required = false) String companion2,
            @RequestParam(required = false) String newCompanion2,

            @RequestParam(required = false) String selectedTrail,
            @RequestParam(required = false) String newTrail,
            @RequestParam(required = false) String comment,

            RedirectAttributes redirectAttributes) {

        LocalDate date = LocalDate.parse(climbDate);

        // New companion text takes priority over dropdown selection
        String c1 = firstNonBlank(newCompanion1, companion1);
        String c2 = firstNonBlank(newCompanion2, companion2);

        // Prevent selecting the same companion twice
        if (c1 != null && c2 != null && c1.equalsIgnoreCase(c2)) {
            c2 = null;
        }

        String companions = "";
        if (c1 != null) {
            companions = c1;
        }
        if (c2 != null) {
            companions += (companions.isEmpty() ? "" : ", ") + c2;
        }

        // New trail takes priority over dropdown selection
        String trail = firstNonBlank(newTrail, selectedTrail);
        if (trail == null) {
            trail = "";
        }

        String peakname = jdbcTemplate.queryForObject(
            "SELECT name FROM gridpeaks.mountains WHERE rank = ?",
            String.class,
            peakrank
        );

        String username = jdbcTemplate.queryForObject(
            "SELECT username FROM gridpeaks.users WHERE userid = ?",
            String.class,
            userid
        );

        String sql =
            "INSERT INTO gridpeaks.hikes " +
            "(peakname, dayclimbed, monthclimbed, yearclimbed, " +
            "peakrank, companions, comment, trail, entered_by, userid) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
            sql,
            peakname,
            date.getDayOfMonth(),
            date.getMonthValue(),
            date.getYear(),
            peakrank,
            companions,
            comment == null ? "" : comment,
            trail,
            username,
            userid
        );

        redirectAttributes.addAttribute("list", list);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Hike saved successfully!"
        );

        return "redirect:/hike-entry";
    }

    private String firstNonBlank(String newValue, String selectedValue) {
        if (newValue != null && !newValue.isBlank()) {
            return newValue.trim();
        }
        if (selectedValue != null && !selectedValue.isBlank()) {
            return selectedValue.trim();
        }
        return null;
    }
}