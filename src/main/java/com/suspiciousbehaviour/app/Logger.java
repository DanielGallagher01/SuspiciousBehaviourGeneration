package com.suspiciousbehaviour.app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ParsedDomain;
import fr.uga.pddl4j.parser.ParsedProblem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.nio.file.Path;

public class Logger {
    private PrintWriter simpleLogWriter;
    private PrintWriter detailedLogWriter;
    private PrintWriter planLogWriter;
    private boolean initialized = false;


    public boolean initialize(File outputFolder, String simpleLogPath, String detailedLogPath, String planPath) {
        Path simpleLog    = outputFolder.toPath().resolve(simpleLogPath);
        Path detailedLog  = outputFolder.toPath().resolve(detailedLogPath);
        Path planLog      = outputFolder.toPath().resolve(planPath);
  
        return initialize(simpleLog.toString(), detailedLog.toString(), planLog.toString());
    }

    public boolean initialize(String simpleLogPath, String detailedLogPath, String planPath) {
        try {
            simpleLogWriter = new PrintWriter(new FileWriter(simpleLogPath, false));
            detailedLogWriter = new PrintWriter(new FileWriter(detailedLogPath, false));
            planLogWriter = new PrintWriter(new FileWriter(planPath, false));
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            simpleLogWriter.println("=== Log started at " + timestamp + " ===");
            detailedLogWriter.println("=== Detailed log started at " + timestamp + " ===");
            
            simpleLogWriter.flush();
            detailedLogWriter.flush();
            planLogWriter.flush();
            
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

    public void logAction(Action action, DefaultProblem problem) {
        if (!initialized) return;

        String message = "(";

        message += action.getName();

        for (int i = 0; i < action.arity(); i++) {
            final int index = action.getValueOfParameter(i);
            message += " " + problem.getConstantSymbols().get(index);
        }

        message += ")";
        planLogWriter.println(message);
        planLogWriter.flush();
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

