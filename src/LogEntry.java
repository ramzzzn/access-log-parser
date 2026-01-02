import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {

    private String ipAddr;
    private LocalDateTime dateTime;
    private HttpMethod method;
    private String path;
    private int responseCode;
    private int responseSize;
    private String referer;
    private UserAgent userAgent;
    private final boolean valid;


    public LogEntry(String line) {
        Matcher matcher = parseString(line);

        if (!(matcher.matches())) {
            this.valid = false;
            return;
        }

        String ipAddr = matcher.group(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(matcher.group(2), formatter);
        HttpMethod method = HttpMethod.valueOf(matcher.group(3));
        String path = matcher.group(4);
        int responseCode = Integer.parseInt(matcher.group(5));
        int responseSize = Integer.parseInt(matcher.group(6));
        String referer = matcher.group(7);
        String userAgent = matcher.group(8);
        UserAgent parsedUserAgent = new UserAgent(userAgent);

        this.valid = true;
        this.ipAddr = ipAddr;
        this.dateTime = dateTime;
        this.method = method;
        this.path = path;
        this.responseCode = responseCode;
        this.responseSize = responseSize;
        this.referer = referer;
        this.userAgent = parsedUserAgent;
    }

    private Matcher parseString(String line) {
        String ipRegex = "^(\\S+)";
        String dateTimeRegex = "\\[(.*?)]";
        String methodRegex = "\"(\\w+)";
        String urlRegex = "([^\"]+)\"";
        String codeRegex = "(\\d{3})";
        String sizeRegex = "(\\d+)";
        String refererRegex = "\"([^\"]*)\"";
        String userAgentRegex = "\"([^\"]*)\"";

        String resultRegex = ipRegex + "\\s+\\S+\\s+\\S+\\s+" + dateTimeRegex + "\\s+" + methodRegex + "\\s+"
                + urlRegex + "\\s+" + codeRegex + "\\s+" + sizeRegex + "\\s+" + refererRegex + "\\s+" + userAgentRegex;
        Pattern pattern = Pattern.compile(resultRegex);
        return pattern.matcher(line);
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public boolean isValid() {
        return valid;
    }
}
