package br.edu.com.ifsul.taskorganizer.saripaar;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import br.edu.com.ifsul.taskorganizer.saripaar.annotation.Length;
import br.edu.com.ifsul.taskorganizer.saripaar.annotation.NotEmpty;
import br.edu.com.ifsul.taskorganizer.saripaar.annotation.Select;

public class Validator {

    public interface ValidationListener {
        void onValidationSucceeded();
        void onValidationFailed(List<ValidationError> errors);
    }

    private final Object controller;
    private ValidationListener validationListener;

    public Validator(Object controller) {
        this.controller = controller;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    public void validate() {
        if (validationListener == null) {
            return;
        }

        List<ValidationError> errors = new ArrayList<>();
        Field[] fields = controller.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object viewObject;
            try {
                viewObject = field.get(controller);
            } catch (IllegalAccessException e) {
                continue;
            }

            if (!(viewObject instanceof View)) {
                continue;
            }

            View view = (View) viewObject;

            if (field.isAnnotationPresent(NotEmpty.class)) {
                NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
                if (notEmpty != null && view instanceof EditText) {
                    EditText editText = (EditText) view;
                    String text = editText.getText() != null ? editText.getText().toString() : "";
                    if (notEmpty.trim()) {
                        text = text.trim();
                    }
                    if (text.isEmpty()) {
                        errors.add(new ValidationError(view, notEmpty.message()));
                    }
                }
            }

            if (field.isAnnotationPresent(Length.class)) {
                Length length = field.getAnnotation(Length.class);
                if (length != null && view instanceof EditText) {
                    EditText editText = (EditText) view;
                    String text = editText.getText() != null ? editText.getText().toString() : "";
                    if (length.trim()) {
                        text = text.trim();
                    }
                    if (!text.isEmpty()) {
                        if (text.length() < length.min() || text.length() > length.max()) {
                            errors.add(new ValidationError(view, length.message()));
                        }
                    }
                }
            }

            if (field.isAnnotationPresent(Select.class)) {
                Select select = field.getAnnotation(Select.class);
                if (select != null && view instanceof Spinner) {
                    Spinner spinner = (Spinner) view;
                    if (spinner.getSelectedItemPosition() == select.defaultSelection()) {
                        errors.add(new ValidationError(view, select.message()));
                    }
                }
            }
        }

        if (errors.isEmpty()) {
            validationListener.onValidationSucceeded();
        } else {
            validationListener.onValidationFailed(errors);
        }
    }
}
