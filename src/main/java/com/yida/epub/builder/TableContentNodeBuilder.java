package com.yida.epub.builder;

import com.yida.epub.bean.TableContentNode;
import com.yida.epub.constants.Constants;
import com.yida.epub.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package com.yida.epub.builder
 * @date 2024-09-16 20:38
 * @description Type your description over here.
 */
public class TableContentNodeBuilder {
	private TableContentNodeBuilder() {
	}

	private static class SingletonHolder {
		private static final TableContentNodeBuilder INSTANCE = new TableContentNodeBuilder();
	}

	public static TableContentNodeBuilder getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * @return String
	 * @description 构建生成目录节点的HTML内容
	 * @author yida
	 * @date 2024-09-16 20:46:38
	 */
	public String buildTableContent() {
		// TODO 尚未实现
		return null;
	}

	/**
	 * @param tableContentNode
	 * @return String
	 * @description 构建一级目录节点代码片段
	 * @author yida
	 * @date 2024-09-17 19:49:20
	 */
	public String buildFirstLevelNode(TableContentNode tableContentNode) {
		int pageNumber = tableContentNode.getPageNumber();
		String anchorId = tableContentNode.getAnchorId();
		String tableContentNodeName = tableContentNode.getTableContentNodeName();
		String firstLevelNodeTempalte = Constants.FIRST_LEVEL_NODE_HTML_FRAGMENT;
		String pageNumberStr = StringUtils.leftPad(pageNumber - 1, 4);
		String nodeContent = firstLevelNodeTempalte.replace("${pageNumber}", pageNumberStr);
		nodeContent = firstLevelNodeTempalte.replace("${tableContentNodeName}", tableContentNodeName);
		if (StringUtils.isEmpty(anchorId)) {
			nodeContent = firstLevelNodeTempalte.replace("#${anchorId}", "");
		} else {
			nodeContent = firstLevelNodeTempalte.replace("${anchorId}", anchorId);
		}
		return nodeContent;
	}

	public String buildNonFirstLevelNode(TableContentNode tableContentNode) {
		return null;
	}

	private Map<String, TableContentNode> tableContentNodeMap = new HashMap<>();

	public void put(String nodeId, TableContentNode tableContentNode) {
		tableContentNodeMap.put(nodeId, tableContentNode);
	}

	public void delete(String nodeId) {
		tableContentNodeMap.remove(nodeId);
	}

	public List<TableContentNode> getChildNodes(String parentNodeId) {
		if (null == tableContentNodeMap || tableContentNodeMap.size() <= 0) {
			return null;
		}
		List<TableContentNode> childNodeList = new ArrayList<>();
		for (Map.Entry<String, TableContentNode> entry : tableContentNodeMap.entrySet()) {
			String nodeId = entry.getKey();
			TableContentNode tableContentNode = entry.getValue();
			String parentId = tableContentNode.getParentId();
			if (!parentId.equals(parentNodeId)) {
				continue;
			}
			childNodeList.add(tableContentNode);
		}
		return childNodeList;
	}
}
