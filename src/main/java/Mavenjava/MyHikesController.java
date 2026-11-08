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
public class MyHikesController {

    private final JdbcTemplate jdbcTemplate;

    public MyHikesController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Display hikes for a selected user
    @GetMapping("/my-hikes")
    public String myHikes(
            @RequestParam(required = false) Integer userid,
            Model model) {

        List<Map<String, Object>> users =
            jdbcTemplate.queryForList(
                "SELECT userid, username FROM gridpeaks.users ORDER BY username"
            );

        List<Map<String, Object>> hikes = List.of();

        if (userid != null) {
            String sql =
                "SELECT h.hikeid, h.peakname, " +
                "LPAD(h.monthclimbed::text, 2, '0') || '/' || " +
                "LPAD(h.dayclimbed::text, 2, '0') || '/' || " +
                "h.yearclimbed::text AS climb_date, " +
                "h.companions, h.trail, h.comment, h.peakrank, " +
                "m.list AS mountain_lists " +
                "FROM gridpeaks.hikes h " +
                "LEFT JOIN gridpeaks.mountains m ON h.peakrank = m.rank " +
                "WHERE h.userid = ? OR " +
                "(h.userid IS NULL AND lower(trim(h.entered_by)) = " +
                "(SELECT lower(trim(username)) FROM gridpeaks.users WHERE userid = ?)) " +
                "ORDER BY h.yearclimbed DESC, h.monthclimbed DESC, " +
                "h.dayclimbed DESC, h.hikeid DESC";

            hikes = jdbcTemplate.queryForList(sql, userid, userid);
        }

        model.addAttribute("users", users);
        model.addAttribute("hikes", hikes);
        model.addAttribute("selectedUserid", userid);
        model.addAttribute("hikeCount", hikes.size());

        return "my-hikes";
    }

    // Display the edit page
    @GetMapping("/my-hikes/edit")
    public String editHike(
            @RequestParam long hikeid,
            @RequestParam Integer userid,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!ownsHike(hikeid, userid)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "Hike not found or not owned by this user."
            );
            redirectAttributes.addAttribute("userid", userid);
            return "redirect:/my-hikes";
        }

        String sql =
            "SELECT hikeid, peakname, peakrank, " +
            "yearclimbed || '-' || LPAD(monthclimbed::text, 2, '0') || '-' || " +
            "LPAD(dayclimbed::text, 2, '0') AS climb_date_input, " +
            "companions, trail, comment " +
            "FROM gridpeaks.hikes WHERE hikeid = ?";

        Map<String, Object> hike =
            jdbcTemplate.queryForMap(sql, hikeid);

        List<Map<String, Object>> peaks =
            jdbcTemplate.queryForList(
                "SELECT rank, name FROM gridpeaks.mountains ORDER BY name"
            );

        model.addAttribute("hike", hike);
        model.addAttribute("peaks", peaks);
        model.addAttribute("userid", userid);

        return "edit-hike";
    }

    // Save changes
    @PostMapping("/my-hikes/update")
    public String updateHike(
            @RequestParam long hikeid,
            @RequestParam Integer userid,
            @RequestParam Integer peakrank,
            @RequestParam String climbDate,
            @RequestParam(required = false) String companions,
            @RequestParam(required = false) String trail,
            @RequestParam(required = false) String comment,
            RedirectAttributes redirectAttributes) {

        if (!ownsHike(hikeid, userid)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "Hike not found or not owned by this user."
            );
            redirectAttributes.addAttribute("userid", userid);
            return "redirect:/my-hikes";
        }

        LocalDate date = LocalDate.parse(climbDate);

        String peakname = jdbcTemplate.queryForObject(
            "SELECT name FROM gridpeaks.mountains WHERE rank = ?",
            String.class,
            peakrank
        );

        String sql =
            "UPDATE gridpeaks.hikes SET " +
            "peakname = ?, peakrank = ?, " +
            "dayclimbed = ?, monthclimbed = ?, yearclimbed = ?, " +
            "companions = ?, trail = ?, comment = ? " +
            "WHERE hikeid = ?";

        jdbcTemplate.update(
            sql,
            peakname,
            peakrank,
            date.getDayOfMonth(),
            date.getMonthValue(),
            date.getYear(),
            companions == null ? "" : companions,
            trail == null ? "" : trail,
            comment == null ? "" : comment,
            hikeid
        );

        redirectAttributes.addFlashAttribute(
            "successMessage", "Hike updated successfully!"
        );
        redirectAttributes.addAttribute("userid", userid);

        return "redirect:/my-hikes";
    }

    // Delete one hike
    @PostMapping("/my-hikes/delete")
    public String deleteHike(
            @RequestParam long hikeid,
            @RequestParam Integer userid,
            RedirectAttributes redirectAttributes) {

        if (!ownsHike(hikeid, userid)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "Hike not found or not owned by this user."
            );
        } else {
            jdbcTemplate.update(
                "DELETE FROM gridpeaks.hikes WHERE hikeid = ?",
                hikeid
            );

            redirectAttributes.addFlashAttribute(
                "successMessage", "Hike deleted successfully!"
            );
        }

        redirectAttributes.addAttribute("userid", userid);
        return "redirect:/my-hikes";
    }

    // Check that the hike belongs to the selected user
    private boolean ownsHike(long hikeid, Integer userid) {

        String sql =
            "SELECT COUNT(*) FROM gridpeaks.hikes h " +
            "WHERE h.hikeid = ? AND (" +
            "h.userid = ? OR " +
            "(h.userid IS NULL AND lower(trim(h.entered_by)) = " +
            "(SELECT lower(trim(username)) FROM gridpeaks.users WHERE userid = ?))" +
            ")";

        Integer count = jdbcTemplate.queryForObject(
            sql, Integer.class, hikeid, userid, userid
        );

        return count != null && count > 0;
    }
}