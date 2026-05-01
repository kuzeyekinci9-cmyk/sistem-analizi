package com.sitemanagement.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Poll {
    private int pollId;
    private String question;
    private Map<String, Integer> options; // Örn: ["Evet" -> 15 oy, "Hayır" -> 5 oy]
    private Set<Integer> votedResidentIds; // Aynı sakinin 2. kez oy vermesini engellemek için

    public Poll(int pollId, String question, String[] initialOptions) {
        this.pollId = pollId;
        this.question = question;
        this.options = new HashMap<>();
        this.votedResidentIds = new HashSet<>();

        // Seçenekleri sıfır oy ile başlatıyoruz
        for (String option : initialOptions) {
            this.options.put(option, 0);
        }
    }

    // Oy verme işlemini doğrudan model içinde güvenli bir şekilde yapıyoruz
    public boolean castVote(int residentId, String selectedOption) {
        // Sakin daha önce oy kullanmış mı veya seçilen opsiyon geçerli mi kontrolü
        if (votedResidentIds.contains(residentId) || !options.containsKey(selectedOption)) {
            return false;
        }

        // Oyu artır ve sakini listeye ekle
        options.put(selectedOption, options.get(selectedOption) + 1);
        votedResidentIds.add(residentId);
        return true;
    }

    // Getters
    public int getPollId() {
        return pollId;
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, Integer> getOptions() {
        return options;
    }

    public Set<Integer> getVotedResidentIds() {
        return votedResidentIds;
    }
}