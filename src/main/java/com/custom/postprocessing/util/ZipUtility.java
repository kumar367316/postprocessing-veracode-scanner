package com.custom.postprocessing.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.custom.postprocessing.constant.PostProcessingConstant;

public class ZipUtility {

	@Value("${backspace.value}")
	private String backSpaceValue;

	Logger logger = LoggerFactory.getLogger(ZipUtility.class);

	public void zipProcessing(List<String> listFiles, String destZipFile) throws FileNotFoundException, IOException {
		final FileOutputStream fileOutputStream = new FileOutputStream(destZipFile);
		final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		for (String fileName : listFiles) {
			File file = new File(fileName);
			if (file.isDirectory()) {
				zipDirectory(file, file.getName(), zipOutputStream);
			} else {
				zipFile(file, zipOutputStream);
			}
		}
		zipOutputStream.flush();
		zipOutputStream.close();
		fileOutputStream.close();
	}

	public void zipDirectory(File folder, String parentFolder, ZipOutputStream zipOutputStream) {
		try {
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					zipDirectory(file, parentFolder + backSpaceValue + file.getName(), zipOutputStream);
					continue;
				}
				final FileInputStream fileInputStream = new FileInputStream(file);
				zipOutputStream.putNextEntry(new ZipEntry(parentFolder + backSpaceValue + file.getName()));
				final BufferedInputStream inputStreamFile = new BufferedInputStream(fileInputStream);
				long bytesReadFile = 0;
				byte[] bytesInputFile = new byte[PostProcessingConstant.MEMORY_SIZE];
				int readFile = 0;
				while ((readFile = inputStreamFile.read(bytesInputFile)) != -1) {
					zipOutputStream.write(bytesInputFile, 0, readFile);
					bytesReadFile += readFile;
				}
				logger.info("bytesInputFile:" + bytesReadFile);
				fileInputStream.close();
				zipOutputStream.closeEntry();
				inputStreamFile.close();
			}
		} catch (Exception exception) {
			logger.info("exception zipDirectory method:" + exception.getMessage());
		}
	}

	private void zipFile(File file, ZipOutputStream zipOutputStream) throws FileNotFoundException, IOException {
		ZipEntry zipEntry = new ZipEntry(file.getName());
		zipOutputStream.putNextEntry(zipEntry);
		final FileInputStream fileInputStream = new FileInputStream(file);
		final BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
		long bytesReadFile = 0;
		byte[] bytesInFile = new byte[PostProcessingConstant.MEMORY_SIZE];
		int readFileInput = 0;
		while ((readFileInput = inputStream.read(bytesInFile)) != -1) {
			zipOutputStream.write(bytesInFile, 0, readFileInput);
			bytesReadFile += readFileInput;
		}
		logger.info("bytesReadFile:" + bytesReadFile);
		fileInputStream.close();
		inputStream.close();
		zipOutputStream.closeEntry();
		zipOutputStream.close();
	}
}