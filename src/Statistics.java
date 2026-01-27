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
    private final HashSet<String> notExistPages = new HashSet<>();
    private final HashSet<String> uniqueUsers = new HashSet<>();
    private final HashMap<String, Integer> osCount = new HashMap<>();
    private final HashMap<String, Integer> browserCount = new HashMap<>();
    private int userVisitCount;
    private int errorRequestCount;


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
        OsType os = logEntry.getUserAgent().getOsType();
        BrowserType browser = logEntry.getUserAgent().getBrowserType();
        boolean isBot = logEntry.getUserAgent().isBot();

        // Рассчитываем частоту встречаемости по каждой ОС и браузеру
        osCount.put(os.toString(), osCount.getOrDefault(os.toString(), 0) + 1);
        browserCount.put(browser.toString(), browserCount.getOrDefault(browser.toString(), 0) + 1);

        // Создаем списки существующих и несуществующих страниц
        if (logEntry.getResponseCode() == 200) {
            existPages.add(logEntry.getPath().split(" ")[0]);
        }
        if (logEntry.getResponseCode() == 404) {
            notExistPages.add(logEntry.getPath().split(" ")[0]);
        }

        totalTraffic += logEntry.getResponseSize();
        if (minTime == null || logEntry.getDateTime().isBefore(minTime)) {
            minTime = logEntry.getDateTime();
        }
        if (maxTime == null || logEntry.getDateTime().isAfter(maxTime)) {
            maxTime = logEntry.getDateTime();
        }

        // Подсчитываем количество посещений пользователей и список уникальных пользователей
        if (!isBot) {
            userVisitCount++;
            uniqueUsers.add(logEntry.getIpAddr());
        }

        // Подсчитываем количество ошибочных запросов
        if (logEntry.getResponseCode() >= 400 && logEntry.getResponseCode() <= 599) {
            errorRequestCount++;
        }
    }

    public HashSet<String> getExistPages() {
        return existPages;
    }

    public HashSet<String> getNotExistPages() {
        return notExistPages;
    }

    private HashMap<String, Double> getStatistics(HashMap<String, Integer> countMap) {
        HashMap<String, Double> stats = new HashMap<>();
        // Рассчитываем сумму значений всех ключей
        int total = countMap.values().stream().mapToInt(Integer::intValue).sum();

        // Рассчитываем долю по каждому ключу и записываем результат в новый HashMap
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            double stat = Double.parseDouble(String.format(Locale.US, "%.3f", entry.getValue() * 1.0 / total));
            stats.put(entry.getKey(), stat);
        }
        return stats;
    }

    public HashMap<String, Double> getOsStatistics() {
        return getStatistics(osCount);
    }

    public HashMap<String, Double> getBrowsersStatistics() {
        return getStatistics(browserCount);
    }


    public int getDurationInHours() {
        return (int) java.time.Duration.between(minTime, maxTime).getSeconds() / 3600;
    }

    public int getTrafficRate() {
        return totalTraffic / getDurationInHours();
    }

    public double getAvgUsersVisitsCount() {
        double avgVisits = (double) userVisitCount / getDurationInHours();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgVisits));
    }

    public double getAvgErrorRequestCount() {
        double avgErrors = (double) errorRequestCount / getDurationInHours();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgErrors));
    }

    public double getAvgVisitByUser() {
        double avgVisitByUser = (double) userVisitCount / uniqueUsers.size();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgVisitByUser));
    }
}
