package com.zgamelogic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class ModerationService {
    private final List<String> bannedWords;
    private long lastModified;

    public ModerationService() {
        this.bannedWords = new ArrayList<>();
        lastModified = 0;
        refreshWordList();
    }

    /**
     * Checks a string message against a list of banned words to see if a word is in that message
     * @param message Message to check against
     * @return True if the message contains a banned word
     */
    public boolean isBannedWordIncluded(String message){
        message = message.toLowerCase().trim();
        return bannedWords.stream()
                .map(message::contains)
                .reduce(false, (prev, cur) -> prev || cur);
    }

    /**
     * Refreshes the banned word list. Runs every 5 minutes.
     * Check is based off the file update time, if that has changed it refreshes the list.
     */
    @Scheduled(cron = "0 */5 * * * *")
    private void refreshWordList() {
        File wordsFile = new File("banned-words.txt");
        long currentFileUpdate = wordsFile.lastModified();
        if(currentFileUpdate == lastModified) return;
        lastModified = currentFileUpdate;
        if(!wordsFile.exists()) return;
        log.info("Banned word list changed detected. Updating list.");
        bannedWords.clear();
        try {
            Scanner input = new Scanner(wordsFile);
            while(input.hasNextLine()) bannedWords.add(input.nextLine().toLowerCase().trim());
            input.close();
        } catch (FileNotFoundException e) {
            log.error("Unable to open and read banned-words.txt", e);
        }
    }
}
