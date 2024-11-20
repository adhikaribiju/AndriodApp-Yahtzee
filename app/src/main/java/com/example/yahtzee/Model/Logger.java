package com.example.yahtzee.Model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private static Logger instance;
    private StringBuilder logBuffer;
    private Logger() {
        logBuffer = new StringBuilder();
    }


    /**
     * Gets the singleton instance of the Logger class.
     * @return The singleton instance of the Logger class.
     * Help from: https://www.geeksforgeeks.org/singleton-class-java/#
     */
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }

    /**
     * Logs a message with a timestamp.
     * @param message The message to be logged.
     */
    public void log(String message) {
        String formattedMessage = getCurrentTimestamp() + ": " + message;
        logBuffer.append(formattedMessage).append("\n\n");
    }

    public String getLog() {
        return logBuffer.toString();
    }

    /**
     * Gets the current timestamp formatted as "yyyy-MM-dd HH:mm:ss".
     * @return The formatted current timestamp.
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
}
