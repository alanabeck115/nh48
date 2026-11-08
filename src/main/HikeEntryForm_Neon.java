package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeSet;
import java.math.BigDecimal;

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
import java.io.PrintWriter;
import java.io.FileWriter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
/*
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;


import java.util.Properties;

jakarta.mail.Session session =
jakarta.mail.Session.getInstance(properties,
    new jakarta.mail.Authenticator() {
        @Override
        protected jakarta.mail.PasswordAuthentication
                getPasswordAuthentication() {
            return new jakarta.mail.PasswordAuthentication(
                emailUsername, emailPassword);
        }
    });
*/
public class HikeEntryForm_Neon extends JFrame {

    // Neon connection settings are supplied through Windows environment variables.
    // GRIDPEAKS_DB_URL must be a JDBC URL ending in /gridpeaks?sslmode=require.
    private static final String URL = System.getenv("GRIDPEAKS_DB_URL");
    private static final String USER = System.getenv("GRIDPEAKS_DB_USER");
    private static final String PASSWORD = System.getenv("GRIDPEAKS_DB_PASSWORD");

    // Single-user mode for now. The existing Neon user row is Alan (userid 1).
    private static final String ACTIVE_USERNAME = "Alan";


    private JComboBox<String> listCombo;
    private JComboBox<String> peakCombo;
    private JComboBox<String> companionCombo;
    private JComboBox<String> companion2Combo;
    private JComboBox<String> companion3Combo;
    private JComboBox<String> trailCombo;
    private JComboBox<String> userCombo;
    private JComboBox<String> companionHistoryCombo;
    

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
    private JTextField newListField;
    private JTextField newPeakField;
    private JTextField newUserField;
    private JTextField newCompanion1Field;
    private JTextField newCompanion2Field;
    private JTextField newCompanion3Field;
    private JTextField newTrailField;
    private JTextField emailField;
    

    private void triggerDeploy() {

         
        String token = System.getenv("GITHUB_TOKEN");

        if (token == null || token.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "GITHUB_TOKEN environment variable is not set.",
                "Deployment Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {


        	String apiUrl =
        	    "https://api.github.com/repos/"
    			+ "alanabeck115/nh48"
    			+ "/actions/workflows/deploy.yml/dispatches";

        	if (token == null || token.trim().isEmpty()) {
        	    throw new IllegalStateException(
        	        "GITHUB_TOKEN environment variable is missing.");
        	}

        	URL url = new URL(apiUrl);
        	HttpURLConnection connection =
        	    (HttpURLConnection) url.openConnection();

        	connection.setRequestMethod("POST");
        	connection.setRequestProperty(
        	    "Accept", "application/vnd.github+json");
        	connection.setRequestProperty(
        	    "Authorization", "Bearer " + token.trim());
        	connection.setRequestProperty(
        	    "Content-Type", "application/json");
        	connection.setDoOutput(true);

        	String body = "{\"ref\":\"main\"}";

        	try (OutputStream os = connection.getOutputStream()) {
        	    os.write(body.getBytes(StandardCharsets.UTF_8));
        	}

        	int status = connection.getResponseCode();

        	System.out.println("GitHub HTTP Status: " + status);

        	if (status == 204) {
        	    System.out.println("Deployment workflow dispatched successfully.");
        	} else {
        	    java.io.InputStream errorStream = connection.getErrorStream();

        	    if (errorStream != null) {
        	        try (java.io.InputStream es = errorStream) {
        	            System.out.println(
        	                new String(es.readAllBytes(), StandardCharsets.UTF_8));
        	        }
        	    }
        	}

        	connection.disconnect();
        	//
        	
            String json = "{\"ref\":\"main\"}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header(
                    "Accept",
                    "application/vnd.github+json"
                )
                .header(
                    "Authorization",
                    "Bearer " + token
                )
                .header(
                    "X-GitHub-Api-Version",
                    "2022-11-28"
                )
                .header(
                    "Content-Type",
                    "application/json"
                )
                .POST(
                    HttpRequest.BodyPublishers.ofString(json)
                )
                .build();

            HttpResponse<String> response =
                client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
                );

