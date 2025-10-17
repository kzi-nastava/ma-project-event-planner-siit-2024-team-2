package com.example.eventplanner.utils;

import android.text.Html;
import android.text.Spanned;

public class FormatUtil {
    private FormatUtil() {}

    public static String markdownToHtml(String markdown) {
        markdown = markdown.replace("\n", "<br>");
        markdown = markdown.replaceAll("\\*\\*\\*(.*?)\\*\\*\\*", "<b><i>$1</i></b>");
        markdown = markdown.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        markdown = markdown.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        return markdown;
    }

    public static Spanned markdownToSpanned(String markdown) {
        return Html.fromHtml(markdownToHtml(markdown), Html.FROM_HTML_MODE_COMPACT);
    }
}
