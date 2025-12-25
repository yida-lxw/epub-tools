package com.yida.epub;

import com.yida.epub.bean.RenameCoverImageResult;
import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.StringUtils;
import com.yida.epub.utils.ZipUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用于将epub格式电子书中的繁体字转换为简体字
 *
 * @author yida
 */
public class Application {
    public static final String inCompatibleMeta = "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"/>";

    /**
     * 已实现功能：
     * 1. 繁体转简体
     * 2. 替换封面图片文件
     * 3. 修复封面图片不显示问题
     * 4. 清理opf文件内容中可能藏着的广告内容
     * 5. 清除广告页或页面内容中插入的广告文本
     * @param args
     */
    public static void main(String[] args) {
        String suffix = ".epub";
        //待处理的epub格式电子书存放的根目录
        String basePath = "D:/ChromeDownloads/ttttt";
        //页面语言
        String language = "zh";
        //是否需要繁体转简体操作
        boolean requiredTranslate = false;
        //是否需要替换封面图片文件
        boolean requiredReplaceCoverImage = false;
        //每本电子书的封面图片文件(封面图片文件名与电子书文件名保持一致)
        String newCoverImageFolderPath = null;
        //标识是否为广告页的关键词
        String[] advertisementWordArray = {
                "古德猫宁李", "关注“幸福的味道”微信公众号", "免费阅读更多经典自然文学",
                "Digital Lab是上海译文出版社数字业务的实验部门"
        };
        //需要直接删除的广告文本
        String[] advertiseContentArray = {
                "<div class=\"calibre3\">本书由“<a class=\"calibre1\" href=\"http://epubw.com\">ePUBw.COM</a>”整理，<a class=\"calibre1\" href=\"http://epubw.com\">ePUBw.COM</a> 提供最新最全的优质电子书下载！！！</div>",
                "<div class=\"calibre3\">\n" +
                        "        本书由“ \n" +
                        "        <a class=\"calibre1\" href=\"http://epubw.com\">\n" +
                        "          ePUBw.COM\n" +
                        "        </a>”整理， \n" +
                        "        <a class=\"calibre1\" href=\"http://epubw.com\">\n" +
                        "          ePUBw.COM\n" +
                        "        </a> 提供最新最全的优质电子书下载！！！\n" +
                        "      </div>",
                "<p height=\"1em\" width=\"0pt\" class=\"calibre2\">本书由“行行”整理，如果你不知道读什么书或者想获得更多免费电子书请加小编微信或QQ：2338856113 小编也和结交一些喜欢读书的朋友 或者关注小编个人微信公众号名称：幸福的味道 id：d716-716 为了方便书友朋友找书和看书，小编自己做了一个电子书下载网站，网站的名称为：周读 网址：http://www.ireadweek.com</p>"
        };
        //包含如下文本的节点需要删除
        String[] requiredRemoveNodeTextArray = {
                "品牌方：九久读书人"
        };
        bulkTranslateBooks(basePath, suffix, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void translateOneBook(String basePath, String bookFilePath) {
        translateOneBook(basePath, bookFilePath, (String)null);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language) {
        translateOneBook(basePath, bookFilePath, language, false);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language, boolean requiredTranslate) {
        translateOneBook(basePath, bookFilePath, language, requiredTranslate, (String[])null, (String[])null, (String[])null);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language,
                                         String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        translateOneBook(basePath, bookFilePath, language, false, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language, boolean requiredTranslate,
                                         String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        translateOneBook(basePath, bookFilePath, language, requiredTranslate, false, (String)null, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language,
                                         boolean requiredTranslate, boolean requiredReplaceCoverImage, String newCoverImageFolderPath) {
        translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, (String[])null, (String[])null, (String[])null);
    }

    private static void translateOneBook(String basePath, String bookFilePath, String language,
                                         boolean requiredTranslate, boolean requiredReplaceCoverImage, String newCoverImageFolderPath,
                                         String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        bookFilePath = StringUtils.replaceBackSlash(bookFilePath);
        String bookName = FileUtils.getFileNameWithoutSuffix(bookFilePath);
        try {
            translateBook(basePath, bookName, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath,
                    advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void bulkTranslateBooks(String basePath, String suffix) {
        bulkTranslateBooks(basePath, suffix, (String)null);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language) {
        bulkTranslateBooks(basePath, suffix, language, false);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language, boolean requiredTranslate) {
        bulkTranslateBooks(basePath, suffix, language, requiredTranslate, (String[])null, (String[])null, (String[])null);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language,
                                           String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        bulkTranslateBooks(basePath, suffix, language, false, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language, boolean requiredTranslate,
                                           String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        bulkTranslateBooks(basePath, suffix, language, requiredTranslate, false, (String)null, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language,
                                           boolean requiredTranslate, boolean requiredReplaceCoverImage, String newCoverImageFolderPath) {
        bulkTranslateBooks(basePath, suffix, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, (String[])null, (String[])null, (String[])null);
    }

    private static void bulkTranslateBooks(String basePath, String suffix, String language,
                                           boolean requiredTranslate, boolean requiredReplaceCoverImage,
                                           String newCoverImageFolderPath, String[] advertisementWordArray,
                                           String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) {
        basePath = StringUtils.replaceBackSlash(basePath);
        if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        List<String> epubFileList = FileUtils.listFileWithSpecifedSuffix(basePath, suffix);
        if (null == epubFileList || epubFileList.size() <= 0) {
            System.out.println("There is no any book in [" + basePath + "].");
            return;
        }
        if(StringUtils.isEmpty(language)) {
            language = "zh";
        }
        if(StringUtils.isNotEmpty(newCoverImageFolderPath)) {
            newCoverImageFolderPath = StringUtils.replaceBackSlash(newCoverImageFolderPath, true);
        }
        for (String bookFilePath : epubFileList) {
            bookFilePath = StringUtils.replaceBackSlash(bookFilePath);
            if(!requiredReplaceCoverImage) {
                translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
                continue;
            }
            if(StringUtils.isEmpty(newCoverImageFolderPath)) {
                translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
            } else {
                File coverImageFolder = new File(newCoverImageFolderPath);
                if(!coverImageFolder.exists()) {
                    translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
                } else {
                    List<Path> coverImageFileList = null;
                    try {
                        coverImageFileList = Files.walk(coverImageFolder.toPath())
                                .filter(path -> {
                                    boolean isFile = Files.isRegularFile(path);
                                    if(!isFile) {
                                        return false;
                                    }
                                    String filePath = path.toAbsolutePath().toString();
                                    filePath = StringUtils.replaceBackSlash(filePath);
                                    String imageFileName = FileUtils.getFileName(filePath);
                                    if(imageFileName.endsWith(".jpeg") || imageFileName.endsWith(".JPEG") ||
                                            imageFileName.endsWith(".png") || imageFileName.endsWith(".PNG") ||
                                            imageFileName.endsWith(".jpg") || imageFileName.endsWith(".JPG")) {
                                        return true;
                                    }
                                    return false;
                                })
                                .collect(Collectors.toList());
                    } catch (IOException e) {
                        System.out.println("Error when walk through the cover image folder:[" + coverImageFolder + "].");
                    }
                    if(null == coverImageFileList || coverImageFileList.size() <= 0) {
                        translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFolderPath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
                    } else {
                        String bookFileName = FileUtils.getFileNameWithoutSuffix(bookFilePath);
                        String newCoverImageFilePath = newCoverImageFolderPath + bookFileName + ".jpg";
                        translateOneBook(basePath, bookFilePath, language, requiredTranslate, requiredReplaceCoverImage, newCoverImageFilePath, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
                    }
                }
            }

        }
    }

    private static void translateBook(String basePath, String orignalFileName, String language,
                                      String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) throws IOException {
        translateBook(basePath, orignalFileName, language, false, false, null, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void translateBook(String basePath, String orignalFileName, String language, boolean requiredTranslate,
                                      String[] advertisementWordArray, String[] advertiseContentArray, String[] requiredRemoveNodeTextArray) throws IOException {
        translateBook(basePath, orignalFileName, language, requiredTranslate, false, null, advertisementWordArray, advertiseContentArray, requiredRemoveNodeTextArray);
    }

    private static void translateBook(String basePath, String orignalFileName, String language,
                                      boolean requiredTranslate, boolean requiredReplaceCoverImage, String newCoverImageFolderPath,
                                      String[] advertisementWordArray, String[] advertiseContentArray,
                                      String[] requiredRemoveNodeTextArray) throws IOException {
        String epub = "epub";
        String zip = "zip";
        String outputDirName = "output";
        String path = basePath + orignalFileName + "." + epub;
        String unzipPath = basePath + orignalFileName + "/";
        unzipPath = unzipPath.replace("\\", "/");
        int index = path.lastIndexOf("/");
        String pathPreffix = path.substring(0, index + 1);
        String orignalEpubFileName = FileUtils.getFileName(path);
        String copyPath = pathPreffix + "old_" + orignalEpubFileName;
        FileUtils.copyFile(path, copyPath);
        FileUtils.renameTo(path, zip);

        index = path.lastIndexOf(".");
        pathPreffix = path.substring(0, index + 1);
        String zipPath = pathPreffix + zip;
        boolean unziped = ZipUtils.unZip(zipPath, unzipPath);

        if (unziped) {
            System.out.println("解压成功？" + unziped);
            Map<String, String> pathMap = autoDeterminTheHtmlFilePath(basePath, orignalFileName);
            //需要转成简体字的文本文件的所在文件夹
            String textFilePath = pathMap.get("html");
            //目录文件所在文件夹
            String tocFilePath = pathMap.get("toc");
            String textOutPutFilePath = textFilePath + "/" + outputDirName + "/";
            textOutPutFilePath = textOutPutFilePath.replace("\\", "/");
            if(textOutPutFilePath.startsWith("/")) {
                textOutPutFilePath = textOutPutFilePath.substring(1);
            }
            boolean translated = true;
            if(requiredTranslate) {
                translated = translate2Simple(unzipPath, textFilePath, textOutPutFilePath, true, tocFilePath, false, "utf8");
            }
            System.out.println("翻译成功？" + translated);
            if (translated) {
                String zipFileName = FileUtils.getFileName(path);
                String zipFilePath = basePath + zip + "/";
                zipFilePath = zipFilePath.replace("\\", "/");
                File zipFileDir = new File(zipFilePath);
                if (!zipFileDir.exists()) {
                    zipFileDir.mkdirs();
                }

                String zipFileFullPath = zipFilePath + zipFileName;
                zipFileFullPath = zipFileFullPath.replace(epub, zip);

                if(requiredTranslate && translated) {
                    String txtOutPutPath = unzipPath + textOutPutFilePath;
                    boolean copyFilesResult = FileUtils.copyFiles2Dir(txtOutPutPath, unzipPath + textFilePath);
                    System.out.println("复制xhtml文件成功？" + copyFilesResult);
                    if (copyFilesResult) {
                        boolean deleted = FileUtils.deleteDir(txtOutPutPath);
                        System.out.println("删除output文件夹成功？" + deleted);
                    }
                }

                List<String> excludeFiles = new ArrayList<>();
                String unzipFullPath = unzipPath + zip + "/";
                excludeFiles.add(unzipFullPath);
                List<String> allFileItems = FileUtils.listAllFile(unzipPath, excludeFiles, true);

                if(requiredReplaceCoverImage) {
                    //替换封面图片文件
                    newCoverImageFolderPath = StringUtils.replaceBackSlash(newCoverImageFolderPath, true);
                    String newCoverImageFilePath = newCoverImageFolderPath + orignalFileName + ".jpg";
                    replaceCoverImageFile(basePath, orignalFileName, newCoverImageFilePath);
                }

                String author = getAuthorName(orignalFileName);
                String title = orignalFileName.replace("." + author, "");
                //开始压缩之前先修正封面图片不显示问题
                RenameCoverImageResult renameCoverImageResult = resolveCoverImageDisplayIssue(basePath, orignalFileName, title, author, language, requiredRemoveNodeTextArray);

                //最后清除页面上的广告
                Path htmlFileBasePath = Paths.get(basePath, orignalFileName, textFilePath);
                String htmlFileParentPath = htmlFileBasePath.toAbsolutePath().toString();
                htmlFileParentPath = StringUtils.replaceBackSlash(htmlFileParentPath);
                String opfFilePath = findOpfFile(basePath, orignalFileName);
                cleanAdvertisPage(htmlFileParentPath, opfFilePath, advertisementWordArray, advertiseContentArray);

                //压缩之前尝试检查下opf文件内容的正确性并进行修正
                checkOutOpfFile(opfFilePath);

                boolean renameCoverImageSuccess = renameCoverImageResult.isRenameSuccess();
                String sourceCoverImageFilePath = renameCoverImageResult.getSourceCoverImageFilePath();
                String targetCoverImageFilePath = renameCoverImageResult.getTargetCoverImageFilePath();
                boolean ziped = zipEBook(allFileItems, zipFileFullPath, sourceCoverImageFilePath, targetCoverImageFilePath, renameCoverImageSuccess, unzipPath);
                System.out.println("压缩成功？" + ziped);
                if (ziped) {
                    String epubSuffix = epub;
                    boolean renamed = FileUtils.renameTo(zipFileFullPath, epubSuffix);
                    System.out.println("重命名为epub成功？" + renamed);
                    if (renamed) {
                        String fileName = FileUtils.getFileNameWithoutSuffix(zipFileFullPath);
                        String zipDir = basePath + zip;
                        String epubFilePath = zipDir + "/" + fileName + "." + epubSuffix;
                        String destEpubFilePath = basePath + "new_" + fileName + "." + epubSuffix;
                        boolean copyed = FileUtils.copyFile(epubFilePath, destEpubFilePath);
                        System.out.println("epub文件复制成功？" + copyed);
                        if (copyed) {
                            FileUtils.renameTo(zipPath, epub);
                            FileUtils.deleteDir(unzipPath);
                            FileUtils.deleteDir(zipDir);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param unzipPath
     * @param textFilePath
     * @param textOutPutFilePath
     * @return boolean
     * @description 将文本文件的内容转换为简体中文
     * @author yida
     * @date 2022-07-04 10:27:37
     */
    public static boolean translate2Simple(String unzipPath, String textFilePath, String textOutPutFilePath) {
        return translate2Simple(unzipPath, textFilePath, textOutPutFilePath, false, null);
    }

    /**
     * @param unzipPath
     * @param textFilePath
     * @param textOutPutFilePath
     * @return boolean
     * @description 将文本文件的内容转换为简体中文
     * @author yida
     * @date 2022-07-04 10:27:37
     */
    public static boolean translate2Simple(String unzipPath, String textFilePath, String textOutPutFilePath,
                                           boolean includeTocFile, String tocFilePath) {
        return translate2Simple(unzipPath, textFilePath, textOutPutFilePath, includeTocFile, tocFilePath, false);
    }

    /**
     * @param unzipPath
     * @param textFilePath
     * @param textOutPutFilePath
     * @return boolean
     * @description 将文本文件的内容转换为简体中文
     * @author yida
     * @date 2022-07-04 10:27:37
     */
    public static boolean translate2Simple(String unzipPath, String textFilePath, String textOutPutFilePath,
                                           boolean includeTocFile, String tocFilePath, boolean append) {
        return translate2Simple(unzipPath, textFilePath, textOutPutFilePath, includeTocFile, tocFilePath, append, "UTF-8");
    }

    /**
     * @param unzipPath
     * @param textFilePath
     * @param textOutPutFilePath
     * @return boolean
     * @description 将文本文件的内容转换为简体中文
     * @author yida
     * @date 2022-07-04 10:27:37
     */
    public static boolean translate2Simple(String unzipPath, String textFilePath, String textOutPutFilePath,
                                           boolean includeTocFile, String tocFilePath, boolean append, String charsetName) {
        String textFileFullPath = unzipPath + textFilePath;
        String outputFilePath = unzipPath + textOutPutFilePath;
        List<String> excludeFiles = new ArrayList<>();
        excludeFiles.add("css");
        excludeFiles.add("js");
        excludeFiles.add("jpeg");
        excludeFiles.add("jpg");
        excludeFiles.add("png");
        excludeFiles.add("gif");
        excludeFiles.add("css");
        excludeFiles.add("image");
        excludeFiles.add("images");
        excludeFiles.add("META-INF");

        String tempUnzipPath = null;
        if(unzipPath.endsWith("/")) {
            tempUnzipPath = unzipPath.substring(0, unzipPath.length() - 1);
        } else {
            tempUnzipPath = unzipPath;
        }
        String folderName = tempUnzipPath.substring(tempUnzipPath.lastIndexOf("/") + 1);
        String authorName = getAuthorName(folderName);

        List<String> allFiles = FileUtils.listAllFile(textFileFullPath, excludeFiles, true);
        try {
            File outputDir = new File(outputFilePath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            for (String eachFilePath : allFiles) {
                if (new File(eachFilePath).isDirectory()) {
                    continue;
                }
                eachFilePath = eachFilePath.replace("\\", "/");
                String content = FileUtils.readFileAsString(eachFilePath);
                content = FileUtils.toSimple(content);
                String fileName = FileUtils.getFileName(eachFilePath);
                String outputFileName = outputFilePath + fileName;

                content = content.replace(authorName + "著", authorName + "箤");
                content = content.replace("著作", "珠咗");
                content = content.replace("著者", "茱喆");
                content = content.replace("著名", "珠瞑");
                content = content.replace("著述", "煮淑");
                content = content.replace("原著", "员株");
                content = content.replace("显著", "显株");
                content = content.replace("编著", "编株");
                content = content.replace("译著", "译株");
                content = content.replace("论著", "论株");
                content = content.replace("土著", "土株");
                content = content.replace("专著", "颛驻");
                content = content.replace("撰著", "篆竺");
                content = content.replace("名著", "鸣柱");
                content = content.replace("巨著", "橘诸");
                content = content.replace("拙著", "啄嘱");
                content = content.replace("臭名昭著", "臭名昭珠");
                content = content.replace("恶名昭著", "恶名昭珠");
                content = content.replace("知微见著", "知微见株");


                content = content.replace("著", "着");
                content = content.replace("珍．奥斯汀", "简.奥斯汀");

                content = content.replace(authorName + "箤", authorName + "著");
                content = content.replace("珠咗", "著作");
                content = content.replace("茱喆", "作者");
                content = content.replace("珠瞑", "著名");
                content = content.replace("煮淑", "著述");
                content = content.replace("员株", "原著");
                content = content.replace("显株", "显著");
                content = content.replace("编株", "编著");
                content = content.replace("译株", "译著");
                content = content.replace("论株", "论著");
                content = content.replace("土株", "土著");
                content = content.replace("颛驻", "专著");
                content = content.replace("篆竺", "撰著");

                content = content.replace("鸣柱", "名著");
                content = content.replace("橘诸", "巨著");
                content = content.replace("啄嘱", "拙著");

                content = content.replace("臭名昭珠", "臭名昭著");
                content = content.replace("恶名昭珠", "恶名昭著");
                content = content.replace("知微见株", "知微见著");

                content = content.replace("乾隆", "骞隆");
                content = content.replace("乾坤", "骞坤");
                content = content.replace("乾", "干");
                content = content.replace("骞隆", "乾隆");
                content = content.replace("骞坤", "乾坤");

                content = content.replace("锺", "钟");
                content = content.replace("於", "于");
                content = content.replace("麽", "么");
                content = content.replace("拚", "拼");


                content = content.replace("目次", "目录");
                content = content.replace("菁英", "精英");
                content = content.replace("弁言", "前言");

                content = content.replace("义大利", "意大利");


                FileUtils.write2File(outputFileName, content, charsetName, append);
            }
            if (includeTocFile && null != tocFilePath && !"".equals(tocFilePath)) {
                String tocFileFullPath = unzipPath + tocFilePath;
                if (new File(tocFileFullPath).exists()) {
                    String tocContent = FileUtils.readFileAsString(tocFileFullPath);
                    tocContent = FileUtils.toSimple(tocContent);
                    FileUtils.write2File(tocFileFullPath, tocContent, charsetName, append);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解决epub封面图片不显示问题
     * @param basePath
     * @param orignalFileName
     * @throws IOException
     */
    private static RenameCoverImageResult resolveCoverImageDisplayIssue(String basePath, String orignalFileName, String title,
                                                                        String author, String language, String[] requiredRemoveNodeTextArray) {
        //查找封面图片文件
        String coverImageFilePath = findCoverImageFile(basePath, orignalFileName);
        //获取原始封面图文件名称
        String coverImageFileName = FileUtils.getFileName(coverImageFilePath);

        //查找根目录下的封面文件
        String coverPageFilePath = findCoverPageFile(basePath, orignalFileName);
        if(StringUtils.isNotEmpty(coverPageFilePath)) {
            //更新根目录下的封面文件的封面图片文件后缀名
            correctImagePathInTitlePage(coverPageFilePath, coverImageFileName);
        }

        //查找图书内容页目录下的封面文件
        Map<String, String> pathMap = autoDeterminTheHtmlFilePath(basePath, orignalFileName);
        String internalHtmlPageBasePath = pathMap.get("html");
        String internalCoverImageFilePath = findFirstBookPageFile(basePath, orignalFileName, internalHtmlPageBasePath);
        if(StringUtils.isNotEmpty(internalCoverImageFilePath)) {
            //更新封面图片文件后缀名
            correctImagePathInTitlePage(internalCoverImageFilePath, coverImageFileName);
        }

        //查找opf文件
        String opfFilePath = findOpfFile(basePath, orignalFileName);
        if(StringUtils.isNotEmpty(internalCoverImageFilePath)) {
            //更新opf文件
            updateOpfFile(opfFilePath, title, author, language);
        }

        //查找css样式文件
        List<String> cssFilePathList = findCSSFiles(basePath, orignalFileName);
        if(null != cssFilePathList && cssFilePathList.size() > 0) {
            for (String cssFilePath : cssFilePathList) {
                //更新css文件
                updateCSS(cssFilePath);
            }
        }

        //更新html页面的title
        String internalHtmlPageBaseDir = Paths.get(basePath, orignalFileName, internalHtmlPageBasePath).toAbsolutePath().toString();
        internalHtmlPageBaseDir = StringUtils.replaceBackSlash(internalHtmlPageBaseDir);
        updateHtmlTitle(internalHtmlPageBaseDir, orignalFileName, requiredRemoveNodeTextArray);

        //更新toc.ncx文件
        String tocFileRelativePath = pathMap.get("toc");
        if(StringUtils.isNotEmpty(tocFileRelativePath)) {
            Path tocFilePath = Paths.get(basePath, orignalFileName, tocFileRelativePath);
            String tocFileAbsolutePath = tocFilePath.toAbsolutePath().toString();
            tocFileAbsolutePath = StringUtils.replaceBackSlash(tocFileAbsolutePath);
            updateTocFile(tocFileAbsolutePath);
        }

        //封面图片文件重命名
        RenameCoverImageResult renameCoverImageResult = renameCoverImage(coverImageFilePath);
        return renameCoverImageResult;
    }

    /**
     * 替换封面图片文件
     * @param basePath
     * @param orignalFileName
     */
    private static void replaceCoverImageFile(String basePath, String orignalFileName, String newCoverImageFilePath) {
        //查找封面图片文件
        String coverImageFilePath = findCoverImageFile(basePath, orignalFileName);
        if(StringUtils.isEmpty(coverImageFilePath)) {
            System.out.println("未找到封面图片文件,故无法处理后续的替换封面图片文件操作.");
            return;
        }
        //获取原始封面图文件名称
        File newConverImageFile = new File(newCoverImageFilePath);
        if(!newConverImageFile.exists()) {
            System.out.println("新封面图片文件[" + newCoverImageFilePath + "]不存在,故无法处理后续的替换封面图片文件操作.");
            return;
        }
        FileUtils.copyFile(newCoverImageFilePath, coverImageFilePath);
    }

    private static String findFirstBookPageFile(String basePath, String orignalFileName, String internalTitlePageBaseDir) {
        try {
            String bookPageBasePath = Paths.get(basePath, orignalFileName, internalTitlePageBaseDir).toAbsolutePath().toString();
            bookPageBasePath = StringUtils.replaceBackSlash(bookPageBasePath);
            Optional<Path> titalPageFilePathOptional = Files.list(Paths.get(bookPageBasePath))
                    .filter(p -> {
                        String currentPath = StringUtils.replaceBackSlash(p.toAbsolutePath().toString());
                        if (currentPath.endsWith("cover.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover1.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover1.html")) {
                            return true;
                        }

                        if (currentPath.endsWith("Cover.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover1.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover1.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("titlepage.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("titlepage.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("Titlepage.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Titlepage.html")) {
                            return true;
                        }
                        return false;
                    }).findFirst();
            String titalPageFilePath = null;
            if (titalPageFilePathOptional.isPresent()) {
                titalPageFilePath = titalPageFilePathOptional.get().toAbsolutePath().toString();
            } else {
                titalPageFilePathOptional = Files.list(Paths.get(bookPageBasePath))
                        .filter(p -> {
                            String currentPath = StringUtils.replaceBackSlash(p.toAbsolutePath().toString());
                            String htmlFileName = FileUtils.getFileName(currentPath);
                            if (htmlFileName.endsWith(".xhtml") || htmlFileName.endsWith(".html")) {
                                return true;
                            }
                            return false;
                        }).sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .findFirst();
                if (!titalPageFilePathOptional.isPresent()) {
                    return null;
                }
                titalPageFilePath = titalPageFilePathOptional.get().toAbsolutePath().toString();
            }
            if (StringUtils.isEmpty(titalPageFilePath)) {
                return null;
            }
            titalPageFilePath = StringUtils.replaceBackSlash(titalPageFilePath);
            File titlePageFile = new File(titalPageFilePath);
            long fileSize = titlePageFile.length();
            if (fileSize <= 0) {
                return null;
            }
            return titalPageFilePath;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param basePath
     * @param orignalFileName
     * @return String
     * @description 查找opf文件
     * @author yida
     * @date 2025-11-08 11:19:46
     */
    private static String findOpfFile(String basePath, String orignalFileName) {
        Path startDir = Paths.get(basePath, orignalFileName).normalize();
        try (Stream<Path> pathStream = Files.walk(startDir)) {
            Optional<Path> opfPathOptional = pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String fileName = p.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".opf");
                    }).findFirst();
            if (opfPathOptional.isPresent()) {
                String opfFilePath = opfPathOptional.get().toAbsolutePath().toString();
                opfFilePath = StringUtils.replaceBackSlash(opfFilePath);
                return opfFilePath;
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error searching for opf file: " + e.getMessage());
        }
        return null;
    }

    /**
     * @param basePath
     * @param originalFileName
     * @return String
     * @description 查找封面图片文件
     * @author yida
     * @date 2025-11-08 11:19:46
     */
    private static String findCoverImageFile(String basePath, String originalFileName) {
        Path startDir = Paths.get(basePath, originalFileName).normalize();
        List<Path> coverImages = new ArrayList<>();

        try (Stream<Path> pathStream = Files.walk(startDir)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String fileName = p.getFileName().toString().toLowerCase();
                        return fileName.equals("cover.jpeg") ||
                                fileName.equals("cover.jpg") ||
                                fileName.equals("cover1.jpeg") ||
                                fileName.equals("cover1.jpg");
                    })
                    .forEach(coverImages::add);
            if (!coverImages.isEmpty()) {
                // 按路径长度排序并选择最短的
                coverImages.sort(Comparator.comparingInt(p -> p.toString().length()));
                return StringUtils.replaceBackSlash(coverImages.get(0).toString());
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error searching for cover image: " + e.getMessage());
        }
        return null;
    }

    /**
     * 查找css样式文件
     * @param basePath
     * @param orignalFileName
     * @return
     */
    private static List<String> findCSSFiles(String basePath, String orignalFileName) {
        try {
            List<Path> filePathList  = Files.list(Paths.get(basePath + orignalFileName))
                    .filter(p -> p.toAbsolutePath().toString().endsWith(".css")).collect(Collectors.toList());
            if(null == filePathList || filePathList.size() <= 0) {
                return null;
            }
            List<String> cssFileList = new ArrayList<>();
            for (Path path : filePathList) {
                String cssPath = path.toAbsolutePath().toString();
                cssPath = StringUtils.replaceBackSlash(cssPath);
                cssFileList.add(cssPath);
            }
            return cssFileList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param basePath
     * @param orignalFileName
     * @return {@link List}
     * @description 查找封面页
     * @author yida
     * @date 2025-11-08 11:15:04
     */
    private static String findCoverPageFile(String basePath, String orignalFileName) {
        try {
            Optional<Path> coverPageFileOptional = Files.list(Paths.get(basePath + orignalFileName))
                    .filter(p -> {
                        String currentPath = StringUtils.replaceBackSlash(p.toAbsolutePath().toString());
                        if (currentPath.endsWith("cover.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover1.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("cover1.html")) {
                            return true;
                        }

                        if (currentPath.endsWith("Cover.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover1.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Cover1.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("titlepage.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("titlepage.html")) {
                            return true;
                        }
                        if (currentPath.endsWith("Titlepage.xhtml")) {
                            return true;
                        }
                        if (currentPath.endsWith("Titlepage.html")) {
                            return true;
                        }
                        return false;
                    }).findFirst();
            if(!coverPageFileOptional.isPresent()) {
                return null;
            }
            String coverPageFilePath = coverPageFileOptional.get().toAbsolutePath().toString();
            coverPageFilePath = StringUtils.replaceBackSlash(coverPageFilePath);
            return coverPageFilePath;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新CSS文件内容
     * @param cssFilePath
     */
    private static void updateCSS(String cssFilePath) {
        cssFilePath = StringUtils.replaceBackSlash(cssFilePath);
        String cssFileContent = FileUtils.readFileAsString(cssFilePath);
        if(cssFileContent.contains("a {\\n\" +\n" +
                "                \"    text-decoration:none;  \\n\" +\n" +
                "                \"}")) {
            return;
        }
        cssFileContent = "a {\n" +
                "    text-decoration:none;  \n" +
                "}\n\nli {\n" +
                "  list-style-type:none;\n" +
                "}\n\n" +  cssFileContent;
        FileUtils.write2File(cssFilePath, cssFileContent);
    }

    /**
     * 封面图片重命名
     * @param coverImageFilePath
     */
    private static RenameCoverImageResult renameCoverImage(String coverImageFilePath) {
        coverImageFilePath = StringUtils.replaceBackSlash(coverImageFilePath);
        String coverImageFileName = FileUtils.getFileName(coverImageFilePath);
        if(!coverImageFileName.endsWith(".jpeg")) {
            return new RenameCoverImageResult(coverImageFilePath, coverImageFileName, null, null, false);
        }
        String newCoverImageFileName = coverImageFileName.replace(".jpeg", ".jpg");
        Path sourcePath = Paths.get(coverImageFilePath);
        Path sourceParentPath = sourcePath.getParent();
        Path targetPath = sourceParentPath.resolve(newCoverImageFileName);
        String targetCoverImageFilePath = StringUtils.replaceBackSlash(targetPath.toAbsolutePath().toString());
        boolean renameResult = FileUtils.renameFile(sourcePath, targetPath);
        return new RenameCoverImageResult(coverImageFilePath, coverImageFileName, targetCoverImageFilePath, newCoverImageFileName, renameResult);
    }

    private static String updateOpfFile(String opfFilePath, String title, String author, String language) {
        if(StringUtils.isEmpty(language)) {
            language = "zh";
        }
        String opfFileContent = FileUtils.readFileAsString(opfFilePath);
        Document doc = Jsoup.parse(opfFileContent, "", Parser.xmlParser());
        doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                .syntax(Document.OutputSettings.Syntax.xml);
        Element metadata = doc.selectFirst("metadata");
        String coverName = null;
        if (metadata != null) {
            Elements toKeep = new Elements();
            Element titleElement = metadata.selectFirst("dc|title");
            titleElement.text(title);
            Element authorElement = metadata.selectFirst("dc|creator");
            authorElement.text(author);
            authorElement.attr("opf:file-as", author);
            Element languageElement = metadata.selectFirst("dc|language");
            languageElement.text(language);
            Element coverElement = metadata.selectFirst("meta[name=cover]");
            if(null == coverElement) {
                coverElement = metadata.selectFirst("meta[name=Cover]");
                coverName = coverElement.attr("content");
            } else {
                coverName = coverElement.attr("content");
            }
            // 选择要保留的元素
            toKeep.add(titleElement);
            toKeep.add(authorElement);
            toKeep.add(languageElement);
            toKeep.add(coverElement);

            // 删除所有子元素
            metadata.empty();

            // 添加需要保留的元素
            for (Element element : toKeep) {
                metadata.appendChild(element);
            }
        }
        Element coverItem = doc.selectFirst("manifest > item[id="+coverName+"]");
        String coverImageAttrValue = coverItem.attr("href");
        coverImageAttrValue = coverImageAttrValue.replace(".jpeg", ".jpg");
        coverItem.attr("href", coverImageAttrValue);
        String opfFileNewContent = doc.outerHtml();
        FileUtils.write2File(opfFilePath, opfFileNewContent);
        return coverName;
    }

    /**
     * 检查opf文件内容的正确性
     * @param opfFilePath
     * @return
     */
    private static void checkOutOpfFile(String opfFilePath) {
        String opfFileContent = FileUtils.readFileAsString(opfFilePath);
        Document doc = Jsoup.parse(opfFileContent, "", Parser.xmlParser());
        doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                .syntax(Document.OutputSettings.Syntax.xml);
        Elements manifestItems = doc.select("manifest > item");
        if(null == manifestItems || manifestItems.size() <= 0) {
            return;
        }
        Path opfParentPath = Paths.get(opfFilePath).getParent();
        String opfParentFilePath = opfParentPath.toAbsolutePath().toString();
        opfParentFilePath = StringUtils.replaceBackSlash(opfParentFilePath, true);
        Map<String, String> manifestItemMap = new HashMap<>();
        Map<String, String> manifestItemIdMap = new HashMap<>();
        List<String> deletedItemIdList = new ArrayList<>();
        for (Element manifestItem : manifestItems) {
            String href = manifestItem.attr("href");
            String id = manifestItem.attr("id");
            String currentFilePath = opfParentFilePath + href;
            File currentFile = new File(currentFilePath);
            if(!currentFile.exists()) {
                manifestItem.remove();
                deletedItemIdList.add(id);
            } else {
                if(href.endsWith(".xhtml") || href.endsWith(".html")) {
                    manifestItemMap.put(href, id);
                }
                manifestItemIdMap.put(id, href);
            }
        }
        Element spine = doc.selectFirst("spine");
        Elements itemrefs = doc.select("spine > itemref");
        if(null == itemrefs || itemrefs.size() <= 0) {
            for(Map.Entry<String, String> entry : manifestItemMap.entrySet()) {
                String id = entry.getValue();
                Element itemref = new Element("itemref");
                itemref.attr("idref", id);
                spine.appendChild(itemref);
            }
        } else {
            //删除manifest里不存在的itemref
            for(Element itemref : itemrefs) {
                String idref = itemref.attr("idref");
                String href = manifestItemIdMap.get(idref);
                if(deletedItemIdList.contains(idref)) {
                    itemref.remove();
                } else if(StringUtils.isNotEmpty(href) && !href.endsWith(".xhtml") && !href.endsWith(".html")) {
                    itemref.remove();
                }
            }

            //添加manifest里存在但spine里不存在的的itemref
            for(Map.Entry<String, String> entry : manifestItemMap.entrySet()) {
                String id = entry.getValue();
                Element itemrefElement = spine.selectFirst("itemref[idref=" + id + "]");
                if(null == itemrefElement) {
                    Element itemref = new Element("itemref");
                    itemref.attr("idref", id);
                    spine.appendChild(itemref);
                }
            }
        }

        String opfFileNewContent = doc.outerHtml();
        FileUtils.write2File(opfFilePath, opfFileNewContent);
    }

    private static void updateTocFile(String tocFileAbsolutePath) {
        File tocFile = new File(tocFileAbsolutePath);
        if(!tocFile.exists()) {
            System.out.println("toc.ncx文件[" + tocFileAbsolutePath + "]不存在");
            return;
        }
        String tocFileContent = FileUtils.readFileAsString(tocFileAbsolutePath);
        tocFileContent = replaceTableContents(tocFileContent);
        FileUtils.write2File(tocFileAbsolutePath, tocFileContent);
    }

    /**
     * 纠正封面页里的封面图文件名后缀
     * @param titlePageFilePath
     * @param coverFileName
     */
    private static void correctImagePathInTitlePage(String titlePageFilePath, String coverFileName) {
        String titlePageFileContent = FileUtils.readFileAsString(titlePageFilePath);
        Document doc = Jsoup.parse(titlePageFileContent, "", Parser.xmlParser());
        doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                .syntax(Document.OutputSettings.Syntax.xml);
        String selector = String.format("image[xlink:href*=%s]", coverFileName);
        Element imageElement = doc.selectFirst(selector);
        String attrName = null;
        if (imageElement == null) {
            selector = String.format("image[href*=%s]", coverFileName);
            imageElement = doc.selectFirst(selector);
            if(imageElement == null) {
                selector = String.format("image[src*=%s]", coverFileName);
                imageElement = doc.selectFirst(selector);
                if(imageElement == null) {
                    selector = String.format("img[src*=%s]", coverFileName);
                    imageElement = doc.selectFirst(selector);
                    if(imageElement == null) {
                        return;
                    }
                    attrName = "src";
                } else {
                    attrName = "src";
                }
            } else {
                attrName = "href";
            }
        } else {
            attrName = "xlink:href";
        }
        String hrefValue = imageElement.attr(attrName);
        hrefValue = hrefValue.replace(".jpeg", ".jpg");
        imageElement.attr(attrName, hrefValue);
        String newTitlePageFileContent = doc.outerHtml();
        FileUtils.write2File(titlePageFilePath, newTitlePageFileContent);
    }

    /**
     * 更新每个HTML页面文件的title
     * @param htmlFileBasePath
     * @param title
     */
    private static void updateHtmlTitle(String htmlFileBasePath, String title, String[] requiredRemoveNodeTextArray) {
        try {
            Files.walkFileTree(Paths.get(htmlFileBasePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String currentFilePath = path.toAbsolutePath().toString();
                    currentFilePath = StringUtils.replaceBackSlash(currentFilePath);
                    if(!currentFilePath.endsWith(".xhtml") && !currentFilePath.endsWith(".html")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String htmlContent = FileUtils.readFileAsString(currentFilePath);
                    Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
                    doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                            .syntax(Document.OutputSettings.Syntax.xml);
                    Element titleElement = doc.selectFirst("head > title");
                    if(null == titleElement) {
                        return FileVisitResult.CONTINUE;
                    }
                    String orignalTitle = titleElement.text();
                    if(null == orignalTitle || orignalTitle.length() <= 10) {
                        bulkRemoveElementContainsText(doc, requiredRemoveNodeTextArray);
                        rewriteHTMLFileContent(doc, currentFilePath);
                        return FileVisitResult.CONTINUE;
                    }
                    if(orignalTitle.equals(title)) {
                        bulkRemoveElementContainsText(doc, requiredRemoveNodeTextArray);
                        rewriteHTMLFileContent(doc, currentFilePath);
                        return FileVisitResult.CONTINUE;
                    }
                    titleElement.text(title);
                    bulkRemoveElementContainsText(doc, requiredRemoveNodeTextArray);
                    rewriteHTMLFileContent(doc, currentFilePath);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将更新后的HTML内容再写回原文件
     * @param doc
     * @param currentFilePath
     */
    private static void rewriteHTMLFileContent(Document doc, String currentFilePath) {
        String newHTMLPageFileContent = doc.outerHtml();
        newHTMLPageFileContent = replaceTableContents(newHTMLPageFileContent);
        //删除不兼容的meta标签
        newHTMLPageFileContent = removeIncompatibleMetaContent(newHTMLPageFileContent);
        FileUtils.write2File(currentFilePath, newHTMLPageFileContent);
    }

    private static Map<String, String> autoDeterminTheHtmlFilePath(String basePath, String orignalFileName) {
        Map<String, String> pathMap = new HashMap<>();
        String baseDir = basePath + orignalFileName + "/";
        if (new File(baseDir + "EPUB").exists()) {
            if (new File(baseDir + "EPUB/xhtml/toc.ncx").exists()) {
                pathMap.put("html", "EPUB/xhtml");
                pathMap.put("toc", "EPUB/xhtml/toc.ncx");
            } else if (new File(baseDir + "EPUB/Text/toc.ncx").exists()) {
                pathMap.put("html", "EPUB/Text");
                pathMap.put("toc", "EPUB/Text/toc.ncx");
            } else if (new File(baseDir + "EPUB/text/toc.ncx").exists()) {
                pathMap.put("html", "EPUB/text");
                pathMap.put("toc", "EPUB/text/toc.ncx");
            } else if (new File(baseDir + "EPUB/toc.ncx").exists()) {
                pathMap.put("html", "EPUB");
                pathMap.put("toc", "EPUB/toc.ncx");
            } else if (new File(baseDir + "EPUB/xhtml/cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/xhtml");
                if (new File(baseDir + "EPUB/xhtml/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/xhtml/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/xhtml/cover.xhtml").exists()) {
                pathMap.put("html", "epub/xhtml");
                if (new File(baseDir + "epub/xhtml/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/xhtml/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "EPUB/text/cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/text");
                if (new File(baseDir + "EPUB/text/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/text/cover.xhtml").exists()) {
                pathMap.put("html", "epub/text");
                if (new File(baseDir + "epub/text/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "EPUB/Text/cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/Text");
                if (new File(baseDir + "EPUB/Text/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/Text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/Text/cover.xhtml").exists()) {
                pathMap.put("html", "epub/Text");
                if (new File(baseDir + "epub/Text/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/Text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "EPUB/xhtml/Cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/xhtml");
                if (new File(baseDir + "EPUB/xhtml/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/xhtml/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/xhtml/Cover.xhtml").exists()) {
                pathMap.put("html", "epub/xhtml");
                if (new File(baseDir + "epub/xhtml/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/xhtml/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "EPUB/text/Cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/text");
                if (new File(baseDir + "EPUB/text/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/text/Cover.xhtml").exists()) {
                pathMap.put("html", "epub/text");
                if (new File(baseDir + "epub/text/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "EPUB/Text/Cover.xhtml").exists()) {
                pathMap.put("html", "EPUB/Text");
                if (new File(baseDir + "EPUB/Text/toc.ncx").exists()) {
                    pathMap.put("toc", "EPUB/Text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else if (new File(baseDir + "epub/Text/Cover.xhtml").exists()) {
                pathMap.put("html", "epub/Text");
                if (new File(baseDir + "epub/Text/toc.ncx").exists()) {
                    pathMap.put("toc", "epub/Text/toc.ncx");
                } else if (new File(baseDir + "toc.ncx").exists()) {
                    pathMap.put("toc", "toc.ncx");
                }
            } else {
                pathMap.put("html", "EPUB");
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "OEBPS/Text").exists()) {
            pathMap.put("html", "OEBPS/Text");
            if (new File(baseDir + "OEBPS/Text/toc.ncx").exists()) {
                pathMap.put("toc", "OEBPS/Text/toc.ncx");
            } else if (new File(baseDir + "OEBPS/toc.ncx").exists()) {
                pathMap.put("toc", "OEBPS/toc.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "OPS/Text").exists()) {
            pathMap.put("html", "OPS/Text");
            if (new File(baseDir + "OPS/Text/toc.ncx").exists()) {
                pathMap.put("toc", "OPS/Text/toc.ncx");
            } else if (new File(baseDir + "OPS/toc.ncx").exists()) {
                pathMap.put("toc", "OPS/toc.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "OEBPS").exists()) {
            pathMap.put("html", "OEBPS");
            if (new File(baseDir + "OEBPS/toc.ncx").exists()) {
                pathMap.put("toc", "OEBPS/toc.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "OPS").exists()) {
            pathMap.put("html", "OPS");
            if (new File(baseDir + "OPS/toc.ncx").exists()) {
                pathMap.put("toc", "OPS/toc.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "item/xhtml").exists()) {
            pathMap.put("html", "item/xhtml");
            if (new File(baseDir + "item/xhtml/toc.ncx").exists()) {
                pathMap.put("toc", "item/xhtml/toc.ncx");
            } else if (new File(baseDir + "item/toc.ncx").exists()) {
                pathMap.put("toc", "item/toc.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else if (new File(baseDir + "text").exists()) {
            pathMap.put("html", "text");
            if (new File(baseDir + "text/toc.ncx").exists()) {
                pathMap.put("toc", "text/toc.ncx");
            } else if (new File(baseDir + "text/fb.ncx").exists()) {
                pathMap.put("toc", "text/fb.ncx");
            } else if (new File(baseDir + "fb.ncx").exists()) {
                pathMap.put("toc", "fb.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        }  else if (new File(baseDir + "Text").exists()) {
            pathMap.put("html", "Text");
            if (new File(baseDir + "Text/toc.ncx").exists()) {
                pathMap.put("toc", "Text/toc.ncx");
            } else if (new File(baseDir + "Text/fb.ncx").exists()) {
                pathMap.put("toc", "Text/fb.ncx");
            } else if (new File(baseDir + "fb.ncx").exists()) {
                pathMap.put("toc", "fb.ncx");
            } else {
                pathMap.put("toc", "toc.ncx");
            }
            return pathMap;
        } else {
            pathMap.put("html", "");
            pathMap.put("toc", "toc.ncx");
        }
        return pathMap;
    }

    /**
     * 清理广告内容
     * @param htmlFileBasePath
     */
    private static void cleanAdvertisPage(String htmlFileBasePath, String opfFilePath, String[] advertisementWordArray, String[] advertiseContentArray) {
        try {
            Files.walkFileTree(Paths.get(htmlFileBasePath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String currentFilePath = path.toAbsolutePath().toString();
                    currentFilePath = StringUtils.replaceBackSlash(currentFilePath);
                    if(!currentFilePath.endsWith(".xhtml") && !currentFilePath.endsWith(".html")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String htmlContent = FileUtils.readFileAsString(currentFilePath);
                    Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
                    doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                            .syntax(Document.OutputSettings.Syntax.xml);

                    String currentFileName = FileUtils.getFileNameWithoutSuffix(currentFilePath);
                    boolean requiredDeletedAdvertisPage = isRequiredDeletedAdvertisPage(currentFileName, htmlContent, advertisementWordArray);
                    if(requiredDeletedAdvertisPage) {
                        return deleteAdvertisPageFile(currentFilePath, opfFilePath);
                    }

                    //先判断是不是epubw广告页
                    boolean epubwAdvertisPage = isAdvertisePageOfEpubw(htmlContent);
                    if(epubwAdvertisPage) {
                        //先删除页面中的广告语内容
                        String[] replaceResult = replaceAdvertisContent(currentFilePath, htmlContent, advertiseContentArray);
                        String newHtmlContent = replaceResult[1];
                        boolean requiredDelete = Boolean.valueOf(replaceResult[2]);
                        if(requiredDelete) {
                            return deleteAdvertisPageFile(currentFilePath, opfFilePath);
                        }
                        newHtmlContent = replaceTableContents(newHtmlContent);
                        //将剔除了广告语的页面内容再写入原文件中
                        FileUtils.write2File(currentFilePath, newHtmlContent);
                        return FileVisitResult.CONTINUE;
                    }

                    String newHTMLPageFileContent = doc.outerHtml();
                    newHTMLPageFileContent = replaceTableContents(newHTMLPageFileContent);
                    if(!newHTMLPageFileContent.equals(htmlContent)) {
                        FileUtils.write2File(currentFilePath, newHTMLPageFileContent);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static FileVisitResult deleteAdvertisPageFile(String currentFilePath, String opfFilePath) {
        //先查找页面是否包含图片
        List<String[]> requiredDeleteImageFilePaths = findRequiredDeleteImageFilePath(currentFilePath, opfFilePath);
        if(null != requiredDeleteImageFilePaths && requiredDeleteImageFilePaths.size() > 0) {
            for(String[] pathPair : requiredDeleteImageFilePaths) {
                String imageFilePath = pathPair[0];
                String imageFileHref = pathPair[1];
                if(StringUtils.isEmpty(imageFileHref)) {
                    continue;
                }
                //删除广告页面上的图片文件之前，先更新opf文件
                boolean updateResult = updateOpfFileBeforDeletePageFile(opfFilePath, imageFileHref);
                if(updateResult) {
                    File targetDeleteFile = new File(imageFilePath);
                    if(targetDeleteFile.exists()) {
                        //删除广告页上的图片文件
                        targetDeleteFile.delete();
                    }
                }
            }
        }
        //删除广告页面文件之前先更新opf
        String htmlFileHref = buildManifestItemHref(currentFilePath, opfFilePath);
        boolean updateResult = updateOpfFileBeforDeletePageFile(opfFilePath, htmlFileHref);
        if(updateResult) {
            File targetDeleteFile = new File(currentFilePath);
            if(targetDeleteFile.exists()) {
                //删除广告页面文件
                targetDeleteFile.delete();
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 根据img元素的src属性值，构建图片文件的绝对路径
     * @param htmlFilePath
     * @param imageSrcValue
     * @return
     */
    private static String buildImageFileAbsolutePath(String htmlFilePath, String imageSrcValue) {
        // 1. 获取HTML文件所在目录的Path对象
        Path htmlDirPath = Paths.get(htmlFilePath).getParent();

        // 2. 解析相对路径并转换为绝对路径
        Path resolvedPath = htmlDirPath.resolve(imageSrcValue).normalize();

        // 3. 转换为规范化的绝对路径字符串
        String imgFileAbsolutePath = resolvedPath.toAbsolutePath().toString();
        imgFileAbsolutePath = StringUtils.replaceBackSlash(imgFileAbsolutePath);
        return imgFileAbsolutePath;
    }

    /**
     * 判断是不是ePUBw.COM的广告页面
     * @param htmlContent
     * @return
     */
    private static boolean isAdvertisePageOfEpubw(String htmlContent) {
        if(htmlContent.contains("本书由“行行”整理")) {
            return true;
        }
        if(htmlContent.contains("读累了记得休息一会") || htmlContent.contains("如果你不知道读什么书") ||
                htmlContent.contains("www.ireadweek.com") || htmlContent.contains("公众号名称：幸福的味道")) {
            return true;
        }
        if(htmlContent.contains("本书由") && htmlContent.contains("ePUBw.COM") && htmlContent.contains("整理")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为需要直接整页删除的广告页面
     * @param htmlFileName     html文件名(不包含后缀名)
     * @param htmlContent      html文件内容
     * @return
     */
    private static boolean isRequiredDeletedAdvertisPage(String htmlFileName, String htmlContent, String[] advertisementWordArray) {
        if("ad_chapter".equalsIgnoreCase(htmlFileName)) {
            return true;
        }
        if(null == advertisementWordArray || advertisementWordArray.length == 0) {
            return false;
        }
        for (String advertisementWord : advertisementWordArray) {
            if(htmlContent.contains(advertisementWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在需要删除的广告页中查找需要删除的图片文件
     * @return
     */
    private static List<String[]> findRequiredDeleteImageFilePath(String htmlFilePath, String opfFilePath) {
        String htmlContent = FileUtils.readFileAsString(htmlFilePath);
        Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
        Elements imgElements = doc.select("img");
        if(null == imgElements || imgElements.size() == 0) {
            return null;
        }
        List<String[]> imageFilePaths = new ArrayList<>();
        for (Element imgElement : imgElements) {
            String src = imgElement.attr("src");
            String imgFileAbsolutePath = buildImageFileAbsolutePath(htmlFilePath, src);
            String imageItemHref = buildManifestItemHref(imgFileAbsolutePath, opfFilePath);
            String[] pathPair = new String[] {imgFileAbsolutePath, imageItemHref};
            imageFilePaths.add(pathPair);
        }
        return imageFilePaths;
    }

    /**
     * 根据图片文件的绝对路径，构建图片项的href属性值
     * @param imgFileAbsolutePath
     * @param opfFilePath
     * @return
     */
    private static String buildManifestItemHref(String imgFileAbsolutePath, String opfFilePath) {
        String opfFileContent = FileUtils.readFileAsString(opfFilePath);
        Document doc = Jsoup.parse(opfFileContent, "", Parser.xmlParser());
        Elements imageItems = doc.select("manifest > item");
        if(null == imageItems || imageItems.size() == 0) {
            return null;
        }
        String imageItemHref = null;
        String imageFileName = FileUtils.getFileName(imgFileAbsolutePath);
        for (Element imageItem : imageItems) {
            String hrefValue = imageItem.attr("href");
            if(hrefValue.endsWith(imageFileName)) {
                imageItemHref = hrefValue;
                break;
            }
        }
        return imageItemHref;
    }

    /**
     * 清除网页内容中插入的广告语
     * @param htmlFilePath
     * @param htmlContent
     * @param advertiseContentArray
     * @return
     */
    private static String[] replaceAdvertisContent(String htmlFilePath, String htmlContent, String[] advertiseContentArray) {
        if(null == advertiseContentArray || advertiseContentArray.length == 0) {
            return new String[] {htmlFilePath, htmlContent, "false"};
        }
        boolean containsAdvertise = false;
        for(String advertiseContent : advertiseContentArray) {
            if(htmlContent.contains(advertiseContent)) {
                htmlContent = htmlContent.replace(advertiseContent, "");
                containsAdvertise = true;
            }
        }
        if(!containsAdvertise) {
            return new String[] {htmlFilePath, htmlContent, "false"};
        }
        Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
        Elements divElements = doc.select("div");
        if(null != divElements && divElements.size() > 0) {
            if(divElements.size() > 3) {
                return new String[] {htmlFilePath, htmlContent, "false"};
            }
            Elements pElements = doc.select("p");
            if(null != pElements && pElements.size() > 0) {
                return new String[] {htmlFilePath, htmlContent, "false"};
            }
            return new String[] {htmlFilePath, htmlContent, "true"};
        } else {
            Elements pElements = doc.select("p");
            if(null != pElements && pElements.size() > 0) {
                return new String[] {htmlFilePath, htmlContent, "false"};
            }
            return new String[] {htmlFilePath, htmlContent, "true"};
        }
    }

    /**
     * 删除文件之前先更新opf文件
     * @param opfFilePath
     * @param targetHref
     */
    public static boolean updateOpfFileBeforDeletePageFile(String opfFilePath, String targetHref) {
        if(StringUtils.isEmpty(targetHref)) {
            System.out.println("targetHref:[" + targetHref + "]为空,故无需更新OPF文件.");
            return false;
        }
        // 读取OPF文件内容
        String content = FileUtils.readFileAsString(opfFilePath);

        // 使用Jsoup解析XML
        Document doc = Jsoup.parse(content, "", Parser.xmlParser());
        doc.outputSettings().prettyPrint(true).indentAmount(2).charset(StandardCharsets.UTF_8)
                .syntax(Document.OutputSettings.Syntax.xml);

        // 1. 在manifest中查找匹配的item
        Element manifest = doc.selectFirst("manifest");
        if (manifest == null) {
            System.out.println("未找到manifest元素");
            return false;
        }

        Elements targetItems = manifest.select(String.format("item[href=%s]", targetHref));
        if (targetItems.isEmpty()) {
            System.out.println("未找到匹配的item元素");
            return false;
        }

        Element itemToRemove = targetItems.first();
        String itemId = itemToRemove.attr("id");

        if (itemId.isEmpty()) {
            System.out.println("警告：被删除的<item>缺少id属性");
            return false;
        } else {
            // 2. 在spine中查找匹配的itemref
            Element spine = doc.selectFirst("spine");
            if (spine != null) {
                Element refsToRemove = spine.selectFirst(String.format("itemref[idref=%s]", itemId));
                if(null != refsToRemove) {
                    refsToRemove.remove();
                    System.out.println("从spine中移除了 id=" + itemId + "的itemref节点.");
                }
            }
        }

        // 3. 从manifest中移除item
        if(null != itemToRemove) {
            itemToRemove.remove();
            System.out.println("从manifest中移除了item: " + itemId);
        }

        // 4. 写回原文件
        String newHTMLPageFileContent = doc.outerHtml();
        FileUtils.write2File(opfFilePath, newHTMLPageFileContent);
        System.out.println("删除文件之前,OPF文件更新完成");
        return true;
    }

    private static String replaceTableContents(String newHTMLPageFileContent) {
        if(newHTMLPageFileContent.contains("Table of Contents")) {
            newHTMLPageFileContent = newHTMLPageFileContent.replace("Table of Contents", "目录");
        } else if(newHTMLPageFileContent.contains("TABLE OF CONTENTS")) {
            newHTMLPageFileContent = newHTMLPageFileContent.replace("TABLE OF CONTENTS", "目录");
        }
        return newHTMLPageFileContent;
    }

    private static String removeIncompatibleMetaContent(String htmlContent) {
        if(htmlContent.contains(inCompatibleMeta)) {
            htmlContent = htmlContent.replace(inCompatibleMeta, "");
        }
        return htmlContent;
    }

    /**
     * 将电子书的解压目录再压缩为zip文件
     * @param files
     * @param destZipFile
     * @param sourceCoverImageFilePath
     * @param targetCoverImageFilePath
     * @param renameCoverImageSuccess
     * @return
     */
    public static boolean zipEBook(List<String> files, String destZipFile, String sourceCoverImageFilePath, String targetCoverImageFilePath,
                                   boolean renameCoverImageSuccess, String baseDirPath) {
        String[] filePathArray = files.toArray(new String[]{});
        List<File> listFiles = new ArrayList<File>();
        for (int i = 0; i < filePathArray.length; i++) {
            File targetFile = new File(filePathArray[i]);
            String targetFilePath = targetFile.getAbsolutePath();
            targetFilePath = StringUtils.replaceBackSlash(targetFilePath);
            if(!targetFile.exists()) {
                if(!renameCoverImageSuccess) {
                    continue;
                }
                if(targetFilePath.equals(sourceCoverImageFilePath)) {
                    File targetCoverImageFile = new File(targetCoverImageFilePath);
                    if(!targetCoverImageFile.exists()) {
                        continue;
                    }
                    listFiles.add(targetCoverImageFile);
                }
            } else {
                listFiles.add(targetFile);
            }
        }
        boolean zipResult = true;
        try {
            return ZipUtils.zipFiles(listFiles, destZipFile, baseDirPath);
        } catch (IOException e) {
            zipResult = false;
        } finally {
            return zipResult;
        }
    }

    /**
     * 从书名中提取出作者姓名
     * @param bookName
     * @return
     */
    public static String getAuthorName(String bookName) {
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

    /**
     * 删除包含指定文本的元素
     * @param htmlDocument
     * @param word
     * @return
     */
    public static boolean removeElementContainsText(Document htmlDocument, String word) {
        if(StringUtils.isEmpty(word)) {
            return false;
        }
        word = word.trim();
        // 选择 body 下的所有元素
        Elements allElements = htmlDocument.body().select("span,div,p,h1,h2");

        boolean result = false;
        for (Element element : allElements) {
            // 获取元素的文本内容（去除前后空白）
            String text = element.ownText().trim();
            if (text.contains(word)) {
                // 删除匹配的元素
                element.remove();
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean bulkRemoveElementContainsText(Document htmlDocument, String[] wordArray) {
        if(null == wordArray || wordArray.length <= 0) {
            return false;
        }
        boolean result = false;
        for (String word : wordArray) {
            boolean removeResult = removeElementContainsText(htmlDocument, word);
            if(removeResult) {
                result = removeResult;
            }
        }
        return result;
    }
}
