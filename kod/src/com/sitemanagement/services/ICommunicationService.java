package com.sitemanagement.services;

import com.sitemanagement.models.Announcement;
import com.sitemanagement.models.Poll;
import java.util.List;

public interface ICommunicationService {
    boolean publishAnnouncement(String title, String content);
    List<Announcement> getAllAnnouncements();
    
    boolean createPoll(String question, String[] options);
    boolean castVote(int pollId, int residentId, String selectedOption);
    Poll getPollDetails(int pollId);
    List<Poll> getActivePolls();
}