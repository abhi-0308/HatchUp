package com.backend.APP_Startup;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MentorMatchingServlet")
public class MentorMatchingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection connection = getConnection()) {
            if ("findMentors".equals(action)) {
                findMentors(request, response, connection, out);
            } else if ("scheduleAppointment".equals(action)) {
                scheduleAppointment(request, connection, out);
            } else if ("submitFeedback".equals(action)) {
                submitFeedback(request, connection, out);
            } else {
                out.println("Invalid action specified.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Database connection error: " + e.getMessage());
        }
    }

    private void findMentors(HttpServletRequest request, HttpServletResponse response, Connection connection, PrintWriter out)
            throws SQLException {
        String expertise = request.getParameter("expertise");
        String industry = request.getParameter("industry");
        String location = request.getParameter("location");

        String query = "SELECT * FROM mentors WHERE expertise = ? AND industry = ? AND location = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, expertise);
            stmt.setString(2, industry);
            stmt.setString(3, location);

            ResultSet rs = stmt.executeQuery();
            out.println("<ul>");
            while (rs.next()) {
                out.println("<li>");
                out.println("<h3>" + rs.getString("name") + "</h3>");
                out.println("<p>Expertise: " + rs.getString("expertise") + "</p>");
                out.println("<p>Industry: " + rs.getString("industry") + "</p>");
                out.println("<p>Location: " + rs.getString("location") + "</p>");
                out.println("</li>");
            }
            out.println("</ul>");
        }
    }

    private void scheduleAppointment(HttpServletRequest request, Connection connection, PrintWriter out)
            throws SQLException {
        String mentorName = request.getParameter("mentorName");
        String appointmentDate = request.getParameter("appointmentDate");
        String appointmentTime = request.getParameter("appointmentTime");

        String query = "INSERT INTO appointments (mentor_name, appointment_date, appointment_time) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mentorName);
            stmt.setString(2, appointmentDate);
            stmt.setString(3, appointmentTime);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                out.println("Appointment scheduled successfully with " + mentorName + " on " + appointmentDate + " at " + appointmentTime + ".");
            } else {
                out.println("Failed to schedule appointment.");
            }
        }
    }

    private void submitFeedback(HttpServletRequest request, Connection connection, PrintWriter out)
            throws SQLException {
        String feedbackMentor = request.getParameter("feedbackMentor");
        String feedbackText = request.getParameter("feedbackText");

        String query = "INSERT INTO feedback (mentor_name, feedback) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, feedbackMentor);
            stmt.setString(2, feedbackText);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                out.println("Feedback submitted successfully for " + feedbackMentor + ".");
            } else {
                out.println("Failed to submit feedback.");
            }
        }
    }
}
