package com.sitemanagement.managers;

import java.util.List;

import com.sitemanagement.models.Announcement;
import com.sitemanagement.models.Poll;
import com.sitemanagement.services.ICommunicationService;

public class CommunicationManager implements ICommunicationService{

	@Override
	public boolean publishAnnouncement(String title, String content) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Announcement> getAllAnnouncements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createPoll(String question, String[] options) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean castVote(int pollId, int residentId, String selectedOption) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Poll getPollDetails(int pollId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Poll> getActivePolls() {
		// TODO Auto-generated method stub
		return null;
	}

}
