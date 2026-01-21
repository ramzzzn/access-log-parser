import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private final HashSet<String> existPages = new HashSet<>();
    private final HashMap<String, Integer> osCount = new HashMap<>();

    public Statistics() {
    }

    public int getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public void addEntry(LogEntry logEntry) {
        if (logEntry.getResponseCode() == 200) {
            existPages.add(logEntry.getPath().split(" ")[0]);
        }
        OsType os = logEntry.getUserAgent().getOsType();
        osCount.put(os.toString(), osCount.getOrDefault(os.toString(), 0) + 1);
        totalTraffic += logEntry.getResponseSize();
        if (minTime == null || logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }
        if (maxTime == null || logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }
    }

    public HashSet<String> getExistPages() {
        return existPages;
    }

    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osStats = new HashMap<>();
        // Рассчитываем общее количество для всех операционных систем
        int total = osCount.values().stream().mapToInt(Integer::intValue).sum();

        // Рассчитываем долю для каждой операционной системы и записываем результат в новый HashMap
        for (Map.Entry<String, Integer> entry : osCount.entrySet()) {
            double osStat = Double.parseDouble(String.format(Locale.US, "%.3f", entry.getValue() * 1.0 / total));
            osStats.put(entry.getKey(), osStat);
        }
        return osStats;
    }

    public int getTrafficRate() {
        int durationInHours = (int) java.time.Duration.between(minTime, maxTime).getSeconds() / 3600;
        return totalTraffic / durationInHours;
    }
}
