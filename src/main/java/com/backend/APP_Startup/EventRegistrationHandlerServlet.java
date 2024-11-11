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

public class EventRegistrationHandlerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/myDatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String event = request.getParameter("event");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // Database connection setup
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // SQL insert statement
            String sql = "INSERT INTO event_registrations (event_name, registrant_name, registrant_email) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, event);
            statement.setString(2, name);
            statement.setString(3, email);

            // Execute the insert
            int rowsAffected = statement.executeUpdate();

            // Check if insertion was successful
            if (rowsAffected > 0) {
                // Redirect to thank you page if successful
                response.sendRedirect("thank-you-register.html");
            } else {
                // Display error if insertion fails
                response.getWriter().println("Failed to register for the event. Please try again.");
            }

            // Close resources
            statement.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("An error occurred during registration: " + e.getMessage());
        }
    }
}
