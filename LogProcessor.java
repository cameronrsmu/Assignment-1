import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

class LogEntry {
    String timestamp;
    String logLevel;
    String message;

    LogEntry(String timestamp, String logLevel, String message) {
        this.timestamp = timestamp;
        this.logLevel = logLevel;
        this.message = message;
    }
}

class Queue<T> {
    private LinkedList<T> list = new LinkedList<>();

    void enqueue(T item) {
        list.addLast(item);
    }

    T dequeue() {
        return list.pollFirst();
    }

    boolean isEmpty() {
        return list.isEmpty();
    }
}

class Stack<T> {
    private LinkedList<T> list = new LinkedList<>();

    void push(T item) {
        list.addFirst(item);
    }

    T pop() {
        return list.pollFirst();
    }

    boolean isEmpty() {
        return list.isEmpty();
    }

    LinkedList<T> getLastN(int n) {
        LinkedList<T> result = new LinkedList<>();
        int count = 0;
        for (T item : list) {
            if (count >= n) break;
            result.add(item);
            count++;
        }
        return result;
    }
}

public class LogProcessor {
    private static final String LOG_FILE = "log-data.csv";

    public static void main(String[] args) {
        Queue<LogEntry> logQueue = new Queue<>();
        Stack<LogEntry> errorStack = new Stack<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("] ");
                if (parts.length < 2) {
                    continue;
                }
                String timestamp = parts[0].substring(1);
                String[] levelAndMessage = parts[1].split(" ", 2);
                if (levelAndMessage.length < 2) {
                    continue;
                }
                String logLevel = levelAndMessage[0];
                String message = levelAndMessage[1];
                logQueue.enqueue(new LogEntry(timestamp, logLevel, message));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int infoCount = 0;
        int warnCount = 0;
        int errorCount = 0;
        int memoryWarnings = 0;

        while (!logQueue.isEmpty()) {
            LogEntry entry = logQueue.dequeue();
            switch (entry.logLevel) {
                case "INFO":
                    infoCount++;
                    break;
                case "WARN":
                    warnCount++;
                    if (entry.message.contains("Memory")) {
                        memoryWarnings++;
                    }
                    break;
                case "ERROR":
                    errorCount++;
                    errorStack.push(entry);
                    break;
            }
        }

        System.out.println("Log Level Counts:");
        System.out.println("INFO: " + infoCount);
        System.out.println("WARN: " + warnCount);
        System.out.println("ERROR: " + errorCount);
        System.out.println("\nMemory Warnings: " + memoryWarnings);

        System.out.println("\nLast 100 Errors:");
        LinkedList<LogEntry> recentErrors = errorStack.getLastN(100);
        for (LogEntry error : recentErrors) {
            System.out.println(error.timestamp + " " + error.message);
        }
    }
}
