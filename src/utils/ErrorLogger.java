package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import data.DataContainer;

public class ErrorLogger
{
	private static final String LOG_FILE = "error_log.txt";
    private static final PrintWriter writer;

    static {
        PrintWriter temp = null;
        try {
            temp = new PrintWriter(new FileWriter(DataContainer.appLocal + File.separator + LOG_FILE, true), true);
        } catch (IOException e) {
            System.err.println("Failed to open log file: " + e.getMessage());
        }
        writer = temp;
    }

    public static void log(Throwable t) {
        if (writer != null) {
            writer.println("[" + new java.util.Date() + "] " + t);
            t.printStackTrace(writer);
            writer.flush();
        } else {
            System.err.println("Logger not initialized.");
            t.printStackTrace();
        }
    }
}