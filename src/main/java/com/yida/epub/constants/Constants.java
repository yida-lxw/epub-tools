package com.yida.epub.constants;

/**
 * @author yida
 * @package com.yida.epub.constants
 * @date 2024-09-16 19:58
 * @description 常量类
 */
public class Constants {
	//正斜杠
	public static final String FORWARD_SLASH = "/";
	//反斜杠
	public static final String BACK_SLASH = "\\";
	//反斜杠(用于Java正则表达式中)
	public static final String BACK_SLASH_IN_REGEX = "\\\\";

	public static final String FIRST_LEVEL_NODE_HTML_FRAGMENT = "<p class=\"calibre_8\">\n" +
			"      <a href=\"index_split_${pageNumber}.html#${anchorId}\">${tableContentNodeName}</a>\n" +
			"    </p>";

	public static final String OTHER_LEVEL_NODE_HTML_FRAGMENT = "<blockquote class=\"calibre_9\">\n" +
			"      <blockquote class=\"calibre_10\">\n" +
			"        <a href=\"index_split_${pageNumber}.html#${anchorId}\">${tableContentNodeName}</a>\n" +
			"      </blockquote>\n" +
			"    </blockquote>";

	//当前页每行文字内容显示模板
	public static final String LINE_CONTENT_HTML_FRAGMENT = "<p class=\"calibre_5\">${lineContent}</p>";

	//当前页章节节点显示模板
	public static final String CHAPTER_NODE_HTML_FRAGMENT = "<p id=\"p_${pageNumber}\" class=\"calibre_3\">\n" +
			"\t<span class=\"calibre3\">\n" +
			"\t\t<span class=\"bold\">${chapterNodeName}</span>\n" +
			"\t</span>\n" +
			"  </p>";

	public static final String IMAGE_MANIFEST_HTML_FRAGMENT = "<item href=\"images/${imageName}\" id=\"${imageId}\" media-type=\"image/jpeg\"/>";

	public static final String HTML_PAGE_MANIFEST_HTML_FRAGMENT = "<item href=\"index_split_${pageNumber}.html\" id=\"id_${pageNumber}\" media-type=\"application/xhtml+xml\"/>";

	public static final String HTML_PAGE_ITEMREF_HTML_FRAGMENT = "<itemref idref=\"id_${pageNumber}\"/>";

	public static final String START_PAGE_GUID_HTML_FRAGMENT = "<reference href=\"index_split_${pageNumber}.html#${anchorId}\" title=\"start\" type=\"text\"/>";

	public static final String NAV_POINT_HTML_FRAGMENT = "<navPoint class=\"chapter\" id=\"num_${pageNumber}\" playOrder=\"${pageNumber}\">\n" +
			"      <navLabel>\n" +
			"        <text>${tableContentNodeName}</text>\n" +
			"      </navLabel>\n" +
			"      <content src=\"index_split_${pageNumber}.html\"/>\n" +
			"    </navPoint>";
}