            if (response.statusCode() == 204) {

                JOptionPane.showMessageDialog(
                    this,
                    "GitHub deployment workflow triggered successfully."
                );

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "GitHub deployment failed.\n\n"
                    + "HTTP Status: "
                    + response.statusCode()
                    + "\n\n"
                    + response.body(),
                    "Deployment Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Unable to trigger GitHub deployment:\n\n"
                + ex.getMessage(),
                "Deployment Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public HikeEntryForm() {

        setTitle("GridPeaks Hike Entry");
        setSize(1100, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        validateDatabaseSettings();
        createUI();

        initializeListsTable();
        loadLists();
        loadUsers();
        userCombo.setSelectedItem(ACTIVE_USERNAME);
        loadPeaks();
        loadCompanions();
        loadTrails();
    }

    private void validateDatabaseSettings() {
        if (URL == null || URL.trim().isEmpty() ||
                USER == null || USER.trim().isEmpty() ||
                PASSWORD == null || PASSWORD.isEmpty()) {
            throw new IllegalStateException(
                "Set GRIDPEAKS_DB_URL, GRIDPEAKS_DB_USER, and " +
                "GRIDPEAKS_DB_PASSWORD before starting HikeEntryForm.");
        }
        if (!URL.startsWith("jdbc:postgresql://") ||
                !URL.contains("/gridpeaks")) {
            throw new IllegalStateException(
                "GRIDPEAKS_DB_URL must be a PostgreSQL JDBC URL for the gridpeaks database.");
        }
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

        // Refresh the Peak dropdown whenever a different list is selected.
        listCombo.addActionListener(e -> loadPeaks());

        companionCombo =
                new JComboBox<>();
        
        companion2Combo =
                new JComboBox<>(); 
        
        companion3Combo =
                new JComboBox<>(); 

        trailCombo =
                new JComboBox<>();
        
        companionHistoryCombo = new JComboBox<>();
        
        newListField = new JTextField(12);
        newPeakField = new JTextField(12);
        newUserField = new JTextField(12);
        newCompanion1Field = new JTextField(12);
        newCompanion2Field = new JTextField(12);
        newCompanion3Field = new JTextField(12);
        newTrailField = new JTextField(12);
        emailField = new JTextField(22);

        userCombo = new JComboBox<>();

        companionReportButton = new JButton("Companion Report");
        multipleMountainsButton = new JButton("Multiple Mountains Report");
        alanTotalListButton = new JButton("Alan Total List Mountains Report");
        gridProgressButton = new JButton("Grid Progress Report");
        totalNH48Button = new JButton("Total NH48 Report");
        Hikes2026Button = new JButton("2026 Hikes Report");
        
        companionCombo.addItem("");
        companion2Combo.addItem("");
        companion3Combo.addItem("");
        trailCombo.addItem("");







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

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        userPanel.add(userCombo);
        userPanel.add(new JLabel("New:"));
        userPanel.add(newUserField);
        JButton addUserButton = new JButton("Add User");
        addUserButton.setEnabled(false); // Registration is disabled in single-user mode.
        userPanel.add(addUserButton);
        addField(panel, gc, row++, "User:", userPanel);

        JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        listPanel.add(listCombo);
        listPanel.add(new JLabel("New:"));
        listPanel.add(newListField);
        JButton addListButton = new JButton("Add List");
        addListButton.addActionListener(e -> addList());
        listPanel.add(addListButton);
        addField(panel, gc, row++, "List:", listPanel);

        JPanel peakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        peakPanel.add(peakCombo);
        peakPanel.add(new JLabel("New:"));
        peakPanel.add(newPeakField);
        JButton addPeakButton = new JButton("Add Peak");
        addPeakButton.addActionListener(e -> addPeak());
        peakPanel.add(addPeakButton);
        addField(panel, gc, row++, "Peak:", peakPanel);


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
        
        JPanel comp3Panel = new JPanel();
        comp3Panel.add(companion3Combo);
        comp3Panel.add(new JLabel("New:"));
        comp3Panel.add(newCompanion3Field);

        addField(panel, gc, row++,
                "Companion 3:",
                comp3Panel);
        
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

        JPanel companionHistoryPanel = new JPanel(
        	    new FlowLayout(FlowLayout.LEFT));

        companionHistoryPanel.add(
            new JLabel("Companion Hike History:"));

        companionHistoryPanel.add(companionHistoryCombo);

        companionHistoryPanel.add(
        	    new JLabel("Email:"));

        	companionHistoryPanel.add(emailField);
        	
        	JButton submitButton = new JButton("Submit");
        	companionHistoryPanel.add(submitButton);
        	
        	submitButton.addActionListener(e -> {
        	    String recipient = emailField.getText().trim();

        	    // Validate the email only if one was entered.
        	    if (!recipient.isEmpty() &&
        	            !recipient.matches(
        	                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        	        JOptionPane.showMessageDialog(
        	            this,
        	            "Please enter a valid email address.",
        	            "Invalid Email",
        	            JOptionPane.WARNING_MESSAGE);
        	        emailField.requestFocus();
        	        return;
        	    }

        	    // Generate the report whether or not an email was entered.
        	    generateCompanionHikeHistory();
        	});

        	
        loadCompanionHistoryList();

        gc.gridx = 0;
        gc.gridy = row + 2;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(companionHistoryPanel, gc);

        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.NONE;
        
        

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
        });

        buttons.add(deployButton);


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
    // Keep list names independently of mountain rows. Existing comma-separated
    // mountains.list values are copied into this table the first time the form runs.
    private void initializeListsTable() {
        // The lists table already exists in Neon; only seed any missing names.
        String seedSql =
            "INSERT INTO gridpeaks.lists (listname) " +
            "SELECT DISTINCT trim(value) " +
            "FROM gridpeaks.mountains m, " +
            "unnest(string_to_array(m.list, ',')) AS values_list(value) " +
            "WHERE m.list IS NOT NULL AND trim(value) <> '' " +
            "ON CONFLICT (listname) DO NOTHING";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement seed = conn.prepareStatement(seedSql)) {
            seed.executeUpdate();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadLists() {
        listCombo.removeAllItems();
        String sql =
            "SELECT listname FROM gridpeaks.lists ORDER BY listname";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listCombo.addItem(rs.getString("listname"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadUsers() {
        userCombo.removeAllItems();
        String sql =
            "SELECT username FROM gridpeaks.users WHERE username = ? ORDER BY username";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ACTIVE_USERNAME);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userCombo.addItem(rs.getString("username"));
                }
            }
            if (userCombo.getItemCount() == 0) {
                throw new IllegalStateException(
                    "The configured user '" + ACTIVE_USERNAME +
                    "' was not found in gridpeaks.users.");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addUser() {
        String username = newUserField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a user name in the New: box.");
            return;
        }
        String sql = "INSERT INTO gridpeaks.users (username) VALUES (?) " +
                     "ON CONFLICT (username) DO NOTHING";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int inserted = ps.executeUpdate();
            loadUsers();
            userCombo.setSelectedItem(username);
            newUserField.setText("");
            if (inserted == 0) {
                JOptionPane.showMessageDialog(this, "That user already exists; selected it.");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addList() {
        String listName = newListField.getText().trim();
        if (listName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a list name in the New: box.");
            return;
        }
        String sql = "INSERT INTO gridpeaks.lists (listname) VALUES (?) " +
                     "ON CONFLICT (listname) DO NOTHING";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, listName);
            int inserted = ps.executeUpdate();
            loadLists();
            listCombo.setSelectedItem(listName);
            newListField.setText("");
            if (inserted == 0) {
                JOptionPane.showMessageDialog(this, "That list already exists; selected it.");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addPeak() {
        JTextField rankField = new JTextField(8);
        JTextField nameField = new JTextField(newPeakField.getText().trim(), 18);
        JTextField elevationField = new JTextField(10);
        JTextField stateField = new JTextField(14);
        JTextField trailField = new JTextField(18);

        JPanel fields = new JPanel(new GridLayout(0, 2, 5, 5));
        fields.add(new JLabel("Rank:")); fields.add(rankField);
        fields.add(new JLabel("Peak name:")); fields.add(nameField);
        fields.add(new JLabel("Elevation:")); fields.add(elevationField);
        fields.add(new JLabel("State:")); fields.add(stateField);
        fields.add(new JLabel("Trail:")); fields.add(trailField);

        int choice = JOptionPane.showConfirmDialog(
            this, fields, "Add Peak", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String state = stateField.getText().trim();
        String trail = trailField.getText().trim();
        if (name.isEmpty() || rankField.getText().trim().isEmpty() ||
                elevationField.getText().trim().isEmpty() || state.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Rank, peak name, elevation, and state are required.");
            return;
        }

        try {
            int rank = Integer.parseInt(rankField.getText().trim());
            BigDecimal elevation = new BigDecimal(elevationField.getText().trim());
            String sql =
                "INSERT INTO gridpeaks.mountains " +
                "(rank, name, elevation, state, list, trail) " +
                "VALUES (?, ?, ?, ?, '', ?)";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, rank);
                ps.setString(2, name);
                ps.setBigDecimal(3, elevation);
                ps.setString(4, state);
                ps.setString(5, trail.isEmpty() ? null : trail);
                ps.executeUpdate();
            }

            // The new mountain deliberately has no list membership, so it
            // will appear only when no list filter is selected (or after it
            // is assigned to a list in the database).
            loadPeaks();
            newPeakField.setText("");
            JOptionPane.showMessageDialog(this,
                "Peak added. It has not been assigned to a list.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Rank must be a whole number and elevation must be numeric.",
                "Invalid Peak Details", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            showError(ex);
        }
    }




    private void loadPeaks() {
        peakCombo.removeAllItems();

        String selectedList = (listCombo == null)
                ? null : (String) listCombo.getSelectedItem();

        String sql;
        if (selectedList == null || selectedList.trim().isEmpty()) {
            // No list selected: show every mountain.
            sql = "SELECT name FROM gridpeaks.mountains ORDER BY name";
        } else {
            // mountains.list is comma-separated and a mountain may belong
            // to more than one list (for example, 'nh48, highpoint').
            sql = "SELECT m.name " +
                  "FROM gridpeaks.mountains m " +
                  "WHERE EXISTS ( " +
                  "  SELECT 1 " +
                  "  FROM unnest(string_to_array(COALESCE(m.list, ''), ',')) AS item(tag) " +
                  "  WHERE lower(trim(item.tag)) = lower(trim(?)) " +
                  ") ORDER BY m.name";
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (selectedList != null && !selectedList.trim().isEmpty()) {
                ps.setString(1, selectedList);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    peakCombo.addItem(rs.getString("name"));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }


    private void loadCompanions() {

        companionCombo.removeAllItems();
        companion2Combo.removeAllItems();
        companion3Combo.removeAllItems();
        companionCombo.addItem("");
        companion2Combo.addItem("");
        companion3Combo.addItem("");
        


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
                companion3Combo.addItem(c);
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

    private void loadCompanionHistoryList() {

        companionHistoryCombo.removeAllItems();

        TreeSet<String> companions = new TreeSet<>();

        String sql =
            "SELECT companions " +
            "FROM gridpeaks.hikes " +
            "WHERE companions IS NOT NULL";

        try (Connection conn =
                 DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String value = rs.getString("companions");

                if (value != null) {
                    for (String name : value.split(",")) {
                        name = name.trim();

                        if (!name.isEmpty()) {
                            companions.add(name);
                        }
                    }
                }
            }

            for (String name : companions) {
                companionHistoryCombo.addItem(name);
            }

        } catch (Exception ex) {
            showError(ex);
        }
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

        String companion3 =
        	    companion3Combo.getSelectedItem() == null
        	        ? ""
        	        : companion3Combo.getSelectedItem()
        	                        .toString()
        	                        .trim();
        
        if (!newCompanion1Field.getText().trim().isEmpty()) {
            companion1 = newCompanion1Field.getText().trim();
        }

        if (!newCompanion2Field.getText().trim().isEmpty()) {
            companion2 = newCompanion2Field.getText().trim();
        }
        if (!newCompanion3Field.getText().trim().isEmpty()) {
            companion3 = newCompanion3Field.getText().trim();
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
        	if(!companion3.isEmpty()) {

        	    if(!companion.isEmpty())
        	        companion += ", ";

        	    companion += companion3;
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
            "yearclimbed,peakrank,companions,trail,comment,entered_by,userid) " +
            "VALUES (?,?,?,?,?,?,?,?,?, " +
            "(SELECT userid FROM gridpeaks.users WHERE username = ?))";



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
            ps.setString(10,enteredBy);

            ps.executeUpdate();


            JOptionPane.showMessageDialog(
                this,
                "Hike saved.");

            companionCombo.setSelectedIndex(0);
            companion2Combo.setSelectedIndex(0);
            companion3Combo.setSelectedIndex(0);

            newCompanion1Field.setText("");
            newCompanion2Field.setText("");
            newCompanion3Field.setText("");
            newTrailField.setText("");
            trailCombo.setSelectedIndex(0);
            loadCompanions();
            loadTrails();
            loadCompanionHistoryList();

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
        private void generateCompanionHikeHistory() {

            String companion =
                (String) companionHistoryCombo.getSelectedItem();

            if (companion == null || companion.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Please select a companion.");
                return;
            }

            String safeCompanion = companion.trim()
            	    .toLowerCase()
            	    .replaceAll("[^a-z0-9]+", "_")
            	    .replaceAll("^_|_$", "");

            String fileName = "companion_hike_history_" 
            	    + safeCompanion + ".html";

            String sql =
                "SELECT h.peakname, h.monthclimbed, " +
                "h.dayclimbed, h.yearclimbed, " +
                "h.companions, h.comment " +
                "FROM gridpeaks.hikes h " +
                "WHERE EXISTS ( " +
                "  SELECT 1 " +
                "  FROM unnest(string_to_array(h.companions, ',')) AS c " +
                "  WHERE trim(c) = ? " +
                ") " +
                "ORDER BY h.yearclimbed DESC, " +
                "h.monthclimbed DESC, h.dayclimbed DESC, " +
                "h.peakname ASC";

            int count = 0;

            try (Connection conn =
                     DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, companion);

                try (ResultSet rs = ps.executeQuery();
                     PrintWriter out = new PrintWriter(
                         new FileWriter(fileName))) {

                    out.println("<!DOCTYPE html>");
                    out.println("<html><head>");
                    out.println("<meta charset='UTF-8'>");
                    out.println("<title>Companion Hike History</title>");

                    out.println("<style>");
                    out.println("body { font-family: Arial, sans-serif; "
                        + "margin: 25px; }");
                    out.println("table { border-collapse: collapse; "
                        + "width: 100%; }");
                    out.println("th, td { border: 1px solid #ccc; "
                        + "padding: 8px; text-align: left; }");
                    out.println("th { background: #e5e5e5; }");
                    out.println("tr:nth-child(even) { background: #f5f5f5; }");
                    out.println("</style>");

                    out.println("</head><body>");

                    out.println("<h1>Companion Hike History</h1>");
                    out.println("<h2>Companion: "
                        + htmlEscape(companion) + "</h2>");

                    out.println("<table>");
                    out.println("<tr>");
                    out.println("<th>Peak Name</th>");
                    out.println("<th>Month</th>");
                    out.println("<th>Day</th>");
                    out.println("<th>Year</th>");
                    out.println("<th>Companions</th>");
                    out.println("<th>Notes</th>");
                    out.println("</tr>");

                    while (rs.next()) {
                        out.println("<tr>");

                        out.println("<td>" +
                            htmlEscape(rs.getString("peakname")) +
                            "</td>");

                        out.println("<td>" +
                            rs.getInt("monthclimbed") +
                            "</td>");

                        out.println("<td>" +
                            rs.getInt("dayclimbed") +
                            "</td>");

                        out.println("<td>" +
                            rs.getInt("yearclimbed") +
                            "</td>");

                        out.println("<td>" +
                            htmlEscape(rs.getString("companions")) +
                            "</td>");

                        out.println("<td>" +
                            htmlEscape(rs.getString("comment")) +
                            "</td>");

                        out.println("</tr>");

                        count++;
                    }
                    

                    out.println("</table>");
                    out.println("<p>Total hikes: " + count + "</p>");
                    out.println("</body></html>");
                    
                }


                
                openHtml(fileName);

               

            } catch (Exception ex) {
                showError(ex);
            }
        }
                
        private String htmlEscape(String value) {

            if (value == null) {
                return "";
            }

            return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
        }
    }
