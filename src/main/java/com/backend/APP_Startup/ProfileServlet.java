package com.backend.APP_Startup;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/myDatabase"; // Replace with your database URL
    private static final String DB_USER = "root";  // Replace with your MySQL username
    private static final String DB_PASS = "";  // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email"); // Retrieve email, assumed as unique identifier

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection to the database
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Query to fetch user profile data from users table
            String userQuery = "SELECT role, name, email FROM users WHERE email = ?";
            PreparedStatement userStatement = conn.prepareStatement(userQuery);
            userStatement.setString(1, email);

            ResultSet userRs = userStatement.executeQuery();

            // If user exists, populate the form with data
            if (userRs.next()) {
                String role = userRs.getString("role");
                String name = userRs.getString("name");
                String userEmail = userRs.getString("email");

                out.println("<html><body>");
                out.println("<h1>Edit Profile</h1>");
                out.println("<form action='profile' method='post'>");

                // Display existing role, name, email
                out.println("<label>Role (from Users):</label>");
                out.println("<input type='text' name='role' value='" + role + "' disabled><br>");

                out.println("<label>Name (from Users):</label>");
                out.println("<input type='text' name='name' value='" + name + "' required><br>");

                out.println("<label>Email (from Users):</label>");
                out.println("<input type='email' name='email' value='" + userEmail + "' disabled><br>");

                // Additional fields for the profile
                out.println("<label>Phone:</label>");
                out.println("<input type='tel' name='phone'><br>");

                out.println("<label>LinkedIn:</label>");
                out.println("<input type='text' name='linkedin'><br>");

                out.println("<label>Location:</label>");
                out.println("<input type='text' name='location'><br>");

                // Role-specific fields based on role selection
                if (role.equals("investor")) {
                    out.println("<label>Investment Range:</label>");
                    out.println("<input type='text' name='investmentRange'><br>");

                    out.println("<label>Preferred Funding Stages:</label>");
                    out.println("<input type='text' name='investmentStage'><br>");

                    out.println("<label>Geographical Preferences:</label>");
                    out.println("<input type='text' name='geographicalPreferences'><br>");
                } else if (role.equals("mentor")) {
                    out.println("<label>Areas of Expertise:</label>");
                    out.println("<input type='text' name='areasOfExpertise'><br>");

                    out.println("<label>Previous Mentorships:</label>");
                    out.println("<textarea name='previousMentorships'></textarea><br>");

                    out.println("<label>Availability:</label>");
                    out.println("<input type='text' name='availability'><br>");
                } else if (role.equals("startup")) {
                    out.println("<label>Startup Name:</label>");
                    out.println("<input type='text' name='startupName'><br>");

                    out.println("<label>Stage:</label>");
                    out.println("<input type='text' name='startupStage'><br>");

                    out.println("<label>Product Description:</label>");
                    out.println("<textarea name='productDescription'></textarea><br>");

                    out.println("<label>Startup Needs:</label>");
                    out.println("<textarea name='startupNeeds'></textarea><br>");
                }

                out.println("<button type='submit'>Save Changes</button>");
                out.println("</form>");
                out.println("</body></html>");
            } else {
                out.println("User not found for email: " + email);
            }

            userRs.close();
            userStatement.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            out.println("An error occurred while retrieving the profile.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String role = request.getParameter("role");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String linkedin = request.getParameter("linkedin");
        String location = request.getParameter("location");

        // Database connection parameters
        String dbURL = "jdbc:mysql://localhost:3306/myDatabase";
        String dbUser = "root";
        String dbPass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Determine query and parameters based on role
            String query;
            PreparedStatement statement;

            if ("investor".equals(role)) {
                // Investor-specific fields
                String investmentRange = request.getParameter("investmentRange");
                String investmentStage = request.getParameter("investmentStage");
                String geographicalPreferences = request.getParameter("geographicalPreferences");

                query = "INSERT INTO profiles (role, name, email, phone, linkedin, location, investmentRange, investmentStage, geographicalPreferences) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE name=?, email=?, phone=?, linkedin=?, location=?, investmentRange=?, investmentStage=?, geographicalPreferences=?";
                
                statement = conn.prepareStatement(query);
                statement.setString(1, role);
                statement.setString(2, name);
                statement.setString(3, email);
                statement.setString(4, phone);
                statement.setString(5, linkedin);
                statement.setString(6, location);
                statement.setString(7, investmentRange);
                statement.setString(8, investmentStage);
                statement.setString(9, geographicalPreferences);

                // Set parameters for update
                statement.setString(10, name);
                statement.setString(11, email);
                statement.setString(12, phone);
                statement.setString(13, linkedin);
                statement.setString(14, location);
                statement.setString(15, investmentRange);
                statement.setString(16, investmentStage);
                statement.setString(17, geographicalPreferences);

            } else if ("mentor".equals(role)) {
                // Mentor-specific fields
                String areasOfExpertise = request.getParameter("areasOfExpertise");
                String previousMentorships = request.getParameter("previousMentorships");
                String availability = request.getParameter("availability");

                query = "INSERT INTO profiles (role, name, email, phone, linkedin, location, areasOfExpertise, previousMentorships, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE name=?, email=?, phone=?, linkedin=?, location=?, areasOfExpertise=?, previousMentorships=?, availability=?";
                
                statement = conn.prepareStatement(query);
                statement.setString(1, role);
                statement.setString(2, name);
                statement.setString(3, email);
                statement.setString(4, phone);
                statement.setString(5, linkedin);
                statement.setString(6, location);
                statement.setString(7, areasOfExpertise);
                statement.setString(8, previousMentorships);
                statement.setString(9, availability);

                // Set parameters for update
                statement.setString(10, name);
                statement.setString(11, email);
                statement.setString(12, phone);
                statement.setString(13, linkedin);
                statement.setString(14, location);
                statement.setString(15, areasOfExpertise);
                statement.setString(16, previousMentorships);
                statement.setString(17, availability);

            } else if ("startup".equals(role)) {
                // Startup-specific fields
                String startupName = request.getParameter("startupName");
                String startupStage = request.getParameter("startupStage");
                String productDescription = request.getParameter("productDescription");
                String startupNeeds = request.getParameter("startupNeeds");

                query = "INSERT INTO profiles (role, name, email, phone, linkedin, location, startupName, startupStage, productDescription, startupNeeds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE name=?, email=?, phone=?, linkedin=?, location=?, startupName=?, startupStage=?, productDescription=?, startupNeeds=?";
                
                statement = conn.prepareStatement(query);
                statement.setString(1, role);
                statement.setString(2, name);
                statement.setString(3, email);
                statement.setString(4, phone);
                statement.setString(5, linkedin);
                statement.setString(6, location);
                statement.setString(7, startupName);
                statement.setString(8, startupStage);
                statement.setString(9, productDescription);
                statement.setString(10, startupNeeds);

                // Set parameters for update
                statement.setString(11, name);
                statement.setString(12, email);
                statement.setString(13, phone);
                statement.setString(14, linkedin);
                statement.setString(15, location);
                statement.setString(16, startupName);
                statement.setString(17, startupStage);
                statement.setString(18, productDescription);
                statement.setString(19, startupNeeds);
            } else {
                throw new SQLException("Invalid role");
            }

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Redirect to profile.html if sign-up is successful
                response.sendRedirect("resoucedirectory.html");
            } else {
                response.getWriter().println("Failed to sign up. Please try again.");
            }

            // Close resources
            statement.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("An error occurred while saving your data: " + e.getMessage());
        }
    }
}
