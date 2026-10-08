package br.edu.com.ifsul.taskorganizer.model;

import br.edu.com.ifsul.taskorganizer.R;

public enum Category {
    STUDIES("Studies", R.string.category_studies),
    WORK("Work", R.string.category_work),
    PERSONAL("Personal", R.string.category_personal),
    OTHER("Other", R.string.category_other);

    private final String displayName;
    private final int displayNameResId;

    Category(String displayName, int displayNameResId) {
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
