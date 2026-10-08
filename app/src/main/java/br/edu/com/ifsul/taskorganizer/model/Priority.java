package br.edu.com.ifsul.taskorganizer.model;

import br.edu.com.ifsul.taskorganizer.R;

public enum Priority {
    LOW("Low", R.string.priority_low),
    MEDIUM("Medium", R.string.priority_medium),
    HIGH("High", R.string.priority_high);

    private final String displayName;
    private final int displayNameResId;

    Priority(String displayName, int displayNameResId) {
        this.displayName = displayName;
        this.displayNameResId = displayNameResId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayNameResId() {
        return displayNameResId;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
