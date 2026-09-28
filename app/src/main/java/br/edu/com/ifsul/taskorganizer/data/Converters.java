package br.edu.com.ifsul.taskorganizer.data;

import androidx.room.TypeConverter;

import br.edu.com.ifsul.taskorganizer.model.Category;
import br.edu.com.ifsul.taskorganizer.model.Priority;

public class Converters {

    @TypeConverter
    public static String fromPriority(Priority priority) {
        return priority == null ? null : priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String priority) {
        return priority == null ? null : Priority.valueOf(priority);
    }

    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static Category toCategory(String category) {
        return category == null ? null : Category.valueOf(category);
    }
}
