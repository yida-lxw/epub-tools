package com.yida.epub.config;

import com.yida.epub.utils.OSUtils;
import com.yida.epub.utils.StringUtils;

public class CalibreConfig {
    public static final String DEFAULT_CALIBRE_CONVERTER_NAME = "ebook-convert";

    /**
     * Calibre安装根目录
     */
    private String calibreBasePath;

    /**
     * Calibre转换器名称
     */
    private String converterName;

    public CalibreConfig(String calibreBasePath) {
        this(calibreBasePath, OSUtils.isWindows()?DEFAULT_CALIBRE_CONVERTER_NAME + ".exe" : DEFAULT_CALIBRE_CONVERTER_NAME);
    }

    public CalibreConfig(String calibreBasePath, String converterName) {
        calibreBasePath = StringUtils.replaceBackSlash(calibreBasePath);
        this.calibreBasePath = calibreBasePath;
        if(StringUtils.isEmpty(converterName)) {
            if(OSUtils.isWindows()) {
                converterName = DEFAULT_CALIBRE_CONVERTER_NAME + ".exe";
            } else {
                converterName = DEFAULT_CALIBRE_CONVERTER_NAME;
            }
        } else {
            if(OSUtils.isWindows()) {
                if(!converterName.endsWith(".exe")) {
                    converterName += ".exe";
                }
            } else {
                if(converterName.endsWith(".exe")) {
                    converterName = converterName.substring(0, converterName.lastIndexOf("."));
                }
            }
        }
        this.converterName = converterName;
    }

    public String getCalibreBasePath() {
        return calibreBasePath;
    }

    public void setCalibreBasePath(String calibreBasePath) {
        this.calibreBasePath = calibreBasePath;
    }

    public String getConverterName() {
        return converterName;
    }

    public void setConverterName(String converterName) {
        this.converterName = converterName;
    }
}
