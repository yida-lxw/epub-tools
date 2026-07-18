package com.yida.epub.bean;

import com.yida.epub.enums.TableContentNodeType;

import java.util.List;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2024-09-16 19:31
 * @description 书籍目录节点
 */
public class TableContentNode {
	/**
	 * 当前节点对应第几页(从1开始计算)
	 */
	private int pageNumber;

	/**
	 * 锚点元素id
	 */
	private String anchorId;

	/**
	 * 节点深度
	 */
	private int deepth;

	/**
	 * 目录节点名称
	 */
	private String tableContentNodeName;

	/**
	 * 父节点id
	 */
	private String parentId;

	/**
	 * 子节点id列表
	 */
	private List<String> childIds;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getAnchorId() {
		return anchorId;
	}

	public void setAnchorId(String anchorId) {
		this.anchorId = anchorId;
	}

	public int getDeepth() {
		return deepth;
	}

	public void setDeepth(int deepth) {
		this.deepth = deepth;
	}

	public String getTableContentNodeName() {
		return tableContentNodeName;
	}

	public void setTableContentNodeName(String tableContentNodeName) {
		this.tableContentNodeName = tableContentNodeName;
	}

	public boolean firstLevelNode() {
		return this.deepth == 0;
	}

	public TableContentNodeType getTableContentNodeType() {
		return TableContentNodeType.of(this.deepth);
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<String> getChildIds() {
		return childIds;
	}

	public void setChildIds(List<String> childIds) {
		this.childIds = childIds;
	}
}
