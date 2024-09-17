package com.yida.epub.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yida
 * @package com.yida.epub.utils
 * @date 2024-09-17 09:13
 * @description PDF操作工具类
 */
public class PDFUtils {
	private static ExecutorService pdf2ImageThreadPool = new ThreadPoolExecutor(2, 8,
			60L, TimeUnit.SECONDS,
			new ArrayBlockingQueue(1024));

	private static ExecutorService image2HTMLThreadPool = new ThreadPoolExecutor(2, 8,
			60L, TimeUnit.SECONDS,
			new ArrayBlockingQueue(1024));

	public static void main(String[] args) throws IOException {
		String pdfFilePath = "/Users/yida/Downloads/test/考研英语-基础词+生僻词.陈正康.pdf";
		String pdfFileName = FileUtils.getFileNameWithoutSuffix(pdfFilePath);
		boolean isDoubleLayer = PDFUtils.isPDFDoubleLayer(pdfFilePath);
		System.out.println("PDF is double layer: " + isDoubleLayer);

		float dpi = 500f;
		String outputPath = "/Users/yida/Downloads/test/";
		String imageOutputPath = outputPath + pdfFileName + "/images";
		PDFUtils.convert2Image(pdfFilePath, imageOutputPath, dpi);
	}

	/**
	 * @param pdfFilePath
	 * @return boolean
	 * @description 判断是否为双层PDF
	 * @author yida
	 * @date 2024-09-17 09:19:00
	 */
	public static boolean isPDFDoubleLayer(String pdfFilePath) throws IOException {
		try (PDDocument document = Loader.loadPDF(new File(pdfFilePath))) {
			Set<PDPage> pagesWithAnnotations = new HashSet<>();
			for (PDPage page : document.getPages()) {
				if (page.getAnnotations().size() > 0) {
					pagesWithAnnotations.add(page);
				}
			}
			return pagesWithAnnotations.size() > 1;
		}
	}

	public static void convert2Image(String pdfFilePath, String imageOutputPath, float dpi) {
		imageOutputPath = StringUtils.replaceBackSlash(imageOutputPath);
		if (!imageOutputPath.endsWith("/")) {
			imageOutputPath = imageOutputPath + "/";
		}
		File imageFileOutputDir = new File(imageOutputPath);
		if (!imageFileOutputDir.exists()) {
			imageFileOutputDir.mkdirs();
		}
		String finalImageOutputPath = imageOutputPath;
		PDDocument pdDocument = null;
		List<CompletableFuture> completableFutureList = new ArrayList<>();
		try {
			pdDocument = Loader.loadPDF(new File(pdfFilePath));
			PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
			for (int pageIndex = 0; pageIndex < pdDocument.getNumberOfPages(); pageIndex++) {
				int finalPageIndex = pageIndex;
				CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
					try {
						BufferedImage image = pdfRenderer.renderImageWithDPI(finalPageIndex, dpi);
						String pageNumber = StringUtils.leftPad(finalPageIndex + 1, 4);
						String outputImagePath = finalImageOutputPath + pageNumber + ".png";
						ImageIO.write(image, "PNG", new File(outputImagePath));
						// 提交Image转HTML任务
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, pdf2ImageThreadPool);
				completableFutureList.add(completableFuture);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != completableFutureList && completableFutureList.size() > 0) {
			CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
		}
		try {
			if (null != pdDocument) {
				pdDocument.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
