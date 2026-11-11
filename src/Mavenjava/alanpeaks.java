package Mavenjava;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class alanpeaks {

	//multiple peak list
	
    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/gridpeaks";
        String user = "postgres";
        String password = "newStrongPassword123!";

        String[] months = {
        	    "", "January", "February", "March", "April",
        	    "May", "June", "July", "August",
        	    "September", "October", "November", "December"
        	};
        
        String sql =
        	    "SELECT m.name AS peakname, " +
        	    "       m.elevation, " +
        	    "       m.list AS listname, " +
        	    "       COUNT(*) AS climb_count, " +
        	    "       MIN(h.yearclimbed * 100 + h.monthclimbed) AS first_climb, " +
        	    "       MAX(h.yearclimbed * 100 + h.monthclimbed) AS last_climb " +
        	    "FROM gridpeaks.hikes h " +
        	    "JOIN gridpeaks.mountains m ON h.peakname = m.name " +
        	    "GROUP BY m.name, m.elevation, m.list " +
        	    "HAVING COUNT(*) >= 3 " +
        	    "ORDER BY climb_count DESC, m.name";

        try (
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

            String fileName = "mountains_climbed_3plus.html";

            try (PrintWriter out = new PrintWriter(fileName)) {

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Mountains Climbed Three or More Times</title>");
                out.println("</head>");
                out.println("<body>");

                out.println("<h1>Mountains Climbed Three or More Times</h1>");

                out.println("<table border='1' cellpadding='5' cellspacing='0'>");
                out.println("<tr>");
                out.println("<th>Peak Name</th>");
                out.println("<th>Elevation</th>");
                out.println("<th>Times Climbed</th>");
                out.println("<th>List Name</th>");
                out.println("<th>First Climbed</th>");
                out.println("<th>Last Climbed</th>");
                out.println("</tr>");

                while (rs.next()) {

                    int first = rs.getInt("first_climb");
                    int last = rs.getInt("last_climb");

                    int firstYear = first / 100;
                    int firstMonth = first % 100;

                    int lastYear = last / 100;
                    int lastMonth = last % 100;

                    out.println("<tr>");
                    out.println("<td>" + rs.getString("peakname") + "</td>");
                    out.println("<td>" + rs.getInt("elevation") + "</td>");
                    out.println("<td>" + rs.getInt("climb_count") + "</td>");
                    out.println("<td>" + rs.getString("listname") + "</td>");
                    out.println("<td>" + months[firstMonth] + " " + firstYear + "</td>");
                    out.println("<td>" + months[lastMonth] + " " + lastYear + "</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("</body>");
                out.println("</html>");
            }

            File f = new File(fileName);
            System.out.println("HTML file created:");
            System.out.println(f.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}