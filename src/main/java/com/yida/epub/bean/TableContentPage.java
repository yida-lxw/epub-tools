package com.yida.epub.bean;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 20:30
 * @description 书籍目录页
 */
public class TableContentPage {
	private String title;
	/**
	 * 总页数
	 */
	private int totalPage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}
