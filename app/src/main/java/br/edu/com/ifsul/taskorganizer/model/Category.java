package br.edu.com.ifsul.taskorganizer.model;

public enum Category {
    STUDIES("Studies"),
    WORK("Work"),
    PERSONAL("Personal"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
