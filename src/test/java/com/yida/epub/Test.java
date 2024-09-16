package com.yida.epub;

import com.yida.epub.utils.FileUtils;

/**
 * @author yida
 * @package com.yida
 * @date 2022-07-04 11:09:
 * @description Type your description over here.
 */
public class Test {
	public static void main(String[] args) {
		//若目标文件已存在，则会覆盖原文件
		String unzipPath = "/Users/yida/Downloads/ttt/韩愈选集.孙昌武/";
		FileUtils.copyFiles2Dir(unzipPath + "a/", unzipPath + "b/");
	}
}
