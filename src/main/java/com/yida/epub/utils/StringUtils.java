package com.yida.epub.utils;

import com.yida.epub.constants.Constants;

/**
 * @author yida
 * @package com.yida.epub.utils
 * @date 2024-09-17 09:34
 * @description Type your description over here.
 */
public class StringUtils {
	public static boolean isEmpty(String source) {
		return null == source || source.isEmpty();
	}

	public static boolean isNotEmpty(String source) {
		return !isEmpty(source);
	}

	public static String leftPad(int num, int len) {
		return String.format("%0" + len + "d", num);
	}

	/**
	 * 替换路径中的反斜杠为斜杠
	 *
	 * @param appRootPath
	 */
	public static String replaceBackSlash(String appRootPath) {
		if (null != appRootPath && !"".equals(appRootPath)) {
			appRootPath = appRootPath.replaceAll(Constants.BACK_SLASH_IN_REGEX, "/");
		}
		return appRootPath;
	}

	public static String fixFilePath(String filePath) {
		filePath = StringUtils.replaceBackSlash(filePath);
		if (!filePath.endsWith("/")) {
			filePath = filePath + "/";
		}
		return filePath;
	}
}
