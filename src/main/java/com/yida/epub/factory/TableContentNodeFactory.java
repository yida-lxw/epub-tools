package com.yida.epub.factory;

import com.yida.epub.bean.TableContentNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package com.yida.epub.factory
 * @date 2024-09-16 20:38
 * @description Type your description over here.
 */
public class TableContentNodeFactory {
	private TableContentNodeFactory() {
	}

	private static class SingletonHolder {
		private static final TableContentNodeFactory INSTANCE = new TableContentNodeFactory();
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

	public static TableContentNodeFactory getInstance() {
		return SingletonHolder.INSTANCE;
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
