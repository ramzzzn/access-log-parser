public class UserAgent {

    OsType osType;
    BrowserType browserType;
    boolean isBot;

    public UserAgent(String userAgent) {
        this.osType = parseOsType(userAgent);
        this.browserType = parseBrowser(userAgent);
        this.isBot = userAgent.toLowerCase().contains("bot");
    }

    private static OsType parseOsType(String userAgent) {
        if (userAgent.contains("Windows NT")) {
            return OsType.WINDOWS;
        }
        if (userAgent.contains("Mac OS X")) {
            return OsType.MACOS;
        }
        if ((userAgent.contains("Linux")) && !(userAgent.contains("Android"))) {
            return OsType.LINUX;
        }
        return OsType.ANOTHER;
    }

    private static BrowserType parseBrowser(String userAgent) {
        if ((userAgent.contains("Edg/")) || (userAgent.contains("Edge/"))) {
            return BrowserType.EDGE;
        }
        if (userAgent.contains("OPR/")) {
            return BrowserType.OPERA;
        }
        if (userAgent.contains("Firefox/")) {
            return BrowserType.FIREFOX;
        }
        if (userAgent.contains("Chrome/")) {
            return BrowserType.CHROME;
        }
        return BrowserType.ANOTHER;
    }

    public OsType getOsType() {
        return osType;
    }

    public BrowserType getBrowserType() {
        return browserType;
    }

    public boolean isBot() {
        return isBot;
    }

    @Override
    public String toString() {
        return "UserAgent{" + osType + ", " + browserType + "isBot=" + isBot + "}";
    }
}
