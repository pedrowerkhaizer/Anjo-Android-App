package utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class teclado {
    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
