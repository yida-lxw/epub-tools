package com.yida.epub.bean;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 21:03
 * @description Type your description over here.
 */
public class ImageFileSource {
	private String imageFileName;
	private String imageId;

	public ImageFileSource(String imageFileName, String imageId) {
		this.imageFileName = imageFileName;
		this.imageId = imageId;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
