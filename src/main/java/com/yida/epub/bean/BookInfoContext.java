package com.yida.epub.bean;

/**
 * @author yida
 * @package com.yida.epub.bean
 * @date 2025-12-27 11:33
 * @description 书籍信息上下文
 */
public class BookInfoContext {
	/**
	 * 程序工作根目录
	 */
	private String basePath;
	/**
	 * 书籍名称
	 */
	private String bookName;
	/**
	 * 作者
	 */
	private String author;
	/**
	 * 出版社
	 */
	private String publisher;
	/**
	 * 出版日期(yyyy-MM-dd格式)
	 */
	private String publishDate;

	/**
	 * 书籍总页数
	 */
	private String pageCount;

	/**
	 * 书籍定价
	 */
	private String price;

	/**
	 * 书籍ISBN号
	 */
	private String isbn;
	/**
	 * 是否存在封面页
	 */
	private boolean existsCoverPage;

	/**
	 * 是否存在版权页
	 */
	private boolean existsCopyRightPage;

	/**
	 * 电子书html页面相对路径
	 */
	private String htmlPageRelativePath;

	/**
	 * 封面页相对路径
	 */
	private String coverPageRelativePath;

	/**
	 * 版权页相对路径
	 */
	private String copyRightPageRelativePath;

	/**
	 * 目录页相对路径
	 */
	private String tocPageRelativePath;

	/**
	 * opf文件的相对路径
	 */
	private String opfFileRelativePath;

	/**
	 * 封面图片文件的相对路径
	 */
	private String coverImageFileRelativePath;

	/**
	 * CSS文件的相对路径
	 */
	private String cssFileRelativePath;

	/**
	 * 其他页面上的图片文件的相对路径
	 */
	private String otherImageFileRelativePath;

	/**
	 * 封面图下载链接
	 */
	private String coverImageDownloadUrl;

	/**
	 * 页面额外CSS文件下载链接
	 */
	private String[] cssFileDownloadUrl;

	/**
	 * 书籍目录内容抓取链接(根据ISBN从豆瓣图书抓取)
	 */
	private String contentsCrawlUrl;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean isExistsCoverPage() {
		return existsCoverPage;
	}

	public void setExistsCoverPage(boolean existsCoverPage) {
		this.existsCoverPage = existsCoverPage;
	}

	public boolean isExistsCopyRightPage() {
		return existsCopyRightPage;
	}

	public void setExistsCopyRightPage(boolean existsCopyRightPage) {
		this.existsCopyRightPage = existsCopyRightPage;
	}

	public String getHtmlPageRelativePath() {
		return htmlPageRelativePath;
	}

	public void setHtmlPageRelativePath(String htmlPageRelativePath) {
		this.htmlPageRelativePath = htmlPageRelativePath;
	}

	public String getCoverPageRelativePath() {
		return coverPageRelativePath;
	}

	public void setCoverPageRelativePath(String coverPageRelativePath) {
		this.coverPageRelativePath = coverPageRelativePath;
	}

	public String getCopyRightPageRelativePath() {
		return copyRightPageRelativePath;
	}

	public void setCopyRightPageRelativePath(String copyRightPageRelativePath) {
		this.copyRightPageRelativePath = copyRightPageRelativePath;
	}

	public String getTocPageRelativePath() {
		return tocPageRelativePath;
	}

	public void setTocPageRelativePath(String tocPageRelativePath) {
		this.tocPageRelativePath = tocPageRelativePath;
	}

	public String getOpfFileRelativePath() {
		return opfFileRelativePath;
	}

	public void setOpfFileRelativePath(String opfFileRelativePath) {
		this.opfFileRelativePath = opfFileRelativePath;
	}

	public String getCoverImageFileRelativePath() {
		return coverImageFileRelativePath;
	}

	public void setCoverImageFileRelativePath(String coverImageFileRelativePath) {
		this.coverImageFileRelativePath = coverImageFileRelativePath;
	}

	public String getCssFileRelativePath() {
		return cssFileRelativePath;
	}

	public void setCssFileRelativePath(String cssFileRelativePath) {
		this.cssFileRelativePath = cssFileRelativePath;
	}

	public String getOtherImageFileRelativePath() {
		return otherImageFileRelativePath;
	}

	public void setOtherImageFileRelativePath(String otherImageFileRelativePath) {
		this.otherImageFileRelativePath = otherImageFileRelativePath;
	}

	public String getCoverImageDownloadUrl() {
		return coverImageDownloadUrl;
	}

	public void setCoverImageDownloadUrl(String coverImageDownloadUrl) {
		this.coverImageDownloadUrl = coverImageDownloadUrl;
	}

	public String[] getCssFileDownloadUrl() {
		return cssFileDownloadUrl;
	}

	public void setCssFileDownloadUrl(String[] cssFileDownloadUrl) {
		this.cssFileDownloadUrl = cssFileDownloadUrl;
	}

	public String getContentsCrawlUrl() {
		return contentsCrawlUrl;
	}

	public void setContentsCrawlUrl(String contentsCrawlUrl) {
		this.contentsCrawlUrl = contentsCrawlUrl;
	}
}
