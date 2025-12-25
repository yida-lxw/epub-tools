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
import java.nio.file.StandardCopyOption;
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
		try (InputStream inputStream = new FileInputStream(filePath);
			 BufferedReader bufferedReader = new BufferedReader(
					 new InputStreamReader(inputStream, charset))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
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
			absolutePath = absolutePath.replace("\\", "/");
			if (theFile.isDirectory() && !absolutePath.endsWith("/")) {
				absolutePath += "/";
			}
			if (filterDir && theFile.isDirectory()) {
				for (String excludeSuffix : excludeSuffixs) {
					excludeSuffix = excludeSuffix.replace("\\", "/");
					if (absolutePath.endsWith(excludeSuffix)) {
						valid = false;
						break;
					}
				}
			}

			if (requiredclude) {
				String suffix = getFileSuffix(absolutePath);
				for (String excludeSuffix : excludeSuffixs) {
					excludeSuffix = excludeSuffix.replace("\\", "/");
					if (suffix.equalsIgnoreCase(excludeSuffix)) {
						valid = false;
						break;
					}
				}
			}
			if (valid) {
				fileList.add(absolutePath);
				// 递归遍历子目录
				if (theFile.isDirectory()) {
					fileList.addAll(listAllFile(absolutePath, excludeSuffixs, filterDir));
				}
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
		int lastIndex = fileName.lastIndexOf(".");
		if(lastIndex != -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
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
	 * 文件重命名
	 * @param sourceFilePath   待改名的文件绝对路径
	 * @param targetFilePath   重命名后的文件绝对路径
	 * @return
	 */
	public static boolean renameFile(Path sourceFilePath, Path targetFilePath) {
		try {
			Files.move(sourceFilePath, targetFilePath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 文件重命名
	 * @param sourceFilePath   待改名的文件绝对路径
	 * @param newFileName      文件的新文件名(带后缀名)
	 * @return
	 */
	public static boolean renameFile(String sourceFilePath, String newFileName) {
		Path sourcePath = Paths.get(sourceFilePath);
		Path sourceParentPath = sourcePath.getParent();
		Path targetPath = sourceParentPath.resolve(newFileName);
		try {
			Files.move(sourcePath, targetPath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 文件剪切
	 * @param sourceFilePath   待剪切的文件绝对路径
	 * @param targetFilePath   文件剪切后的绝对路径
	 * @return
	 */
	public static boolean cutFile(String sourceFilePath, String targetFilePath) {
		Path sourcePath = Paths.get(sourceFilePath);
		Path targetPath = Paths.get(targetFilePath);
		try {
			Files.move(sourcePath, targetPath);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 文件复制
	 *
	 * @param sourceFilePath
	 * @param targetFilePath
	 * @return
	 */
	public static boolean copyFile(Path sourceFilePath, Path targetFilePath) {
		String sourceFileAbsolutePath = sourceFilePath.toAbsolutePath().toString();
		sourceFileAbsolutePath = StringUtils.replaceBackSlash(sourceFileAbsolutePath);
		String targetFileAbsolutePath = targetFilePath.toAbsolutePath().toString();
		targetFileAbsolutePath = StringUtils.replaceBackSlash(targetFileAbsolutePath);
		return copyFile(sourceFileAbsolutePath, targetFileAbsolutePath);
	}

	/**
	 * 复制文件(若目标文件已存在 ， 则会覆盖原文件)
	 *
	 * @param sourceFileAbsolutePath
	 * @param targetFileAbsolutePath
	 * @return
	 */
	public static boolean copyFile(String sourceFileAbsolutePath, String targetFileAbsolutePath) {
		targetFileAbsolutePath = StringUtils.replaceBackSlash(targetFileAbsolutePath);
		Path targetPath = null;
		Path targetFilePath = Paths.get(targetFileAbsolutePath);
		try {
			targetPath = Files.copy(Paths.get(sourceFileAbsolutePath), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("复制文件[" + sourceFileAbsolutePath + "]至[" + targetFileAbsolutePath + "]出错了:\n" + e.getMessage());
			return false;
		}
		if (null == targetPath) {
			return false;
		}
		String targetPathString = targetPath.toAbsolutePath().toString();
		targetPathString = StringUtils.replaceBackSlash(targetPathString);
		return targetPathString.equals(targetFileAbsolutePath);
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
		// 尝试使用增强的删除方法，支持重试和文件解锁
		return forceDeleteDirectoryWithRetry(directory, 3, 1000);
	}

	/**
	 * 强制删除目录，带有重试机制和文件解锁处理
	 * @param directory 要删除的目录
	 * @param maxRetries 最大重试次数
	 * @param retryDelayMs 重试延迟时间（毫秒）
	 * @return 是否删除成功
	 */
	private static boolean forceDeleteDirectoryWithRetry(File directory, int maxRetries, long retryDelayMs) {
		int attempt = 0;
		boolean deleted = false;
		IOException lastException = null;

		while (attempt < maxRetries && !deleted) {
			try {
				attempt++;
				// 先尝试递归删除所有文件，确保文件流关闭
				if (directory.exists()) {
					deleteDirectoryRecursively(directory);
				}
				deleted = true;
			} catch (IOException e) {
				lastException = e;
				System.out.println("删除尝试 " + attempt + " 失败: " + e.getMessage() + ", 正在重试...");
				try {
					Thread.sleep(retryDelayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		if (!deleted && lastException != null) {
			lastException.printStackTrace();
		}
		return deleted;
	}

	/**
	 * 递归删除目录，尝试解锁和关闭文件流
	 * @param file 要删除的文件或目录
	 * @throws IOException 如果删除失败
	 */
	private static void deleteDirectoryRecursively(File file) throws IOException {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteDirectoryRecursively(child);
				}
			}
		}

		// 对于文件，尝试强制删除
		if (file.exists()) {
			// 尝试强制删除文件，确保释放资源
			if (!forceDeleteFile(file)) {
				throw new IOException("Unable to delete " + file.getAbsolutePath());
			}
		}
	}

	/**
	 * 强制删除文件，尝试多种方式
	 * @param file 要删除的文件
	 * @return 是否删除成功
	 */
	private static boolean forceDeleteFile(File file) {
		// 尝试标准删除
		if (file.delete()) {
			return true;
		}

		// 如果失败，尝试设置为可写再删除
		file.setWritable(true);
		if (file.delete()) {
			return true;
		}

		// 最后尝试使用Java NIO删除
		try {
			java.nio.file.Files.delete(file.toPath());
			return true;
		} catch (IOException e) {
			// 如果都失败，记录错误但不抛出异常，让上层处理
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
	public static String readFileFromClasspath(String filePath) {
		InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
		if (null == inputStream) {
			filePath = "/" + filePath;
			inputStream = FileUtils.class.getResourceAsStream(filePath);
		}
		try {
			return IOUtils.toString(inputStream);
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	public static InputStream getInputStreamFromClasspath(String filePath) {
		InputStream inputStream = FileUtils.class.getResourceAsStream(filePath);
		if (null == inputStream) {
			filePath = "/" + filePath;
			inputStream = FileUtils.class.getResourceAsStream(filePath);
		}
		return inputStream;
	}

	/**
	 * @param sourceDirPath
	 * @param zipFilePath
	 * @description 将指定目录打包为epub
	 * @author yida
	 * @date 2024-09-16 21:57:07
	 */
	private static void pack2Epub(String sourceDirPath, String zipFilePath) throws IOException {
		Path zipPath = Paths.get(zipFilePath);
		if (Files.exists(zipPath)) {
			Files.delete(zipPath);
		}

		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
			Path sourceDir = Paths.get(sourceDirPath);
			Files.walk(sourceDir)
					.filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						try (InputStream is = Files.newInputStream(path)) {
							String relativePath = sourceDir.relativize(path).toString();
							zos.putNextEntry(new ZipEntry(relativePath));
							IOUtils.copy(is, zos);
							zos.closeEntry();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		}
	}
}
