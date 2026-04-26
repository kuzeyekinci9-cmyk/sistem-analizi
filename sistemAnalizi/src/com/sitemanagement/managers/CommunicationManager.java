package com.sitemanagement.managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.sitemanagement.db.DatabaseHelper;
import com.sitemanagement.models.Announcement;
import com.sitemanagement.models.Poll;
import com.sitemanagement.services.ICommunicationService;

public class CommunicationManager implements ICommunicationService {

    @Override
    public boolean publishAnnouncement(String title, String content) {
        String query = "INSERT INTO Announcements (title, content) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        String query = "SELECT * FROM Announcements ORDER BY publish_date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Announcement(rs.getInt("id"), rs.getString("title"), rs.getString("content")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean createPoll(String question, String[] options) {
        String pollQuery = "INSERT INTO Polls (question) VALUES (?)";
        String optionQuery = "INSERT INTO Poll_Options (poll_id, option_text) VALUES (?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pollStmt = conn.prepareStatement(pollQuery, Statement.RETURN_GENERATED_KEYS)) {
                pollStmt.setString(1, question);
                pollStmt.executeUpdate();
                
                try (ResultSet generatedKeys = pollStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int pollId = generatedKeys.getInt(1);
                        try (PreparedStatement optStmt = conn.prepareStatement(optionQuery)) {
                            for (String option : options) {
                                optStmt.setInt(1, pollId);
                                optStmt.setString(2, option);
                                optStmt.addBatch();
                            }
                            optStmt.executeBatch();
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException ex) { conn.rollback(); return false; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean castVote(int pollId, int residentId, String optionText) {
        String checkQuery = "SELECT * FROM Poll_Votes WHERE poll_id = ? AND resident_id = ?";
        String voteQuery = "INSERT INTO Poll_Votes (poll_id, resident_id) VALUES (?, ?)";
        // SQL Injection ve ID karmaşasını önlemek için metin ile eşleşme yapıyoruz
        String updateOptQuery = "UPDATE Poll_Options SET vote_count = vote_count + 1 WHERE poll_id = ? AND option_text = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, pollId);
            checkStmt.setInt(2, residentId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) return false;

            conn.setAutoCommit(false);
            try (PreparedStatement voteStmt = conn.prepareStatement(voteQuery);
                 PreparedStatement updateStmt = conn.prepareStatement(updateOptQuery)) {
                 
                voteStmt.setInt(1, pollId);
                voteStmt.setInt(2, residentId);
                voteStmt.executeUpdate();

                updateStmt.setInt(1, pollId);
                updateStmt.setString(2, optionText);
                updateStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) { conn.rollback(); return false; }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override 
    public Poll getPollDetails(int pollId) { 
        String pollQuery = "SELECT question FROM Polls WHERE id = ?";
        String optionsQuery = "SELECT option_text, vote_count FROM Poll_Options WHERE poll_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pollStmt = conn.prepareStatement(pollQuery);
             PreparedStatement optStmt = conn.prepareStatement(optionsQuery)) {
            
            pollStmt.setInt(1, pollId);
            ResultSet rsPoll = pollStmt.executeQuery();
            if (rsPoll.next()) {
                Poll poll = new Poll(pollId, rsPoll.getString("question"), new String[0]);
                optStmt.setInt(1, pollId);
                ResultSet rsOpt = optStmt.executeQuery();
                while(rsOpt.next()) {
                    poll.getOptions().put(rsOpt.getString("option_text"), rsOpt.getInt("vote_count"));
                }
                return poll;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; 
    }
    
    @Override 
    public List<Poll> getActivePolls() { 
        List<Poll> list = new ArrayList<>();
        String query = "SELECT * FROM Polls";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Poll(rs.getInt("id"), rs.getString("question"), new String[0]));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list; 
    }
}