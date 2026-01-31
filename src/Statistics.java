import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int errorRequestCount;
    private final HashSet<String> existPages = new HashSet<>();
    private final HashSet<String> notExistPages = new HashSet<>();
    private final HashSet<String> realUserIpAddrList = new HashSet<>();
    private int realUserVisitCount;
    private final HashSet<String> refererDomainList = new HashSet<>();
    private final HashMap<String, Integer> osCount = new HashMap<>();
    private final HashMap<String, Integer> browserCount = new HashMap<>();
    private final HashMap<Integer, Integer> realUserVisitCountPerSecond = new HashMap<>();
    private final HashMap<String, Integer> realUserVisitCountPerUser = new HashMap<>();

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

        updateRealUserMetrics(logEntry);
        updateErrorRequestCount(logEntry.getResponseCode());
        addRefererDomainToList(logEntry.getReferer());
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
        return (int) java.time.Duration.between(minTime, maxTime).toHours();
    }

    public int getTrafficRate() {
        return totalTraffic / getDurationInHours();
    }

    private void updateRealUserMetrics(LogEntry logEntry) {
        if (logEntry.getUserAgent().isBot()) {
            realUserVisitCount++;
            realUserIpAddrList.add(logEntry.getIpAddr());
            updateRealUserVisitCountPerSecond(logEntry.getDateTime());
            updateRealUserVisitCountPerUser(logEntry.getIpAddr());
        }
    }

    private void updateRealUserVisitCountPerSecond(LocalDateTime logTime) {
        int second = (int) logTime.toEpochSecond(java.time.ZoneOffset.of("+03:00"));
        realUserVisitCountPerSecond.put(second, realUserVisitCountPerSecond.getOrDefault(second, 0) + 1);
    }

    private void updateRealUserVisitCountPerUser(String userIpAddr) {
        realUserVisitCountPerUser.put(userIpAddr, realUserVisitCountPerUser.getOrDefault(userIpAddr, 0) + 1);
    }

    private void updateErrorRequestCount(int responseCode) {
        if (responseCode >= 400 && responseCode <= 599) {
            errorRequestCount++;
        }
    }

    public double getAvgUsersVisitsCount() {
        double avgVisits = (double) realUserVisitCount / getDurationInHours();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgVisits));
    }

    public double getAvgErrorRequestCount() {
        double avgErrors = (double) errorRequestCount / getDurationInHours();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgErrors));
    }

    public double getAvgVisitByUser() {
        double avgVisitByUser = (double) realUserVisitCount / realUserIpAddrList.size();
        return Double.parseDouble(String.format(Locale.US, "%.3f", avgVisitByUser));
    }

    private Map.Entry<Integer, Integer> getTimeOfPeakVisitAndCountOfPeakVisitPerSecond() {
        return realUserVisitCountPerSecond.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
    }

    public LocalDateTime getTimeOfPeakVisitPerSecond() {
        return LocalDateTime.ofEpochSecond(getTimeOfPeakVisitAndCountOfPeakVisitPerSecond().getKey(), 0, ZoneOffset.of("+03:00"));
    }

    public int getCountOfPeakVisitPerSecond() {
        return getTimeOfPeakVisitAndCountOfPeakVisitPerSecond().getValue();
    }

    private String extractDomainName(String url) {
        try {
            URL uri = new URL(url);
            return uri.getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private void addRefererDomainToList(String referer) {
        if (!referer.equals("-")) {
            String domainName = extractDomainName(referer);
            if (domainName != null) {
                refererDomainList.add(domainName);
            }
        }
    }

    public HashSet<String> getRefererDomains() {
        return refererDomainList;
    }

    private Map.Entry<String, Integer> getIpOfUserAndCountOfVisitPerUser() {
        return realUserVisitCountPerUser.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
    }

    public String getIpOfVisitingUser() {
        return getIpOfUserAndCountOfVisitPerUser().getKey();
    }

    public Integer getCountVisitPerUser() {
        return getIpOfUserAndCountOfVisitPerUser().getValue();
    }
}
