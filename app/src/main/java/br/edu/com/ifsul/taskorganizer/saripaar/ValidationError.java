package br.edu.com.ifsul.taskorganizer.saripaar;

import android.view.View;

public class ValidationError {

    private final View view;
    private final String errorMessage;

    public ValidationError(View view, String errorMessage) {
        this.view = view;
        this.errorMessage = errorMessage;
    }

    public View getView() {
        return view;
    }

    public String getCollatedErrorMessage() {
        return errorMessage;
    }
}
