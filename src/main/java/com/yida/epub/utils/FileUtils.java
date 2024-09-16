package com.yida.epub.utils;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yida
 * @package com.yida.utils
 * @date 2022-07-04 08:51:
 * @description Type your description over here.
 */
public class FileUtils {

	/**
	 * @param tranditional
	 * @return String
	 * @description 繁体字转成简体字
	 * @author yida
	 * @date 2022-07-04 09:03:29
	 */
	public static String toSimple(String tranditional) {
		return ZhConverterUtil.toSimple(tranditional);
	}

	/**
	 * @param filePath
	 * @return String
	 * @description 读取文本文件内容
	 * @author yida
	 * @date 2022-07-04 09:01:05
	 */
	public static String readFileAsString(String filePath) {
		return readFileAsString(filePath, "UTF-8");
	}

	/**
	 * @param filePath
	 * @param charset
	 * @return String
	 * @description 读取文本文件内容
	 * @author yida
	 * @date 2022-07-04 09:01:05
	 */
	public static String readFileAsString(String filePath, String charset) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			inputStream = new FileInputStream(filePath);
			String line;
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
			line = bufferedReader.readLine();
			while (line != null) {
				stringBuilder.append(line).append("\n");
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return stringBuilder.toString();
		}
	}

	/**
	 * @param filepath
	 * @param content
	 * @description 将字符串写入文件
	 * @author yida
	 * @date 2022-07-04 09:09:28
	 */
	public static void write2File(String filepath, String content) {
		write2File(filepath, content, "UTF-8");
	}

	/**
	 * @param filepath
	 * @param content
	 * @description 将字符串写入文件
	 * @author yida
	 * @date 2022-07-04 09:09:28
	 */
	public static void write2File(String filepath, String content, String charsetName) {
		write2File(filepath, content, charsetName, false);
	}

	/**
	 * @param filepath
	 * @param content
	 * @description 将字符串写入文件
	 * @author yida
	 * @date 2022-07-04 09:09:28
	 */
	public static void write2File(String filepath, String content, String charsetName, boolean append) {
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}
			try (BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, append), charsetName))) {
				bufferedWriter.write(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> listAllFile(String filePath) {
		return listAllFile(filePath, false);
	}

	public static List<String> listAllFile(String filePath, boolean filterDir) {
		return listAllFile(filePath, null, filterDir);
	}

	public static List<String> listFileWithSpecifedSuffix(String filePath, String suffix) {
		List<String> fileList = new ArrayList<>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		for (File theFile : files) {
			boolean valid = true;
			String absolutePath = theFile.getAbsolutePath();
			if (theFile.isDirectory()) {
				continue;
			}
			if (!absolutePath.endsWith(suffix)) {
				continue;
			}
			fileList.add(absolutePath);
		}
		return fileList;
	}

	public static List<String> listAllFile(String filePath, List<String> excludeSuffixs, boolean filterDir) {
		List<String> fileList = new ArrayList<>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		boolean requiredclude = (null != excludeSuffixs && excludeSuffixs.size() > 0);
		for (File theFile : files) {
			boolean valid = true;
			String absolutePath = theFile.getAbsolutePath();
			if (theFile.isDirectory() && !absolutePath.endsWith("/")) {
				absolutePath += "/";
			}
			if (filterDir && theFile.isDirectory()) {
				for (String excludeSuffix : excludeSuffixs) {
					if (absolutePath.endsWith(excludeSuffix)) {
						valid = false;
						break;
					}
				}
			}

			if (requiredclude) {
				String suffix = getFileSuffix(absolutePath);
				for (String excludeSuffix : excludeSuffixs) {
					if (suffix.equalsIgnoreCase(excludeSuffix)) {
						valid = false;
						break;
					}
				}
			}
			if (valid) {
				fileList.add(absolutePath);
			}
		}
		return fileList;
	}

	/**
	 * @param filePath
	 * @return String
	 * @description 获取文件名称
	 * @author yida
	 * @date 2022-07-04 09:23:07
	 */
	public static String getFileName(String filePath) {
		int index = filePath.lastIndexOf("/");
		return filePath.substring(index + 1);
	}

	public static String getFileNameWithoutSuffix(String filePath) {
		int index = filePath.lastIndexOf("/");
		String fileName = filePath.substring(index + 1);
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		return fileName;
	}

	/**
	 * @param filePath
	 * @return String
	 * @description 获取文件后缀名
	 * @author yida
	 * @date 2022-07-04 10:40:49
	 */
	public static String getFileSuffix(String filePath) {
		int index = filePath.lastIndexOf(".");
		return filePath.substring(index + 1);
	}

	/**
	 * @param filePath
	 * @param newSuffix
	 * @description 修改文件的后缀名
	 * @author yida
	 * @date 2022-07-04 09:41:47
	 */
	public static boolean renameTo(String filePath, String newSuffix) {
		String orignalPreffix = filePath.substring(0, filePath.lastIndexOf(".") + 1);
		File file = new File(filePath);
		File newfile = new File(orignalPreffix + newSuffix);
		return file.renameTo(newfile);
	}

	/**
	 * @param srcFile
	 * @param destFile
	 * @description 复制文件(若目标文件已存在 ， 则会覆盖原文件)
	 * @author yida
	 * @date 2022-07-04 10:54:12
	 */
	public static boolean copyFile(String srcFile, String destFile) {
		try {
			File destFileObj = new File(destFile);
			if (!destFileObj.exists()) {
				destFileObj.createNewFile();
			}
			org.apache.commons.io.FileUtils.copyFile(new File(srcFile), destFileObj);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param srcDir
	 * @param destDir
	 * @return boolean
	 * @description 复制文件夹(若目标文件已存在 ， 则会覆盖原文件)
	 * @author yida
	 * @date 2022-07-04 10:55:31
	 */
	public static boolean copyDir(String srcDir, String destDir) {
		try {
			org.apache.commons.io.FileUtils.copyDirectory(new File(srcDir), new File(destDir));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @param sourcePath
	 * @param destPath
	 * @description 将指定文件夹下的所有文件和文件夹复制到目标文件夹下
	 * @author yida
	 * @date 2022-07-04 11:18:10
	 */
	public static boolean copyFiles2Dir(String sourcePath, String destPath) {
		File file = new File(sourcePath);
		String[] filePath = file.list();

		if (!(new File(destPath)).exists()) {
			(new File(destPath)).mkdir();
		}
		boolean result = true;
		for (int i = 0; i < filePath.length; i++) {
			if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
				boolean success = copyDir(sourcePath + File.separator + filePath[i], destPath + File.separator + filePath[i]);
				if (result) {
					result = success;
				}
			}
			if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
				boolean success = copyFile(sourcePath + File.separator + filePath[i], destPath + File.separator + filePath[i]);
				if (result) {
					result = success;
				}
			}
		}
		return result;
	}

	public static boolean deleteDir(String directory) {
		return deleteDir(new File(directory));
	}

	/**
	 * @param directory
	 * @return boolean
	 * @description 删除指定文件夹
	 * @author yida
	 * @date 2022-07-04 11:02:26
	 */
	public static boolean deleteDir(File directory) {
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(directory);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @param filePath
	 * @return String
	 * @description 从classpath读取文件内容
	 * @author yida
	 * @date 2024-09-16 21:39:34
	 */
	public String readFileFromClasspath(String filePath) throws IOException {
		InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
		if (null == inputStream) {
			filePath = "/" + filePath;
			inputStream = FileUtils.class.getResourceAsStream(filePath);
		}
		return IOUtils.toString(inputStream);
	}

	/**
	 * @param sourceDirPath
	 * @param zipFilePath
	 * @description 将指定目录打包为epub
	 * @author yida
	 * @date 2024-09-16 21:57:07
	 */
	private static void pack2Epub(String sourceDirPath, String zipFilePath) throws IOException {
		Path p = Files.createFile(Paths.get(zipFilePath));
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(p))) {
			Path filePath = Paths.get(sourceDirPath);
			Files.walk(filePath)
					.filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						ZipEntry zipEntry = new ZipEntry(filePath.relativize(path).toString());
						try {
							zipOutputStream.putNextEntry(zipEntry);
							zipOutputStream.write(Files.readAllBytes(path));
							zipOutputStream.closeEntry();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (null != zipOutputStream) {
								try {
									zipOutputStream.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
		}
	}
}
