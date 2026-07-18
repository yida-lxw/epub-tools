package com.yida.epub.builder;

/**
 * @author yida
 * @package com.yida.epub.builder
 * @date 2024-09-17 16:11
 * @description 书籍目录页内容构建器
 */
public class TableContentPageBuilder {
	private TableContentPageBuilder() {
	}

	private static class SingletonHolder {
		private static final TableContentPageBuilder INSTANCE = new TableContentPageBuilder();
	}

	public static TableContentPageBuilder getInstance() {
		return TableContentPageBuilder.SingletonHolder.INSTANCE;
	}

	public void buildTableContent() {

	}
}
