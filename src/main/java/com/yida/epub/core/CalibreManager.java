package com.yida.epub.core;

import com.yida.epub.config.CalibreConfig;
import com.yida.epub.enums.EBookFormat;
import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

public class CalibreManager {
    /**
     * Calibre安装根目录
     */
    private String calibreBasePath;

    /**
     * Calibre转换器名称
     */
    private String converterName;

    /**源电子书文件路径*/
    private String sourceEBookFilePath;

    /**源电子书文件所在父级目录绝对路径*/
    private String sourceEBookParentFolderPath;

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

    private static class CalibreManagerHolder {
        private static final CalibreManager INSTANCE = new CalibreManager();
    }

    private CalibreManager() {}

    // 获取单例实例
    public static CalibreManager getInstance() {
        return CalibreManagerHolder.INSTANCE;
    }

    /**
     * 批量转换电子书
     * @return
     */
    public boolean bulkConvert() {
        boolean convertResult = true;
        CalibreConfig calibreConfig = new CalibreConfig(calibreBasePath, converterName);
        Path ebookSourceParentPath = Paths.get(sourceEBookParentFolderPath);
        try {
            Files.walkFileTree(ebookSourceParentPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String currentFilePath = path.toAbsolutePath().toString();
                    currentFilePath = StringUtils.replaceBackSlash(currentFilePath);
                    String fileSuffix = FileUtils.getFileSuffix(currentFilePath);
                    fileSuffix = fileSuffix.toLowerCase();
                    if(!fileSuffix.equals("epub") && !fileSuffix.equals("mobi") && !fileSuffix.equals("azw3") &&
                            !fileSuffix.equals("azw") && !fileSuffix.equals("pdf") && !fileSuffix.equals("djvu") &&
                            !fileSuffix.equals("chm") && !fileSuffix.equals("txt")) {
                        return FileVisitResult.CONTINUE;
                    }
                    ConverterInputParameter converterInputParameter = new ConverterInputParameter(currentFilePath, destEBookOutputFolderPath, targetEBookFormat, bookName, authorName, publisher);
                    EBookConverter eBookConverter = new EBookConverter(calibreConfig, converterInputParameter);
                    eBookConverter.convert();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            convertResult = false;
        }
        return convertResult;
    }

    public boolean convert() {
        CalibreConfig calibreConfig = new CalibreConfig(calibreBasePath, converterName);
        ConverterInputParameter converterInputParameter = new ConverterInputParameter(sourceEBookFilePath, destEBookOutputFolderPath, targetEBookFormat, bookName, authorName, publisher);
        EBookConverter eBookConverter = new EBookConverter(calibreConfig, converterInputParameter);
        return eBookConverter.convert();
    }

    public CalibreManager calibreBasePath(String calibreBasePath) {
        this.calibreBasePath = calibreBasePath;
        return this;
    }

    public CalibreManager converterName(String converterName) {
        this.converterName = converterName;
        return this;
    }

    public CalibreManager sourceEBookFilePath(String sourceEBookFilePath) {
        this.sourceEBookFilePath = sourceEBookFilePath;
        return this;
    }

    public CalibreManager sourceEBookParentFolderPath(String sourceEBookParentFolderPath) {
        this.sourceEBookParentFolderPath = sourceEBookParentFolderPath;
        return this;
    }

    public CalibreManager destEBookOutputFolderPath(String destEBookOutputFolderPath) {
        this.destEBookOutputFolderPath = destEBookOutputFolderPath;
        return this;
    }

    public CalibreManager targetEBookFormat(EBookFormat targetEBookFormat) {
        this.targetEBookFormat = targetEBookFormat;
        return this;
    }

    public CalibreManager bookName(String bookName) {
        this.bookName = bookName;
        return this;
    }

    public CalibreManager authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public CalibreManager publisher(String publisher) {
        this.publisher = publisher;
        return this;
    }
}
