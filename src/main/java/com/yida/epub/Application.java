package com.yida.epub;

import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于将epub格式电子书中的繁体字转换为简体字
 *
 * @author yida
 */
public class Application {

	public static void main(String[] args) throws IOException {
		String suffix = ".epub";
		String basePath = "/Users/yida/Downloads/ttt/";
		List<String> epubFileList = FileUtils.listFileWithSpecifedSuffix(basePath, suffix);
		if (null == epubFileList || epubFileList.size() <= 0) {
			System.out.println("There is no any book.");
			return;
		}
		for (String bookFilePath : epubFileList) {
			String bookName = FileUtils.getFileNameWithoutSuffix(bookFilePath);
			translateBook(basePath, bookName);
		}
	}

	private static void translateBook(String basePath, String orignalFileName) throws IOException {
		String epub = "epub";
		String zip = "zip";
		String outputDirName = "output";
		String path = basePath + orignalFileName + "." + epub;
		String unzipPath = basePath + orignalFileName + File.separator;
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
			String textOutPutFilePath = textFilePath + File.separator + outputDirName + File.separator;

			boolean translated = translate2Simple(unzipPath, textFilePath, textOutPutFilePath, true, tocFilePath, false, "utf8");
			System.out.println("翻译成功？" + translated);
			if (translated) {
				String zipFileName = FileUtils.getFileName(path);
				String zipFilePath = unzipPath + zip + File.separator;
				File zipFileDir = new File(zipFilePath);
				if (!zipFileDir.exists()) {
					zipFileDir.mkdirs();
				}

				String zipFileFullPath = zipFilePath + zipFileName;
				zipFileFullPath = zipFileFullPath.replace(epub, zip);
				String txtOutPutPath = unzipPath + textOutPutFilePath;
				boolean copyFilesResult = FileUtils.copyFiles2Dir(txtOutPutPath, unzipPath + textFilePath);
				System.out.println("复制xhtml文件成功？" + copyFilesResult);
				if (copyFilesResult) {
					boolean deleted = FileUtils.deleteDir(txtOutPutPath);
					System.out.println("删除output文件夹成功？" + deleted);
				}
				List<String> excludeFiles = new ArrayList<>();
				excludeFiles.add(unzipPath + zip + File.separator);
				List<String> allFileItems = FileUtils.listAllFile(unzipPath, excludeFiles, true);
				boolean ziped = ZipUtils.zip(allFileItems, zipFileFullPath);
				System.out.println("压缩成功？" + ziped);
				if (ziped) {
					String epubSuffix = epub;
					boolean renamed = FileUtils.renameTo(zipFileFullPath, epubSuffix);
					System.out.println("重命名为epub成功？" + renamed);
					if (renamed) {
						String fileName = FileUtils.getFileNameWithoutSuffix(zipFileFullPath);
						String epubFilePath = unzipPath + zip + File.separator + fileName + "." + epubSuffix;
						String destEpubFilePath = basePath + "new_" + fileName + "." + epubSuffix;
						boolean copyed = FileUtils.copyFile(epubFilePath, destEpubFilePath);
						System.out.println("epub文件复制成功？" + copyed);
						if (copyed) {
							FileUtils.renameTo(zipPath, epub);
							FileUtils.deleteDir(unzipPath);
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
				String content = FileUtils.readFileAsString(eachFilePath);
				content = FileUtils.toSimple(content);
				String fileName = FileUtils.getFileName(eachFilePath);
				String outputFileName = outputFilePath + fileName;
				content = content.replace("著作", "珠咗");
				content = content.replace("著名", "珠瞑");
				content = content.replace("原著", "员株");
				content = content.replace("显著", "显株");
				content = content.replace("编著", "编株");
				content = content.replace("译著", "译株");
				content = content.replace("论著", "论株");
				content = content.replace("土著", "土株");
				content = content.replace("臭名昭著", "臭名昭珠");
				content = content.replace("恶名昭著", "恶名昭珠");
				content = content.replace("知微见著", "知微见株");


				content = content.replace("著", "着");
				content = content.replace("珍．奥斯汀", "简.奥斯汀");


				content = content.replace("珠咗", "著作");
				content = content.replace("珠瞑", "著名");
				content = content.replace("员株", "原著");
				content = content.replace("显株", "显著");
				content = content.replace("编株", "编著");
				content = content.replace("译株", "译著");
				content = content.replace("论株", "论著");
				content = content.replace("土株", "土著");
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

	private static Map<String, String> autoDeterminTheHtmlFilePath(String basePath, String orignalFileName) {
		Map<String, String> pathMap = new HashMap<>();
		String baseDir = basePath + orignalFileName + "/";
		if (new File(baseDir + "OEBPS/Text").exists()) {
			pathMap.put("html", "OEBPS/Text");
			if (new File(baseDir + "OEBPS/Text/toc.ncx").exists()) {
				pathMap.put("toc", "OEBPS/Text/toc.ncx");
			} else if (new File(baseDir + "OEBPS/toc.ncx").exists()) {
				pathMap.put("toc", "OEBPS/toc.ncx");
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
		} else {
			pathMap.put("html", "");
			pathMap.put("toc", "toc.ncx");
		}
		return pathMap;
	}
}
