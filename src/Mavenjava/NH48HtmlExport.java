package Mavenjava;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Month;

public class NH48HtmlExport {

    private static String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String formatCompanions(String s) {
        return (s == null || s.trim().isEmpty())
                ? "Solo"
                : s.trim();
    }

    private static String cleanText(String s) {

        if (s == null) {
            return "";
        }

        return s
                .replace("\uFFFD", " - ")   // Unicode replacement character
                .replace("�", " - ")        // visible bad character
                .replace("—", " - ")        // em dash
                .replace("–", " - ")        // en dash
                .trim();
    }

    private static String escapeHtml(String s) {

        if (s == null) {
            return "";
        }

        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String monthName(int month) {

        if (month < 1 || month > 12) {
            return "";
        }

        String name = Month.of(month).name();

        return name.substring(0, 1)
                + name.substring(1).toLowerCase();
    }

    public static void main(String[] args) {

        String url =
                "jdbc:postgresql://localhost:5432/gridpeaks";

        String user = "postgres";

        String password =
                "newStrongPassword123!";

        String sql =
                "SELECT " +
                "    peakname, " +
                "    yearclimbed, " +
                "    monthclimbed, " +
                "    companions, " +
                "    comment " +
                "FROM gridpeaks.hikes " +
                "WHERE peakname IN ( " +
                "    SELECT name " +
                "    FROM gridpeaks.mountains " +
                "    WHERE list = 'nh48' " +
                ") " +
                "AND yearclimbed BETWEEN 1980 AND 2026 " +
                "ORDER BY yearclimbed, monthclimbed, peakname";

        File htmlFile =
                new File("nh48-1980-2026.html");

        try (

            Connection conn =
                    DriverManager.getConnection(
                            url,
                            user,
                            password
                    );

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            PrintWriter out =
                    new PrintWriter(htmlFile);

        ) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>NH48 Hikes 1980-2026</title>");

            out.println("<style>");
            out.println(
                    "body{" +
                    "font-family:Arial,sans-serif;" +
                    "max-width:1000px;" +
                    "margin:auto;" +
                    "padding:20px;" +
                    "}"
            );

            out.println(
                    "h1{" +
                    "margin-bottom:20px;" +
                    "}"
            );

            out.println(
                    "h2{" +
                    "margin-top:28px;" +
                    "margin-bottom:8px;" +
                    "}"
            );

            out.println(
                    "li{" +
                    "margin-bottom:5px;" +
                    "}"
            );

            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>NH48 Hikes (1980-2026)</h1>");

            int currentYear = -1;
            int count = 0;

            while (rs.next()) {

                count++;

                int year =
                        rs.getInt("yearclimbed");

                if (year != currentYear) {

                    if (currentYear != -1) {
                        out.println("</ul>");
                    }

                    out.printf(
                            "<h2>%d</h2>%n",
                            year
                    );

                    out.println("<ul>");

                    currentYear = year;
                }

                String peak =
                        cleanText(
                                nullToEmpty(
                                        rs.getString("peakname")
                                )
                        );

                int month =
                        rs.getInt("monthclimbed");

                String companions =
                        cleanText(
                                formatCompanions(
                                        rs.getString("companions")
                                )
                        );

                String comment =
                        cleanText(
                                nullToEmpty(
                                        rs.getString("comment")
                                )
                        );

                String line =
                        peak +
                        " (" + monthName(month) + ")" +
                        " - " +
                        companions;

                if (!comment.isEmpty()) {
                    line +=
                            " - " +
                            comment;
                }

                out.printf(
                        "<li>%s</li>%n",
                        escapeHtml(line)
                );
            }

            if (currentYear != -1) {
                out.println("</ul>");
            }

            out.println("</body>");
            out.println("</html>");

            out.flush();

            System.out.println(
                    "Created file: " +
                    htmlFile.getAbsolutePath()
            );

            System.out.println(
                    "Total hikes written: " +
                    count
            );

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}