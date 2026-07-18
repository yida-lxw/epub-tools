package com.yida.epub;

import com.yida.epub.core.CalibreManager;
import com.yida.epub.enums.EBookFormat;

public class CalibreManagerTest {
    public static void main(String[] args) {
        /**本地Calibre安装根目录*/
        String calibreBasePath = "E:/Calibre2/";
        /**待转换电子书文件绝对路径*/
        String sourceEBookFilePath = "D:/ChromeDownloads/老舍散文-可喜的寂寞.老舍.epub";
        /**目标电子书输出文件夹路径*/
        String destEBookOutputFolderPath = "D:/ChromeDownloads/ttttt";
        /**需要转换的目标格式*/
        EBookFormat targetEBookFormat = EBookFormat.MOBI;
        //tring bookName = "老舍散文-可喜的寂寞";
        //String authorName = "老舍";
        //String publisher = "浙江文艺出版社";

        //这3个参数可以不填
        String bookName = "";
        String authorName = "";
        String publisher = "";

        CalibreManager calibreManager = CalibreManager.getInstance()
                .calibreBasePath(calibreBasePath)
                .sourceEBookFilePath(sourceEBookFilePath)
                .destEBookOutputFolderPath(destEBookOutputFolderPath)
                .targetEBookFormat(targetEBookFormat)
                .bookName(bookName)
                .authorName(authorName)
                .publisher(publisher);
        calibreManager.convert();
    }
}
