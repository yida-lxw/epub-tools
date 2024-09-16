package com.yida.epub.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author yida
 * @package com.yida.utils
 * @date 2022-07-04 09:44:
 * @description Type your description over here.
 */
public class ZipUtils {
	public static final int BUFFER = 8192;

	/**
	 * 解压文件
	 *
	 * @param zipPath 要解压的目标文件
	 * @param descDir 指定解压目录
	 * @return 解压结果：成功，失败
	 */
	@SuppressWarnings("rawtypes")
	public static boolean unZip(String zipPath, String descDir) {
		File zipFile = new File(zipPath);
		boolean flag = false;
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile, Charset.forName("utf8"));
			for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				InputStream in = null;
				OutputStream out = null;
				try {
					in = zip.getInputStream(entry);
					//指定解压后的文件夹+当前zip文件的名称
					String outPath = (descDir + zipEntryName).replace("/", File.separator);
					//判断路径是否存在,不存在则创建文件路径
					File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
					if (!file.exists()) {
						file.mkdirs();
					}
					//判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
					if (new File(outPath).isDirectory()) {
						continue;
					}
					//保存文件路径信息（可利用md5.zip名称的唯一性，来判断是否已经解压）
					out = new FileOutputStream(outPath);
					byte[] buf1 = new byte[8192];
					int len;
					while ((len = in.read(buf1)) > 0) {
						out.write(buf1, 0, len);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != in) {
						in.close();
					}

					if (null != out) {
						out.close();
					}
				}
			}
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
			try {
				zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean zip(String inputFileName, String zipOutPutFileName) throws Exception {
		return zip(new File(inputFileName), zipOutPutFileName);
	}

	public static boolean zip(File inputFile, String zipOutPutFileName) {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipOutPutFileName));
			return zip(inputFile, out, "");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean zip(File inputFile, ZipOutputStream out, String base) {
		if (inputFile.isDirectory()) {
			File[] fl = inputFile.listFiles();
			try {
				out.putNextEntry(new ZipEntry(base + "/"));
				base = base.length() == 0 ? "" : base + "/";
				for (int i = 0; i < fl.length; i++) {
					zip(fl[i], out, base + fl[i].getName());
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			FileInputStream in = null;
			try {
				out.putNextEntry(new ZipEntry(base));
				in = new FileInputStream(inputFile);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean zipFiles(File[] srcFiles, File zipFile) {
		// 判断压缩后的文件存在不，不存在则创建
		if (!zipFile.exists()) {
			try {
				zipFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 创建 FileOutputStream 对象
		FileOutputStream fileOutputStream = null;
		// 创建 ZipOutputStream
		ZipOutputStream zipOutputStream = null;
		// 创建 FileInputStream 对象
		FileInputStream fileInputStream = null;

		try {
			// 实例化 FileOutputStream 对象
			fileOutputStream = new FileOutputStream(zipFile);
			// 实例化 ZipOutputStream 对象
			zipOutputStream = new ZipOutputStream(fileOutputStream);
			// 创建 ZipEntry 对象
			ZipEntry zipEntry = null;
			// 遍历源文件数组
			for (int i = 0; i < srcFiles.length; i++) {
				// 将源文件数组中的当前文件读入 FileInputStream 流中
				fileInputStream = new FileInputStream(srcFiles[i]);
				// 实例化 ZipEntry 对象，源文件数组中的当前文件
				zipEntry = new ZipEntry(srcFiles[i].getName());
				zipOutputStream.putNextEntry(zipEntry);
				// 该变量记录每次真正读的字节个数
				int len;
				// 定义每次读取的字节数组
				byte[] buffer = new byte[1024];
				while ((len = fileInputStream.read(buffer)) > 0) {
					zipOutputStream.write(buffer, 0, len);
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				zipOutputStream.closeEntry();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				zipOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 压缩成ZIP 方法1
	 *
	 * @param srcDir           压缩文件夹路径
	 * @param out              压缩文件输出流
	 * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
	 *                         <p>
	 *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */

	public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
			throws RuntimeException {
		long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
			long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
		} catch (Exception e) {
			throw new RuntimeException("zip error from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 压缩成ZIP 方法2
	 *
	 * @param srcFiles 需要压缩的文件列表
	 * @param out      压缩文件输出流
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */

	public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
		long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (File srcFile : srcFiles) {
				byte[] buf = new byte[BUFFER];
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				while ((len = in.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
			long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
		} catch (Exception e) {
			throw new RuntimeException("zip error from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 递归压缩方法
	 *
	 * @param sourceFile       源文件
	 * @param zos              zip输出流
	 * @param name             压缩后的名称
	 * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
	 *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @throws Exception
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name,
								 boolean KeepDirStructure) throws Exception {
		byte[] buf = new byte[BUFFER];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			// Complete the entry
			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// 需要保留原来的文件结构时,需要对空文件夹进行处理
				if (KeepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));
					// 没有文件，不需要文件的copy
					zos.closeEntry();
				}
			} else {
				for (File file : listFiles) {
					// 判断是否需要保留原来的文件结构
					if (KeepDirStructure) {
						// 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
						// 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
						compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
					} else {
						compress(file, zos, file.getName(), KeepDirStructure);
					}
				}
			}
		}
	}


	public static boolean zipFiles(List<File> listFiles, String destZipFile) throws FileNotFoundException,
			IOException {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(destZipFile));
			for (File file : listFiles) {
				if (file.isDirectory()) {
					zipDirectory(file, file.getName(), zos);
				} else {
					zipFile(file, zos);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			zos.flush();
			zos.close();
		}
	}

	/**
	 * Compresses files represented in an array of paths
	 *
	 * @param files       a String array containing file paths
	 * @param destZipFile The path of the destination zip file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean zip(String[] files, String destZipFile) throws FileNotFoundException, IOException {
		List<File> listFiles = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			listFiles.add(new File(files[i]));
		}
		return zipFiles(listFiles, destZipFile);
	}

	public static boolean zip(List<String> files, String destZipFile) throws FileNotFoundException, IOException {
		return zip(files.toArray(new String[]{}), destZipFile);
	}

	/**
	 * Adds a directory to the current zip output stream
	 *
	 * @param folder       the directory to be  added
	 * @param parentFolder the path of parent directory
	 * @param zos          the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void zipDirectory(File folder, String parentFolder,
									ZipOutputStream zos) throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			long bytesRead = 0;
			byte[] bytesIn = new byte[BUFFER];
			int read = 0;
			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}
			zos.closeEntry();
		}
	}

	/**
	 * Adds a file to the current zip output stream
	 *
	 * @param file the file to be added
	 * @param zos  the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void zipFile(File file, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		zos.putNextEntry(new ZipEntry(file.getName()));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		long bytesRead = 0L;
		byte[] bytesIn = new byte[BUFFER];
		int read = 0;
		while ((read = bis.read(bytesIn)) != -1) {
			zos.write(bytesIn, 0, read);
			bytesRead += read;
		}
		zos.closeEntry();
	}
}
