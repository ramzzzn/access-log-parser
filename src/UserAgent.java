public class UserAgent {

    OsType osType;
    BrowserType browserType;

    public UserAgent(String userAgent) {
        this.osType = parseOsType(userAgent);
        this.browserType = parseBrowser(userAgent);
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

    @Override
    public String toString() {
        return "UserAgent{" + osType + ", " + browserType + '}';
    }
}
