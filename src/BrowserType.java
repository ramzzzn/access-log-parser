enum BrowserType {

    CHROME("Chrome"),
    FIREFOX("Firefox"),
    EDGE("Edge"),
    OPERA("Opera"),
    ANOTHER("Another browser");

    private final String name;

    BrowserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}