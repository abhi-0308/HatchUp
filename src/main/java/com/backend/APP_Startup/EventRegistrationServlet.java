package com.backend.APP_Startup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/myDatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String eventName = request.getParameter("event-name");
        String eventDate = request.getParameter("event-date");
        String eventLocation = request.getParameter("event-location");
        String eventDescription = request.getParameter("event-description");

        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // SQL to insert the event data
            String sql = "INSERT INTO events (event_name, event_date, event_location, event_description) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, eventName);
            statement.setString(2, eventDate);
            statement.setString(3, eventLocation);
            statement.setString(4, eventDescription);

            // Execute the insert
            int rowsAffected = statement.executeUpdate();

            // Check if data was inserted
            if (rowsAffected > 0) {
                // Redirect to thank you page if successful
                response.sendRedirect("thank-you-host.html");
            } else {
                // Display error message if insert failed
                response.getWriter().println("Failed to register event. Please try again.");
            }

            // Close resources
            statement.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("An error occurred while registering the event: " + e.getMessage());
        }
    }
}
