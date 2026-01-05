enum OsType {

    WINDOWS("Windows"),
    MACOS("macOS"),
    LINUX("Linux"),
    ANOTHER("Another OS");

    private final String name;

    OsType(String name) {
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