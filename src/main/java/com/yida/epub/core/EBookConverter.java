package com.yida.epub.core;


import com.yida.epub.config.CalibreConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EBookConverter {
    private CalibreConfig calibreConfig;
    private ConverterInputParameter converterInputParameter;

    public EBookConverter(CalibreConfig calibreConfig, ConverterInputParameter converterInputParameter) {
        this.calibreConfig = calibreConfig;
        this.converterInputParameter = converterInputParameter;
    }

    public boolean convert() {
        CalibreConverterCommandBuilder calibreConverterCommandBuilder = CalibreConverterCommandBuilder.builder().calibreConfig(this.calibreConfig);
        String[] convertCommandArray = calibreConverterCommandBuilder.buildConvertCommandArray(this.converterInputParameter);
        ProcessBuilder processBuilder = new ProcessBuilder(convertCommandArray);
        processBuilder.redirectErrorStream(true);
        int exitCode = 0;
        try {
            Process process = processBuilder.start();
            printProcessOutput(process.getInputStream());
            exitCode = process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            exitCode = -1;
        }
        if (exitCode != 0) {
            System.out.println("转换失败！退出代码: " + exitCode);
            return false;
        }
        System.out.println("转换成功,电子书保存在：" + converterInputParameter.getDestEBookOutputFolderPath());
        return true;
    }

    private static void printProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Calibre] " + line);
            }
        }
    }

    public CalibreConfig getCalibreConfig() {
        return calibreConfig;
    }

    public void setCalibreConfig(CalibreConfig calibreConfig) {
        this.calibreConfig = calibreConfig;
    }

    public ConverterInputParameter getConverterInputParameter() {
        return converterInputParameter;
    }

    public void setConverterInputParameter(ConverterInputParameter converterInputParameter) {
        this.converterInputParameter = converterInputParameter;
    }
}
