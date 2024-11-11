package com.backend.APP_Startup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data from request
        String role = request.getParameter("role");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Database connection settings
        String dbURL = "jdbc:mysql://localhost:3306/mydatabase";
        String dbUser = "root";
        String dbPass = "";

        try {
            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Check if the email already exists
            String checkEmailQuery = "SELECT email, password FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery);
            checkStmt.setString(1, email);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                // Email already exists, check if the password matches
                String storedPassword = resultSet.getString("password");

                if (storedPassword.equals(password)) {
                    // If email and password match, redirect to resourcedirectory.html
                    response.sendRedirect("resoucedirectory.html");
                } else {
                    // If the password doesn't match, send back an error message
                    response.getWriter().println("<html><body><script>alert('Incorrect password. Please try again.'); window.location='signup.html';</script></body></html>");
                }
            } else {
                // If email doesn't exist, proceed with registration
                String query = "INSERT INTO users (role, name, email, password) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, role);
                statement.setString(2, name);
                statement.setString(3, email);
                statement.setString(4, password);

                // Execute the insert
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Redirect to profile.html if sign-up is successful
                    response.sendRedirect("profile.html");
                } else {
                    response.getWriter().println("<html><body><script>alert('Failed to sign up. Please try again.'); window.location='signup.html';</script></body></html>");
                }

                // Close resources for the insert statement
                statement.close();
            }

            // Close resources for the check statement
            checkStmt.close();
            resultSet.close();
            conn.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("<html><body><script>alert('An error occurred: " + e.getMessage() + "'); window.location='signup.html';</script></body></html>");
        }
    }
}
