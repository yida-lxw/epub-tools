package com.yida.epub.core;

import com.yida.epub.enums.EBookFormat;
import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.StringUtils;

/**
 * 电子书转换器输入参数
 */
public class ConverterInputParameter {
    /**源电子书文件路径*/
    private String sourceEBookFilePath;
    /**目标电子书输出文件夹路径*/
    private String destEBookOutputFolderPath;
    /**目标电子书格式*/
    private EBookFormat targetEBookFormat;

    /**书名*/
    private String bookName;
    /**作者姓名*/
    private String authorName;
    /**出版社*/
    private String publisher;

    public ConverterInputParameter(String sourceEBookFilePath, String destEBookOutputFolderPath, EBookFormat targetEBookFormat, String bookName, String authorName, String publisher) {
        sourceEBookFilePath = StringUtils.replaceBackSlash(sourceEBookFilePath);
        String sourceBookName = FileUtils.getFileNameWithoutSuffix(sourceEBookFilePath);
        this.sourceEBookFilePath = sourceEBookFilePath;
        this.destEBookOutputFolderPath = destEBookOutputFolderPath;
        this.targetEBookFormat = targetEBookFormat;

        if(StringUtils.isEmpty(bookName)) {
            int lastIndex = sourceBookName.lastIndexOf(".");
            String bookNameFromFilePath = sourceBookName.substring(0, lastIndex);
            bookName = bookNameFromFilePath;
        }
        this.bookName = bookName;

        if(StringUtils.isEmpty(authorName)) {
            String author = retrieveAuthorName(sourceBookName);
            authorName = author;
        }

        this.authorName = authorName;
        this.publisher = publisher;
    }

    public static String retrieveAuthorName(String bookName) {
        // 查找第一个方括号的位置（用于处理外国作者）
        int bracketIndex = bookName.indexOf('[');
        if (bracketIndex != -1) {
            // 找到方括号：从第一个方括号开始截取到字符串末尾
            return bookName.substring(bracketIndex);
        }

        // 没有方括号时：查找最后一个点号位置
        int lastDotIndex = bookName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < bookName.length() - 1) {
            // 找到有效点号：截取点号后的内容
            return bookName.substring(lastDotIndex + 1);
        }
        // 没有找到点号或点号在末尾：返回空字符串
        return "";
    }

    public String getSourceEBookFilePath() {
        return sourceEBookFilePath;
    }

    public void setSourceEBookFilePath(String sourceEBookFilePath) {
        this.sourceEBookFilePath = sourceEBookFilePath;
    }

    public String getDestEBookOutputFolderPath() {
        return destEBookOutputFolderPath;
    }

    public void setDestEBookOutputFolderPath(String destEBookOutputFolderPath) {
        this.destEBookOutputFolderPath = destEBookOutputFolderPath;
    }

    public EBookFormat getTargetEBookFormat() {
        return targetEBookFormat;
    }

    public void setTargetEBookFormat(EBookFormat targetEBookFormat) {
        this.targetEBookFormat = targetEBookFormat;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
