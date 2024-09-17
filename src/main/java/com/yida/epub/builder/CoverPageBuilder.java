package com.yida.epub.builder;

import com.yida.epub.bean.CoverPage;
import com.yida.epub.utils.FileUtils;
import com.yida.epub.utils.StringUtils;

import java.io.File;

/**
 * @author yida
 * @package com.yida.epub.builder
 * @date 2024-09-17 13:54
 * @description Type your description over here.
 */
public class CoverPageBuilder {
	private CoverPageBuilder() {
	}

	private static class SingletonHolder {
		private static final CoverPageBuilder INSTANCE = new CoverPageBuilder();
	}

	public static CoverPageBuilder getInstance() {
		return CoverPageBuilder.SingletonHolder.INSTANCE;
	}

	/**
	 * @param coverPage
	 * @param outputBasePath
	 * @param coverImagePath
	 * @description 构建封面页
	 * @author yida
	 * @date 2024-09-17 16:09:17
	 */
	public void buildCoverPage(CoverPage coverPage, String outputBasePath, String coverImagePath) {
		boolean containsCoverPage = coverPage.isContainsCoverPage();
		//若原PDF中不包含封面页
		if (!containsCoverPage) {
			//若用户也没有传入封面图，则什么都不做
			if (StringUtils.isEmpty(coverImagePath)) {
				//do nothing at here
			} else {
				File coverImageFile = new File(coverImagePath);
				//若用户传入了封面图，但该图片文件不存在，则也什么都不做
				if (!coverImageFile.exists()) {
					//do nothing at here
				} else {
					String parentPath = coverImageFile.getParent();
					parentPath = StringUtils.fixFilePath(parentPath);
					outputBasePath = StringUtils.fixFilePath(outputBasePath);
					outputBasePath = outputBasePath + "images/";
					if (outputBasePath.equals(parentPath)) {
						//do nothing at here
					} else {
						String imageFileName = coverImageFile.getName();
						String destImageFilePath = outputBasePath + imageFileName;
						FileUtils.copyFile(coverImagePath, destImageFilePath);
						buildHTMLForCoverPage(coverPage, outputBasePath);
					}
				}
			}
		} else {
			buildHTMLForCoverPage(coverPage, outputBasePath);
		}
	}

	private void buildHTMLForCoverPage(CoverPage coverPage, String outputBasePath) {
		String title = coverPage.getTitle();
		String coverImageName = coverPage.getCoverImageName();
		String imageWidth = coverPage.getImageWidth();
		String imageHeight = coverPage.getImageHeight();
		String htmlContent = FileUtils.readFileFromClasspath("epub/titlePage.xhtml");
		htmlContent = htmlContent.replace("${title}", title)
				.replace("${coverImage}", coverImageName)
				.replace("${imageWidth}", imageWidth)
				.replace("${imageHeight}", imageHeight);
		outputBasePath = StringUtils.fixFilePath(outputBasePath);
		String coverImageFileName = coverImageName.substring(0, coverImageName.lastIndexOf("."));
		String coverPageFileName = "index_split_" + coverImageFileName + ".html";
		String outputCoverPageFilePath = outputBasePath + title + "/" + coverPageFileName;
		FileUtils.write2File(outputCoverPageFilePath, htmlContent);
	}
}
