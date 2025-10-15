package org.example.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManageLogs {

    public ManageLogs() {
        createFileLog();
    }

    /**
     * Function responsible for inserting the logs into the corresponding file
     * @param action You must specify the log type INFO or ERROR
     * @param message You must briefly describe what is being done or what the error is.
     */
    public void saveLog(String action, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = now.format(format);

        String log = "[" + formattedDate + "] [" + action + "] " + message + "\n";

        try (FileWriter writer = new FileWriter("fighterStickMan.log", true)) {
            writer.write(log);
        } catch (IOException e) {
            System.err.println("Error writing log: " + e.getMessage());
        }
    }

    /**
     * Function responsible for creating the file if it does not exist.
     */
    private void createFileLog() {
        try {
            File file = new File("fighterStickMan.log");
            if (file.createNewFile()) {
                System.out.println("File log created");
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }
}
