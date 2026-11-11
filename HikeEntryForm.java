package Mavenjava;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class HikeEntryForm extends JFrame {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/gridpeaks";

    private static final String USER = "postgres";
    private static final String PASSWORD = "newStrongPassword123!";


    private JComboBox<String> listCombo;
    private JComboBox<String> peakCombo;
    private JComboBox<String> companionCombo;
    private JComboBox<String> companion2Combo;
    private JComboBox<String> trailCombo;
    private JComboBox<String> userCombo;
    

    private JComboBox<Integer> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JComboBox<Integer> yearCombo;

    private JButton companionReportButton;
    private JButton multipleMountainsButton;
    private JButton alanTotalListButton;
    private JButton gridProgressButton;
    private JButton totalNH48Button;
    private JButton Hikes2026Button;

    private JTextArea commentArea;
    private JTextField newCompanion1Field;
    private JTextField newCompanion2Field;
    private JTextField newTrailField;




    public HikeEntryForm() {

        setTitle("GridPeaks Hike Entry");
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        createUI();

        loadLists();
        loadPeaks();
        loadCompanions();
        loadTrails();
    }

    private void openHtml(String fileName) {
        try {
            Desktop.getDesktop().browse(new File(fileName).toURI());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Unable to open " + fileName,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createUI() {


        JPanel panel =
                new JPanel(new GridBagLayout());


        GridBagConstraints gc =
                new GridBagConstraints();


        gc.insets =
                new Insets(5,5,5,5);


        gc.anchor =
                GridBagConstraints.WEST;



        listCombo =
                new JComboBox<>();

        peakCombo =
                new JComboBox<>();

        companionCombo =
                new JComboBox<>();
        
        companion2Combo =
                new JComboBox<>(); 

        trailCombo =
                new JComboBox<>();
        
        newCompanion1Field = new JTextField(12);
        newCompanion2Field = new JTextField(12);
        newTrailField = new JTextField(12);

        userCombo =
                new JComboBox<>();
        userCombo.addItem("Alan");
        userCombo.addItem("Mari");

        companionReportButton = new JButton("Companion Report");
        multipleMountainsButton = new JButton("Multiple Mountains Report");
        alanTotalListButton = new JButton("Alan Total List Mountains Report");
        gridProgressButton = new JButton("Grid Progress Report");
        totalNH48Button = new JButton("Total NH48 Report");
        Hikes2026Button = new JButton("2026 Hikes Report");
        
        companionCombo.addItem("");
        companion2Combo.addItem("");
        trailCombo.addItem("");



        listCombo.addActionListener(e ->
                loadPeaks());




        monthCombo =
                new JComboBox<>();

        for(int i=1;i<=12;i++)
            monthCombo.addItem(i);



        dayCombo =
                new JComboBox<>();

        for(int i=1;i<=31;i++)
            dayCombo.addItem(i);



        yearCombo =
                new JComboBox<>();

        for(int i=1980;i<=2035;i++)
            yearCombo.addItem(i);


        yearCombo.setSelectedItem(2026);


        commentArea =
                new JTextArea(5,40);



        int row=0;

        addField(panel,gc,row++,
                "User:",userCombo);

        addField(panel,gc,row++,
                "List:",listCombo);

        addField(panel,gc,row++,
                "Peak:",peakCombo);


        JPanel date =
                new JPanel();

        date.add(monthCombo);
        date.add(dayCombo);
        date.add(yearCombo);


        addField(panel,gc,row++,
                "Date:",date);

        JPanel comp1Panel = new JPanel();
        comp1Panel.add(companionCombo);
        comp1Panel.add(new JLabel("New:"));
        comp1Panel.add(newCompanion1Field);

        addField(panel, gc, row++,
                "Companion 1:",
                comp1Panel);

        JPanel comp2Panel = new JPanel();
        comp2Panel.add(companion2Combo);
        comp2Panel.add(new JLabel("New:"));
        comp2Panel.add(newCompanion2Field);

        addField(panel, gc, row++,
                "Companion 2:",
                comp2Panel);

        JPanel trailPanel = new JPanel();
        trailPanel.add(trailCombo);
        trailPanel.add(new JLabel("New:"));
        trailPanel.add(newTrailField);

        addField(panel, gc, row++,
                "Trail:",
                trailPanel);



        gc.gridx=0;
        gc.gridy=row;

        panel.add(
            new JLabel("Comment:"),gc);


        gc.gridx=1;

        panel.add(
            new JScrollPane(commentArea),gc);



        JButton save =
                new JButton("Save Hike");


        save.addActionListener(e ->
                saveHike());



        JButton delete =
                new JButton("Delete Hike");


        delete.addActionListener(e ->
                deleteHike());



        JPanel buttons =
                new JPanel(new GridLayout(0, 2, 5, 5));

        buttons.add(save);
        buttons.add(delete);
        
        buttons.add(companionReportButton);
        buttons.add(multipleMountainsButton);
        
        buttons.add(alanTotalListButton);
        buttons.add(gridProgressButton);
        
        buttons.add(totalNH48Button);
        buttons.add(Hikes2026Button);

        companionReportButton.addActionListener(e -> {
        runReport(companions.class);
        openHtml("companion_stats.html");
        });

        multipleMountainsButton.addActionListener(e -> {
        runReport(companions.class);
        openHtml("mountains_climbed_3plus.html");
        });

        alanTotalListButton.addActionListener(e -> {
        runReport(companions.class);
        openHtml("myhikinglist.html");
        });

        gridProgressButton.addActionListener(e -> {
        runReport(companions.class);
        openHtml("nhgridremaining.html");
        });

        totalNH48Button.addActionListener(e -> {
        runReport(companions.class);
        openHtml("nh48-progress.html");
    	});
        
        
        Hikes2026Button.addActionListener(e -> {
        runReport(companions.class);
        openHtml("hikes2026.html");
        });
        
        gc.gridx = 0;
        gc.gridy = row + 1;
        panel.add(new JLabel("View Reports:"), gc);

        gc.gridx = 1;
        gc.gridy = row + 1;
        panel.add(buttons, gc);



        add(panel,BorderLayout.CENTER);

JButton deployButton = new JButton("Deploy Website");

deployButton.addActionListener(e -> {
    triggerDeploy();
    JOptionPane.showMessageDialog(
        this,
        "GitHub deployment started."
    );
});

buttonPanel.add(deployButton);

    }

    private void runReport(Class<?> reportClass) {

        try {

            java.lang.reflect.Method main =
                    reportClass.getMethod(
                            "main",
                            String[].class);

            main.invoke(null,
                    (Object) new String[0]);

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void addField(JPanel panel,
                          GridBagConstraints gc,
                          int row,
                          String text,
                          Object component) {


        gc.gridx=0;
        gc.gridy=row;

        panel.add(new JLabel(text),gc);


        gc.gridx=1;

        panel.add(
            (java.awt.Component)component,gc);
    }
    private void loadLists() {

        listCombo.removeAllItems();

        String sql =
            "SELECT DISTINCT trim(value) AS listname " +
            "FROM gridpeaks.mountains, " +
            "unnest(string_to_array(list, ',')) AS value " +
            "WHERE list IS NOT NULL " +
            "ORDER BY listname";


        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs =
                ps.executeQuery()) {


            while(rs.next()) {

                listCombo.addItem(
                    rs.getString("listname").trim());
            }


        } catch(Exception ex) {

            showError(ex);
        }
    }




    private void loadPeaks() {

        peakCombo.removeAllItems();


        String selectedList =
                (String)listCombo.getSelectedItem();


        if(selectedList == null)
            return;



        String sql =
            "SELECT DISTINCT name " +
            "FROM gridpeaks.mountains " +
            "WHERE LOWER(list) LIKE LOWER(?) " +
            "ORDER BY name";



        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql)) {


            ps.setString(1,"%"+selectedList+"%");


            ResultSet rs =
                ps.executeQuery();


            while(rs.next()) {

                peakCombo.addItem(
                    rs.getString("name"));
            }


        } catch(Exception ex) {

            showError(ex);
        }
    }




    private void loadCompanions() {

        companionCombo.removeAllItems();
        companion2Combo.removeAllItems();
        companionCombo.addItem("");
        companion2Combo.addItem("");
        
        


        TreeSet<String> set =
                new TreeSet<>();


        String sql =
            "SELECT companions " +
            "FROM gridpeaks.hikes " +
            "WHERE companions IS NOT NULL";



        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs =
                ps.executeQuery()) {


            while(rs.next()) {

                String value =
                    rs.getString("companions");


                if(value != null) {

                    for(String c:value.split(",")) {

                        c=c.trim();

                        if(!c.isEmpty())
                            set.add(c);
                    }
                }
            }


            for(String c:set) {
                companionCombo.addItem(c);
                companion2Combo.addItem(c);
            }


        } catch(Exception ex) {

            showError(ex);
        }
    }




    private void loadTrails() {

        trailCombo.removeAllItems();

        trailCombo.addItem("");

        
        String sql =
            "SELECT DISTINCT trail " +
            "FROM gridpeaks.hikes " +
            "WHERE trail IS NOT NULL " +
            "AND trim(trail) <> '' " +
            "ORDER BY trail";



        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ResultSet rs =
                ps.executeQuery()) {


            while(rs.next()) {

                trailCombo.addItem(
                    rs.getString("trail"));
            }


        } catch(Exception ex) {

            showError(ex);
        }
    }





    private int getPeakRank(String peak) {


        String sql =
            "SELECT rank " +
            "FROM gridpeaks.mountains " +
            "WHERE name = ? " +
            "LIMIT 1";


        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql)) {


            ps.setString(1,peak);


            ResultSet rs =
                ps.executeQuery();


            if(rs.next())
                return rs.getInt("rank");



        } catch(Exception ex) {

            showError(ex);
        }


        return 0;
    }





    private void saveHike() {


        String enteredBy =
            (String)userCombo.getSelectedItem();

        String peak =
            (String)peakCombo.getSelectedItem();


        if(enteredBy == null || enteredBy.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Select a user.");
            return;
        }

        if(peak == null) {

            JOptionPane.showMessageDialog(
                this,
                "Select a peak.");

            return;
        }



        int rank =
            getPeakRank(peak);



        int month =
            (Integer)monthCombo.getSelectedItem();

        int day =
            (Integer)dayCombo.getSelectedItem();

        int year =
            (Integer)yearCombo.getSelectedItem();


        String companion1 =
        	    companionCombo.getSelectedItem() == null
        	        ? ""
        	        : companionCombo.getSelectedItem()
        	                       .toString()
        	                       .trim();

        String companion2 =
        	    companion2Combo.getSelectedItem() == null
        	        ? ""
        	        : companion2Combo.getSelectedItem()
        	                        .toString()
        	                        .trim();

        if (!newCompanion1Field.getText().trim().isEmpty()) {
            companion1 = newCompanion1Field.getText().trim();
        }

        if (!newCompanion2Field.getText().trim().isEmpty()) {
            companion2 = newCompanion2Field.getText().trim();
        }
        
        
        if(!companion1.isEmpty()
        	        && companion1.equals(companion2)) {

        	    JOptionPane.showMessageDialog(
        	        this,
        	        "Companion 1 and Companion 2 cannot be the same.");

        	    return;
        	}

        	String companion = "";

        	if(!companion1.isEmpty())
        	    companion = companion1;

        	if(!companion2.isEmpty()) {

        	    if(!companion.isEmpty())
        	        companion += ", ";

        	    companion += companion2;
        	}


        	String trail =
        		    trailCombo.getSelectedItem() == null
        		    ? ""
        		    : trailCombo.getSelectedItem().toString().trim();

        		if (!newTrailField.getText().trim().isEmpty()) {
        		    trail = newTrailField.getText().trim();
        		}



        String comment =
            commentArea.getText().trim();



        String sql =
            "INSERT INTO gridpeaks.hikes " +
            "(peakname,dayclimbed,monthclimbed," +
            "yearclimbed,peakrank,companions,trail,comment,entered_by) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";



        try(Connection conn =
                DriverManager.getConnection(URL,USER,PASSWORD);

            PreparedStatement ps =
                conn.prepareStatement(sql)) {



            ps.setString(1,peak);
            ps.setInt(2,day);
            ps.setInt(3,month);
            ps.setInt(4,year);
            ps.setInt(5,rank);
            ps.setString(6,companion);
            ps.setString(7,trail);
            ps.setString(8,comment);
            ps.setString(9,enteredBy);



            ps.executeUpdate();


            JOptionPane.showMessageDialog(
                this,
                "Hike saved.");

            companionCombo.setSelectedIndex(0);
            companion2Combo.setSelectedIndex(0);
            
            newCompanion1Field.setText("");
            newCompanion2Field.setText("");
            newTrailField.setText("");
            trailCombo.setSelectedIndex(0);

        } catch(Exception ex) {

            showError(ex);
        }
    }

    private void deleteHike() {

        String peak =
            (String) peakCombo.getSelectedItem();

        if (peak == null)
            return;

        int rank = getPeakRank(peak);

        String companion1 =
            companionCombo.getSelectedItem() == null
                ? ""
                : companionCombo.getSelectedItem().toString().trim();

        String companion2 =
            companion2Combo.getSelectedItem() == null
                ? ""
                : companion2Combo.getSelectedItem().toString().trim();

        String companion = "";

        if (!companion1.isEmpty())
            companion = companion1;

        if (!companion2.isEmpty()) {

            if (!companion.isEmpty())
                companion += ", ";

            companion += companion2;
        }

        String trail =
            trailCombo.getSelectedItem() == null
                ? ""
                : trailCombo.getSelectedItem().toString().trim();

        int month =
            (Integer) monthCombo.getSelectedItem();

        int day =
            (Integer) dayCombo.getSelectedItem();

        int year =
            (Integer) yearCombo.getSelectedItem();

        String enteredBy =
            (String) userCombo.getSelectedItem();

        String sql =
            "DELETE FROM gridpeaks.hikes " +
            "WHERE entered_by=? " +
            "AND peakrank=? " +
            "AND yearclimbed=? " +
            "AND monthclimbed=? " +
            "AND dayclimbed=? " +
            "AND COALESCE(companions,'') = COALESCE(?, '') " +
            "AND COALESCE(trail,'') = COALESCE(?, '')";

        try (Connection conn =
                 DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, enteredBy);
            ps.setInt(2, rank);
            ps.setInt(3, year);
            ps.setInt(4, month);
            ps.setInt(5, day);
            ps.setString(6, companion);
            ps.setString(7, trail);

            int count = ps.executeUpdate();

            JOptionPane.showMessageDialog(
                this,
                count + " hike deleted.");

        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void showError(Exception ex) {

        ex.printStackTrace();


        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new HikeEntryForm().setVisible(true);

        });
    }

    private void runCompanionsReport() {
        try {
            companions.main(new String[0]);
            JOptionPane.showMessageDialog(this,
                    "Companion report generated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runMyList() {
        try {
            mylist.main(new String[0]);
            JOptionPane.showMessageDialog(this,
                    "Multiple mountains report generated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runAlanPeaks() {
        try {
            alanpeaks.main(new String[0]);
            JOptionPane.showMessageDialog(this,
                    "Alan total list mountains report generated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runGridMountainsLeft() {
        try {
            gridmountainsleft.main(new String[0]);
            JOptionPane.showMessageDialog(this,
                    "Grid progress report generated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runNH48HtmlExport() {
        try {
            NH48HtmlExport.main(new String[0]);
            JOptionPane.showMessageDialog(this,
                    "Total NH48 report generated.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}