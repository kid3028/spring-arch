package com.qull.springarch.beans.propertyeditors;

import com.qull.springarch.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 11:17
 */
public class CustomBooleanEditor extends PropertyEditorSupport {
    public static final String VALUE_TRUE = "true";

    public static final String VALUE_FALSE = "false";

    public static final String VALUE_ON = "on";

    public static final String VALUE_OFF = "off";

    public static final String VALUE_YES = "yes";

    public static final String VALUE_NO = "no";

    public static final String VALUE_1 = "1";

    public static final String VALUE_0 = "0";

    private final boolean allowEmpty;

    public CustomBooleanEditor(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String input = (text != null ? text.trim() : null);
        if (this.allowEmpty && !StringUtils.hasLength(input)) {
            // Treat empty String as null value
            setValue(null);
        }
        if ((VALUE_TRUE.equalsIgnoreCase(input) || VALUE_ON.equals(input) || VALUE_YES.equals(input) || VALUE_1.equals(input))) {
            setValue(Boolean.TRUE);
        }else if(VALUE_FALSE.equals(input) | VALUE_OFF.equals(input) || VALUE_NO.equals(input) || VALUE_0.equals(input)) {
            setValue(Boolean.FALSE);
        }else {
            throw new IllegalArgumentException("Invalid boolean value [" + input + "]");
        }
    }

    @Override
    public String getAsText() {
        if (Boolean.TRUE.equals(getValue())) {
            return VALUE_TRUE;
        }else if(Boolean.FALSE.equals(getValue())) {
            return VALUE_FALSE;
        }else {
            return "";

        }
    }
}
