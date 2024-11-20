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
     * Singleton class to ensure only one instance of Logger is created.
     * @return The Logger instance.
     * Assistance: https://www.geeksforgeeks.org/singleton-class-java/#
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
     * Gets the log buffer as a string.
     * @return The log buffer as a string.
     */
    public String getLog() {
        return logBuffer.toString();
    }


    /**
     * Logs a message with a timestamp.
     * @param message The message to log in string format.
     */
    public void log(String message) {
        String formattedMessage = getCurrentTimestamp() + ": " + message;
        logBuffer.append(formattedMessage).append("\n\n");
    }


    /**
     * Gets the current timestamp.
     * @return The current timestamp in string format.
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
}
