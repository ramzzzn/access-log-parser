import java.time.LocalDateTime;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if (minTime == null || logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }
        if (maxTime == null || logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }
    }

    public int getTrafficRate() {
        int durationInHours = (int) java.time.Duration.between(minTime, maxTime).getSeconds() / 3600;
        return totalTraffic / durationInHours;
    }
}
