package Mavenjava;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gridmountainsleft {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/gridpeaks";
        String user = "postgres";
        String password = "newStrongPassword123!";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            printNH48ByMonthCoverage(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printNH48ByMonthCoverage(Connection conn) {

        String sql =
            "WITH nh48 AS ( " +
            "    SELECT name " +
            "    FROM gridpeaks.mountains " +
            "    WHERE list = 'nh48' " +
            "), " +
            "nh48_by_month AS ( " +
            "    SELECT DISTINCT " +
            "        h.monthclimbed AS month, " +
            "        h.peakname " +
            "    FROM gridpeaks.hikes h " +
            "    JOIN gridpeaks.mountains m " +
            "      ON m.name = h.peakname " +
            "     AND m.list = 'nh48' " +
            ") " +
            "SELECT " +
            "    to_char(make_date(2000, mth.month, 1), 'FMMonth') AS month, " +
            "    COUNT(n.name) AS peaks_missing, " +
            "    string_agg(n.name, ', ' ORDER BY n.name) AS missing_peaks " +
            "FROM (SELECT generate_series(1,12) AS month) mth " +
            "CROSS JOIN nh48 n " +
            "LEFT JOIN nh48_by_month nm " +
            "  ON nm.month = mth.month " +
            " AND nm.peakname = n.name " +
            "WHERE nm.peakname IS NULL " +
            "GROUP BY mth.month " +
            "HAVING COUNT(n.name) > 0 " +
            "ORDER BY mth.month";

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html><head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>NH48 Grid Peaks Remaining</title>\n");
        html.append("<style>\n");
        html.append("body{font-family:Arial,sans-serif;margin:20px;}\n");
        html.append("h1{color:#003366;}\n");
        html.append(".month{margin-bottom:20px;}\n");
        html.append("</style>\n");
        html.append("</head><body>\n");

        html.append("<h1>NH48 Grid Peaks Remaining By Month</h1>\n");

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasRows = false;

            while (rs.next()) {

                hasRows = true;

                String month = rs.getString("month");
                int missing = rs.getInt("peaks_missing");
                String peaks = rs.getString("missing_peaks");

                html.append("<div class='month'>\n");
                html.append("<h2>")
                    .append(month)
                    .append(" (")
                    .append(missing)
                    .append(" missing)</h2>\n");
                html.append("<p>")
                    .append(peaks)
                    .append("</p>\n");
                html.append("</div>\n");
            }

            if (!hasRows) {
                html.append("<p><b>All months have full NH48 coverage.</b></p>\n");
            }

        } catch (SQLException e) {
            System.err.println("SQL error retrieving remaining NH48 peaks:");
            e.printStackTrace();
            return;
        }

        html.append("</body></html>");

        File htmlFile = new File("nhgridremaining.html");

        try (PrintWriter out = new PrintWriter(htmlFile)) {
            out.print(html.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Created: " + htmlFile.getAbsolutePath());
    }
}
