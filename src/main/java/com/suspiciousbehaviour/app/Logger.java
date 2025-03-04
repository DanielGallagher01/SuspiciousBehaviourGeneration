package com.suspiciousbehaviour.app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private PrintWriter simpleLogWriter;
    private PrintWriter detailedLogWriter;
    private boolean initialized = false;

    public boolean initialize(String simpleLogPath, String detailedLogPath) {
        try {
            simpleLogWriter = new PrintWriter(new FileWriter(simpleLogPath, false));
            detailedLogWriter = new PrintWriter(new FileWriter(detailedLogPath, false));
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            simpleLogWriter.println("=== Log started at " + timestamp + " ===");
            detailedLogWriter.println("=== Detailed log started at " + timestamp + " ===");
            
            simpleLogWriter.flush();
            detailedLogWriter.flush();
            
            initialized = true;
            return true;
        } catch (IOException e) {
            System.err.println("Error initializing logger: " + e.getMessage());
            close();
            return false;
        }
    }
    
    public void logSimple(String message) {
        if (!initialized) return;
        
        simpleLogWriter.println(message);
        simpleLogWriter.flush();

 	detailedLogWriter.println(message);
        detailedLogWriter.flush();
    }
    
    public void logDetailed(String message) {
        if (!initialized) return;
        
        detailedLogWriter.println(message);
        detailedLogWriter.flush();
    }
    
    public void close() {
        if (simpleLogWriter != null) {
            simpleLogWriter.println("=== Log closed at " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ===");
            simpleLogWriter.close();
        }
        
        if (detailedLogWriter != null) {
            detailedLogWriter.println("=== Detailed log closed at " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ===");
            detailedLogWriter.close();
        }
        
        initialized = false;
    }
}

