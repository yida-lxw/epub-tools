package com.yida.epub.bean;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 20:19
 * @description Type your description over here.
 */
public class CoverPage {
	/**
	 * 页面title
	 */
	private String title;
	/**
	 * 封面图片文件名称
	 */
	private String coverImageName;
	/**
	 * 图片显示宽度
	 */
	private String imageWidth;
	/**
	 * 图片显示高度
	 */
	private String imageHeight;

	public CoverPage(String title, String coverImageName, String imageWidth, String imageHeight) {
		this.title = title;
		this.coverImageName = coverImageName;
		if (null == imageWidth || imageWidth.length() <= 0) {
			imageWidth = "100%";
		}
		if (null == imageHeight || imageHeight.length() <= 0) {
			imageHeight = "100%";
		}
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCoverImageName() {
		return coverImageName;
	}

	public void setCoverImageName(String coverImageName) {
		this.coverImageName = coverImageName;
	}

	public String getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}

	public String getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}
}
