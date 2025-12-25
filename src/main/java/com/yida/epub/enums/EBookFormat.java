package com.yida.epub.enums;

/**
 * 电子书格式枚举
 */
public enum EBookFormat {
    EPUB("epub"),
    MOBI("mobi"),
    AZW3("azw3"),
    AZW("azw"),
    PDF("pdf"),
    DJVU("djvu"),
    CHM("chm"),
    TXT("txt");

    private String formatName;

    EBookFormat(String formatName) {
        this.formatName = formatName;
    }

    public static EBookFormat of(String formatName) {
        for (EBookFormat eBookFormat : EBookFormat.values()) {
            if (eBookFormat.getFormatName().equals(formatName)) {
                return eBookFormat;
            }
        }
        return EPUB;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }
}
