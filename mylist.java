package Mavenjava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class mylist {

    // Every mountain I've climbed
    
    private static String nullToEmpty(String s) {
       return s == null ? "" : s;
    }
    
    private static String formatCompanions(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "Solo";
        }
        return s;
    }

    // UPDATED: Accepts PrintWriter to write directly to the HTML file
    private static void printResults(ResultSet rs, PrintWriter out) throws SQLException {
        while (rs.next()) {

            String peak = nullToEmpty(rs.getString("peakname"));
            int rank = rs.getInt("peakrank");
            int elevation = rs.getInt("elevation");
            int climbcount = rs.getInt("climbcount");

            int firstDay = rs.getInt("first_day");
            int firstMonth = rs.getInt("first_month");
            int firstYear = rs.getInt("first_year");
            String firstCompanions = formatCompanions(rs.getString("first_companions"));
            String firstComment = nullToEmpty(rs.getString("first_comment"));

            int lastDay = rs.getInt("last_day");
            int lastMonth = rs.getInt("last_month");
            int lastYear = rs.getInt("last_year");
            String lastCompanions = formatCompanions(rs.getString("last_companions"));
            String lastComment = nullToEmpty(rs.getString("last_comment"));

            // Using <br> tags instead of %n since this is an HTML file
            out.printf(
                "<b>%s</b> (%d ft) | Rank: %d | Climbed: %d times<br>" +
                "  First: %d/%d/%d | With: %s | Notes: %s<br>" +
                "  Last: %d/%d/%d | With: %s | Notes: %s<br><br>%n",
                peak, elevation, rank, climbcount,
                firstMonth, firstDay, firstYear, firstCompanions, firstComment,
                lastMonth, lastDay, lastYear, lastCompanions, lastComment
            );
        }
    }

    private static int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/gridpeaks";
        String user = "postgres";
        String password = "newStrongPassword123!";
        
        String fileName = "myhikinglist.html";
        String[] lists = {"nh48", "ne67", "ne100", "adk46", "ne111", "52wav", "highpoint"};

        // Open BOTH the DB connection and the File Writer at the start
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PrintWriter out = new PrintWriter(new FileWriter(fileName))) {

            // Simple HTML header setup
            out.println("<html><body>");
            out.println("<h1>My Mountain Lists</h1>");

            for (String listName : lists) {

                // Count peaks completed in this list
                String countQuery =
                    "SELECT COUNT(DISTINCT h.peakname) " +
                    "FROM gridpeaks.hikes h " +
                    "JOIN gridpeaks.mountains m ON h.peakname = m.name " +
                    "WHERE LOWER(m.list) LIKE '%" + listName.toLowerCase() + "%' ";

                int total = getCount(conn, countQuery); 
                
                out.println("<h2>Alan " + listName.toUpperCase() + ": " + total + "</h2>");
                out.println("<hr>");

                // Full mountain query
                String query =
                    "SELECT m.name AS peakname, " +
                    "       m.rank AS peakrank, " +
                    "       m.elevation AS elevation, " +
                    "       COALESCE(h_count.climbcount, 0) AS climbcount, " +
                    "       h_first.monthclimbed AS first_month, " +
                    "       h_first.dayclimbed AS first_day, " +
                    "       h_first.yearclimbed AS first_year, " +
                    "       h_first.companions AS first_companions, " +
                    "       h_first.comment AS first_comment, " +
                    "       h_last.monthclimbed AS last_month, " +
                    "       h_last.dayclimbed AS last_day, " +
                    "       h_last.yearclimbed AS last_year, " +
                    "       h_last.companions AS last_companions, " +
                    "       h_last.comment AS last_comment " +
                    " FROM ( " +
                    "    SELECT DISTINCT ON (LOWER(name)) * " +
                    "    FROM gridpeaks.mountains " +
                    "    WHERE LOWER(list) LIKE '%" + listName.toLowerCase() + "%' " +
                    "    ORDER BY LOWER(name), rank " +
                    ") m " +
                    
                    // First climb
                    "LEFT JOIN LATERAL ( " +
                    "    SELECT * FROM gridpeaks.hikes h1 " +
                    "    WHERE LOWER(h1.peakname) = LOWER(m.name) " +
                    "    ORDER BY TO_DATE(h1.yearclimbed || '-' || h1.monthclimbed || '-' || h1.dayclimbed,'YYYY-MM-DD') ASC " +
                    "    LIMIT 1 " +
                    ") h_first ON TRUE " +

                    // Last climb
                    "LEFT JOIN LATERAL ( " +
                    "    SELECT * FROM gridpeaks.hikes h2 " +
                    "    WHERE LOWER(h2.peakname) = LOWER(m.name) " +
                    "    ORDER BY TO_DATE(h2.yearclimbed || '-' || h2.monthclimbed || '-' || h2.dayclimbed,'YYYY-MM-DD') DESC " +
                    "    LIMIT 1 " +
                    ") h_last ON TRUE " +

                    // Number of climbs               
                    "LEFT JOIN LATERAL ( " +
                    "    SELECT COUNT(DISTINCT (h3.peakname, h3.dayclimbed, h3.monthclimbed, h3.yearclimbed)) AS climbcount " +
                    "    FROM gridpeaks.hikes h3 " +
                    "    WHERE LOWER(h3.peakname) = LOWER(m.name) " +
                    ") h_count ON TRUE " +

                    "ORDER BY m.rank ASC NULLS LAST, m.name";
            
                try (PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    // Pass the PrintWriter out down here
                    printResults(rs, out);
                }
            } // End of For Loop
            
            out.println("</body></html>");
            
            // This code runs successfully after the loop closes
            File f = new File(fileName);
            System.out.println("HTML file created successfully!");
            System.out.println(f.getAbsolutePath());
            
        } catch (SQLException e) {
            System.err.println("Database Error!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("File Writing Error!");
            e.printStackTrace();
        }
    }
}