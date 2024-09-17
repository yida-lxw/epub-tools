package com.yida.epub.bean;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 20:19
 * @description 封面页
 */
public class CoverPage {
	/**
	 * 原PDF中是否包含封面页
	 */
	private boolean containsCoverPage;
	/**
	 * 若原PDF中包含封面页，则封面页的索引是多少(从零开始计算)
	 */
	private int coverPageIndex;
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

	public CoverPage(boolean containsCoverPage, int coverPageIndex, String title, String coverImageName, String imageWidth, String imageHeight) {
		this.containsCoverPage = containsCoverPage;
		this.coverPageIndex = coverPageIndex;
		if (null == imageWidth || imageWidth.length() <= 0) {
			imageWidth = "100%";
		}
		if (null == imageHeight || imageHeight.length() <= 0) {
			imageHeight = "100%";
		}
		this.title = title;
		this.coverImageName = coverImageName;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public CoverPage(String title, String coverImageName, String imageWidth, String imageHeight) {
		this(true, 0, title, coverImageName, imageWidth, imageHeight);
	}

	public boolean isContainsCoverPage() {
		return containsCoverPage;
	}

	public void setContainsCoverPage(boolean containsCoverPage) {
		this.containsCoverPage = containsCoverPage;
	}

	public int getCoverPageIndex() {
		return coverPageIndex;
	}

	public void setCoverPageIndex(int coverPageIndex) {
		this.coverPageIndex = coverPageIndex;
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
