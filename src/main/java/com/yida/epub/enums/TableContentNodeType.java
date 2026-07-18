package com.yida.epub.enums;

import com.yida.epub.constants.Constants;

/**
 * @author yida
 * @package com.yida.epub.enums
 * @date 2024-09-16 19:56
 * @description 书籍目录节点类型
 */
public enum TableContentNodeType {
	//一级节点
	FIRST_LEVEL_NODE(0, Constants.FIRST_LEVEL_NODE_HTML_FRAGMENT),
	OTHER_LEVEL_NODE(1, Constants.FIRST_LEVEL_NODE_HTML_FRAGMENT);

	private int deepth;
	private String nodeHtmlFragment;

	TableContentNodeType(int deepth, String nodeHtmlFragment) {
		this.deepth = deepth;
		this.nodeHtmlFragment = nodeHtmlFragment;
	}

	public static TableContentNodeType of(int deepth) {
		if (deepth == 0) {
			return FIRST_LEVEL_NODE;
		}
		return OTHER_LEVEL_NODE;
	}

	public int getDeepth() {
		return deepth;
	}

	public void setDeepth(int deepth) {
		this.deepth = deepth;
	}

	public String getNodeHtmlFragment() {
		return nodeHtmlFragment;
	}

	public void setNodeHtmlFragment(String nodeHtmlFragment) {
		this.nodeHtmlFragment = nodeHtmlFragment;
	}
}
