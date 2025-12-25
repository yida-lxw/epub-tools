package com.yida.epub;

import com.yida.epub.core.CalibreManager;
import com.yida.epub.enums.EBookFormat;

/**
 * 电子书批量转换测试
 * @author Lanxiaowei
 */
public class CalibreManagerBulkConvertTest {
    public static void main(String[] args) {
        /**本地Calibre安装根目录*/
        String calibreBasePath = "E:/Calibre2/";
        /**待转换电子书所在目录路径*/
        String sourceEBookFolderPath = "D:/ChromeDownloads/aaaaa";
        /**目标电子书输出文件夹路径*/
        String destEBookOutputFolderPath = "D:/ChromeDownloads/ttttt";
        /**需要转换的目标格式*/
        EBookFormat targetEBookFormat = EBookFormat.AZW3;

        //这3个参数可以不填
        String bookName = "";
        String authorName = "";
        String publisher = "";

        CalibreManager calibreManager = CalibreManager.getInstance()
                .calibreBasePath(calibreBasePath)
                .sourceEBookParentFolderPath(sourceEBookFolderPath)
                .destEBookOutputFolderPath(destEBookOutputFolderPath)
                .targetEBookFormat(targetEBookFormat)
                .bookName(bookName)
                .authorName(authorName)
                .publisher(publisher);
        calibreManager.bulkConvert();
    }
}
