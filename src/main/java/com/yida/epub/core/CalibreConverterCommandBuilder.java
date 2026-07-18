package com.yida.epub.core;

import com.yida.epub.config.CalibreConfig;
import com.yida.epub.enums.EBookFormat;
import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CalibreConverterCommandBuilder {
    public static final String CONVERT_COMMAND_TEMPLATE = "#calibre_basepath##converter_name# \"#input_file_path#\" \"#output_file_path#\" " +
            "--title \"#book_name#\" --authors \"#author_name#\" --author-sort \"#author_name#\" --publisher #publisher#";

    private CalibreConfig calibreConfig;

    public static CalibreConverterCommandBuilder builder() {
        return new CalibreConverterCommandBuilder();
    }

    /**
     * 构建转换命令
     *
     * @param converterInputParameter
     * @return
     */
    public String buildConvertCommand(ConverterInputParameter converterInputParameter) {
        String convertCommandTemplate = CONVERT_COMMAND_TEMPLATE;
        String sourceEBookFilePath = converterInputParameter.getSourceEBookFilePath();
        String destEBookOutputFolderPath = converterInputParameter.getDestEBookOutputFolderPath();
        EBookFormat targetEBookFormat = converterInputParameter.getTargetEBookFormat();
        String bookName = converterInputParameter.getBookName();
        String authorName = converterInputParameter.getAuthorName();
        String destEBookOutputFilePath = buildOutputBookFilePath(sourceEBookFilePath, destEBookOutputFolderPath, targetEBookFormat, bookName, authorName);
        String convertCommand = convertCommandTemplate.replace("#calibre_basepath#", calibreConfig.getCalibreBasePath())
                .replace("#converter_name#", calibreConfig.getConverterName())
                .replace("#input_file_path#", sourceEBookFilePath)
                .replace("#output_file_path#", destEBookOutputFilePath)
                .replace("#book_name#", converterInputParameter.getBookName())
                .replace("#author_name#", converterInputParameter.getAuthorName())
                .replace("#publisher#", converterInputParameter.getPublisher());
        System.out.println("Convert Command: " + convertCommand);
        return convertCommand;
    }

    public String[] buildConvertCommandArray(ConverterInputParameter converterInputParameter) {
        List<String> command = new ArrayList<>();
        String sourceEBookFilePath = converterInputParameter.getSourceEBookFilePath();
        String destEBookOutputFolderPath = converterInputParameter.getDestEBookOutputFolderPath();
        EBookFormat targetEBookFormat = converterInputParameter.getTargetEBookFormat();
        String bookName = converterInputParameter.getBookName();
        String authorName = converterInputParameter.getAuthorName();
        String publisher = converterInputParameter.getPublisher();
        String destEBookOutputFilePath = buildOutputBookFilePath(sourceEBookFilePath, destEBookOutputFolderPath, targetEBookFormat, bookName, authorName);
        String callFilePath = calibreConfig.getCalibreBasePath() + calibreConfig.getConverterName();
        // 添加Calibre可执行路径
        command.add("\"" + callFilePath + "\"");

        // 添加输入文件路径
        command.add("\"" + sourceEBookFilePath + "\"");

        // 添加输出文件路径
        command.add("\"" + destEBookOutputFilePath + "\"");

        // 添加元数据参数
        if (StringUtils.isNotEmpty(bookName)) {
            command.add("--title");
            command.add("\"" + bookName + "\"");
        }
        if (StringUtils.isNotEmpty(authorName)) {
            command.add("--authors");
            command.add(authorName);
            command.add("--author-sort");
            command.add(authorName);
        }
        if (StringUtils.isNotEmpty(publisher)) {
            command.add("--publisher");
            command.add(publisher);
        }
        return command.toArray(new String[0]);
    }

    /**
     * 生成输出电子书文件路径
     *
     * @param sourceEBookFilePath
     * @param destEBookOutputFolderPath
     * @param targetEBookFormat
     * @return
     */
    public String buildOutputBookFilePath(String sourceEBookFilePath, String destEBookOutputFolderPath, EBookFormat targetEBookFormat) {
        return buildOutputBookFilePath(sourceEBookFilePath, destEBookOutputFolderPath, targetEBookFormat, null, null);
    }

    /**
     * 生成输出电子书文件路径
     *
     * @param sourceEBookFilePath
     * @param destEBookOutputFolderPath
     * @param targetEBookFormat
     * @param bookName
     * @param authorName
     * @return
     */
    public String buildOutputBookFilePath(String sourceEBookFilePath, String destEBookOutputFolderPath, EBookFormat targetEBookFormat,
                                          String bookName, String authorName) {
        String destEBookFileName = null;
        if (StringUtils.isEmpty(bookName) || StringUtils.isEmpty(authorName)) {
            sourceEBookFilePath = StringUtils.replaceBackSlash(sourceEBookFilePath);
            String sourceEBookFileNameWithoutSuffix = FileUtils.getFileNameWithoutSuffix(sourceEBookFilePath);
            String destSuffix = targetEBookFormat.getFormatName();
            destEBookFileName = sourceEBookFileNameWithoutSuffix + "." + destSuffix;
        } else {
            String destSuffix = targetEBookFormat.getFormatName();
            destEBookFileName = bookName + "." + authorName + "." + destSuffix;
        }
        destEBookOutputFolderPath = StringUtils.replaceBackSlash(destEBookOutputFolderPath, true);
        String destEBookFilePath = destEBookOutputFolderPath + destEBookFileName;
        return destEBookFilePath;
    }

    public CalibreConverterCommandBuilder calibreConfig(CalibreConfig calibreConfig) {
        this.calibreConfig = calibreConfig;
        return this;
    }
}
