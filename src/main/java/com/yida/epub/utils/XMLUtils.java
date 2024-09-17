package com.yida.epub.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @author yida
 * @package com.yida.epub.utils
 * @date 2024-09-18 06:19
 * @description Type your description over here.
 */
public class XMLUtils {
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		String xmlFileClassPath = "test.xml";
		InputStream inputStream = FileUtils.getInputStreamFromClasspath(xmlFileClassPath);
		Document document = reader.read(inputStream);
		String dataString = document.asXML();
		dataString = XMLUtils.write(dataString, "/a/b/c[1]/d[1]/e[1]/f[1]/@name", "yida");
		dataString = XMLUtils.writeValue(dataString, "/a/b/c[1]/d[1]/e[1]/f[1]/", "www.imyida.com");
		// 获得上面赋值的那个节点
		Element element = XMLUtils.getElement(dataString, "/a/b/c[1]/d[1]/e[1]/f[1]/");
		// 获得该节点内容
		System.out.println(element.attributeValue("name") + " ---> " + element.getText());
		// 复制一个节点，上面已经赋值，内容将全部拷贝
		Element copy = element.createCopy(QName.get("f", element.getParent().getNamespace()));
		dataString = XMLUtils.addElement(dataString, "/a/b/c[1]/d[1]/e[1]/", copy);
		// 删除一个上面复制的那个节点
		dataString = XMLUtils.removeElement(dataString, "/a/b/c[1]/d[1]/e[1]/f[1]/");
		System.out.println(dataString);
	}

	/**
	 * 写数据到指定节点
	 *
	 * @param xml      xml文本
	 * @param nodePath 节点路径
	 * @param value    节点值
	 */
	public static String write(String xml, String nodePath, String value) throws DocumentException {
		if (!nodePath.contains("/@")) {
			throw new IllegalArgumentException(nodePath + "，节点错误，没有/@");
		}
		Document document1 = DocumentHelper.parseText(xml);
		Element rootElement = document1.getRootElement();
		String[] path = nodePath.split("/@");
		String attribute = path[1];
		nodePath = path[0];
		String[] split = nodePath.split("/");
		for (String s : split) {
			if (isEmpty(s)) continue;
			try {
				if (s.contains("[") && s.contains("]")) {
					//多节点下标
					int index = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
					//节点名称
					s = s.substring(0, s.indexOf("["));
					rootElement = (Element) rootElement.elements(s).get(index);
				} else {
					rootElement = rootElement.element(s);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(nodePath + "，节点不存在");
			}
		}
		if (null == rootElement) {
			throw new IllegalArgumentException(nodePath + "，节点不存在");
		}
		if (rootElement.attribute(attribute) == null) {
			throw new IllegalArgumentException(nodePath + "，节点" + attribute + "属性不存在");
		}
		rootElement.attribute(attribute).setValue(isNull(value));
		return document1.asXML();
	}

	/**
	 * 写数据到指定节点
	 *
	 * @param xml      xml文本
	 * @param nodePath 节点路径
	 * @param value    节点值
	 */
	public static String writeValue(String xml, String nodePath, Object value) throws DocumentException {
		if (nodePath.contains("/@")) {
			throw new IllegalArgumentException(nodePath + "，节点错误，不能有/@");
		}
		Document document1 = DocumentHelper.parseText(xml);
		Element rootElement = document1.getRootElement();
		String[] split = nodePath.split("/");
		for (String s : split) {
			if (isEmpty(s)) continue;
			try {
				if (s.contains("[") && s.contains("]")) {
					//多节点下标
					int index = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
					//节点名称
					s = s.substring(0, s.indexOf("["));
					rootElement = (Element) rootElement.elements(s).get(index);
				} else {
					rootElement = rootElement.element(s);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(nodePath + "，节点不存在");
			}
		}
		if (null == rootElement) {
			throw new IllegalArgumentException(nodePath + "，节点不存在");
		}
		rootElement.setText(isNull(value));
		return document1.asXML();
	}

	/**
	 * 获取指定节点
	 *
	 * @param xml      xml文本
	 * @param nodePath 节点路径
	 */
	public static Element getElement(String xml, String nodePath) {
		try {
			Document document1 = DocumentHelper.parseText(xml);
			Element rootElement = document1.getRootElement();
			String[] split = nodePath.split("/");
			for (String s : split) {
				if (isEmpty(s)) continue;
				try {
					if (s.contains("[") && s.contains("]")) {
						//多节点下标
						int index = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
						//节点名称
						s = s.substring(0, s.indexOf("["));
						rootElement = (Element) rootElement.elements(s).get(index);
					} else {
						rootElement = rootElement.element(s);
					}
				} catch (Exception e) {
					throw new IllegalArgumentException(nodePath + "，节点不存在");
				}
			}
			if (null == rootElement) {
				throw new IllegalArgumentException(nodePath + "，节点不存在");
			}
			return rootElement;
		} catch (DocumentException e) {
			throw new IllegalArgumentException(nodePath + "，节点不存在");
		}
	}

	/**
	 * 添加新节点到指定节点下
	 *
	 * @param xml      xml文本
	 * @param nodePath 节点路径
	 * @param element  节点值
	 */
	public static String addElement(String xml, String nodePath, Element element) throws DocumentException {
		Document document1 = DocumentHelper.parseText(xml);
		Element rootElement = document1.getRootElement();
		String[] split = nodePath.split("/");
		for (String s : split) {
			if (nodePath.equals("/")) break;
			if (isEmpty(s)) continue;
			try {
				if (s.contains("[") && s.contains("]")) {
					//多节点下标
					int index = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
					//节点名称
					s = s.substring(0, s.indexOf("["));
					rootElement = (Element) rootElement.elements(s).get(index);
				} else {
					rootElement = rootElement.element(s);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(nodePath + "，节点不存在");
			}
		}
		if (null == rootElement) {
			throw new IllegalArgumentException(nodePath + "，节点不存在");
		}
		// 不存在节点，添加新的节点
		rootElement.add(element);
		return document1.asXML();
	}


	/**
	 * 删除指定节点
	 *
	 * @param xml      xml文本
	 * @param nodePath 节点路径
	 */
	public static String removeElement(String xml, String nodePath) throws DocumentException {
		Document document1 = DocumentHelper.parseText(xml);
		Element rootElement = document1.getRootElement();
		String[] split = nodePath.split("/");
		Element element = rootElement;
		for (String s : split) {
			if (isEmpty(s)) continue;
			try {
				if (s.contains("[") && s.contains("]")) {
					//多节点下标
					int index = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
					//节点名称
					s = s.substring(0, s.indexOf("["));
					element = (Element) element.elements(s).get(index);
				} else {
					element = element.element(s);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		if (element != rootElement) {
			// 删除节点
			element.getParent().remove(element);
		}
		return document1.asXML();
	}

	public static boolean isEmpty(String str) {
		if (str != null) {
			int len = str.length();
			for (int x = 0; x < len; ++x) {
				if (str.charAt(x) > ' ') {
					return false;
				}
			}
		}
		return true;
	}

	public static String isNull(Object value) {
		if (null == value) {
			return "";
		}
		return value.toString().trim();
	}
}
