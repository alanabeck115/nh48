package Mavenjava;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class companions {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/gridpeaks";
        String user = "postgres";
        String password = "newStrongPassword123!";

        try (
            Connection conn = DriverManager.getConnection(url, user, password);
            BufferedWriter writer = new BufferedWriter(new FileWriter("companion_stats.html"))
        ) {

            writeCompanionStats(conn, writer);

            System.out.println("HTML written to companion_stats.html");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCompanionStats(Connection conn, BufferedWriter writer)
            throws SQLException, IOException {

        writer.write("""
            <html>
            <head>
              <title>Companion Climb Statistics</title>
              <style>
                body {
                    font-family: Arial;
                    margin: 40px;
                }
                .companion {
                    margin-bottom: 24px;
                    padding-bottom: 16px;
                    border-bottom: 1px solid #ddd;
                }
              </style>
            </head>
            <body>
            <h1>Companion Climb Statistics</h1>
            """);

        String sql =
            "WITH expanded AS ( " +
            "  SELECT TRIM(LOWER(c.name)) AS companion, " +
            "         h.peakname, " +
            "         make_date(h.yearclimbed, h.monthclimbed, h.dayclimbed) AS climb_date " +
            "  FROM gridpeaks.hikes h " +
            "  CROSS JOIN LATERAL unnest(string_to_array(h.companions, ',')) AS c(name) " +
            "  WHERE h.companions IS NOT NULL " +
            "    AND TRIM(h.companions) <> '' " +
            ") " +
            "SELECT companion, " +
            "       COUNT(DISTINCT peakname) AS peak_count " +
            "FROM expanded " +
            "GROUP BY companion " +
            "HAVING COUNT(DISTINCT peakname) <> 1 " +
            "ORDER BY peak_count DESC, companion";

        try (
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                String companion = rs.getString("companion");
                int peakCount = rs.getInt("peak_count");

                writer.write("<div class='companion'>");

                writer.write("<h2>" + escape(companion) + "</h2>");
                writer.write("<p>Peaks climbed: " + peakCount + "</p>");

                writer.write("<p>" + getFirstClimb(conn, companion) + "</p>");
                writer.write("<p>" + getLastClimb(conn, companion) + "</p>");
                writer.write("<p>" + getMostClimbedPeak(conn, companion) + "</p>");

                writer.write("</div>");
            }
        }

        writer.write("""
            </body>
            </html>
            """);
    }

    private static String getFirstClimb(Connection conn, String companion)
            throws SQLException {

        String sql =
            "SELECT h.peakname, " +
            "       make_date(h.yearclimbed, h.monthclimbed, h.dayclimbed) AS climb_date " +
            "FROM gridpeaks.hikes h " +
            "CROSS JOIN LATERAL unnest(string_to_array(h.companions, ',')) AS c(name) " +
            "WHERE TRIM(LOWER(c.name)) = ? " +
            "ORDER BY climb_date ASC " +
            "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, companion);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "First climb: " +
                       rs.getDate("climb_date") +
                       " – " +
                       rs.getString("peakname");
            }
        }

        return "";
    }

    private static String getLastClimb(Connection conn, String companion)
            throws SQLException {

        String sql =
            "SELECT h.peakname, " +
            "       make_date(h.yearclimbed, h.monthclimbed, h.dayclimbed) AS climb_date " +
            "FROM gridpeaks.hikes h " +
            "CROSS JOIN LATERAL unnest(string_to_array(h.companions, ',')) AS c(name) " +
            "WHERE TRIM(LOWER(c.name)) = ? " +
            "ORDER BY climb_date DESC " +
            "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, companion);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "Last climb: " +
                       rs.getDate("climb_date") +
                       " – " +
                       rs.getString("peakname");
            }
        }

        return "";
    }

    private static String getMostClimbedPeak(Connection conn, String companion)
            throws SQLException {

        String sql =
            "SELECT peakname, COUNT(*) AS climb_count " +
            "FROM gridpeaks.hikes h " +
            "CROSS JOIN LATERAL unnest(string_to_array(h.companions, ',')) AS c(name) " +
            "WHERE TRIM(LOWER(c.name)) = ? " +
            "GROUP BY peakname " +
            "ORDER BY climb_count DESC, peakname " +
            "LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, companion);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "Most climbed: " +
                       rs.getString("peakname") +
                       " (" +
                       rs.getInt("climb_count") +
                       " times)";
            }
        }

        return "";
    }

    private static String escape(String s) {

        if (s == null) return "";

        return s
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}