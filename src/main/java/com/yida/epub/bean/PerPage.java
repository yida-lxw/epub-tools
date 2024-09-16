package com.yida.epub.bean;

import java.util.List;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 20:08
 * @description 书籍的每一页
 */
public class PerPage {
	/**
	 * 当前页码(从1开始计算)
	 */
	private int pageNumber;
	/**
	 * 页面标题
	 */
	private String title;
	/**
	 * 是否包含章节元素
	 */
	private boolean containsChapterNode;
	/**
	 * 标识书籍正文是否从此页开始
	 */
	private boolean start;
	/**
	 * 章节节点名称
	 */
	private String chapterNodeName;
	/**
	 * 当前页每一行的文本内容
	 */
	private List<String> lineContents;

	public PerPage(int pageNumber, String title, boolean containsChapterNode, boolean start, String chapterNodeName, List<String> lineContents) {
		this.pageNumber = pageNumber;
		this.title = title;
		this.containsChapterNode = containsChapterNode;
		this.start = start;
		this.chapterNodeName = chapterNodeName;
		this.lineContents = lineContents;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isContainsChapterNode() {
		return containsChapterNode;
	}

	public void setContainsChapterNode(boolean containsChapterNode) {
		this.containsChapterNode = containsChapterNode;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public String getChapterNodeName() {
		return chapterNodeName;
	}

	public void setChapterNodeName(String chapterNodeName) {
		this.chapterNodeName = chapterNodeName;
	}

	public List<String> getLineContents() {
		return lineContents;
	}

	public void setLineContents(List<String> lineContents) {
		this.lineContents = lineContents;
	}
}
