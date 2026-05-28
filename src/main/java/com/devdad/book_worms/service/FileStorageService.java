package com.devdad.book_worms.service;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devdad.book_worms.model.book.Book;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

	@Value("${application.file.upload.photos-output-path}")
	private String fileUploadPath;

	public String saveFile(
			@Nonnull MultipartFile sourceFile,
			@Nonnull String userId) {
		final String fileUploadSubPath = "users" + separator + userId;

		return uploadFile(sourceFile, fileUploadSubPath);
	}

	private String uploadFile(@Nonnull MultipartFile sourceFile,
			@Nonnull String fileUploadSubPath) {

		final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
		File targetFolder = new File(finalUploadPath);

		if (!targetFolder.exists()) {
			boolean folderCreated = targetFolder.mkdirs();
			if (!folderCreated) {
				log.warn("Failed to create the target folder.");
				return null;
			}
		}

		final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());

		if(fileExtension == null) {
			log.warn("Failed to get file extension");
			return null;
		}

		// ./upload/users/1/2322123238.jpg
		String targetFilePath = 
			finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;

		Path targetPath = Paths.get(targetFilePath);
		try {
			Files.write(targetPath, sourceFile.getBytes());
			log.info("File saved to:: " + targetFilePath);

			return targetFilePath;
		} catch (IOException e) {
			log.error("File failed to save:: ", e);
		}

		return null;
	}

	private String getFileExtension(@Nullable String originalFilename) {
		if (originalFilename == null || originalFilename.isEmpty()) {
			return null;
		}

		int lastDotIndex = originalFilename.lastIndexOf(".");
		if(lastDotIndex == -1){
			return null;
		}

		return originalFilename.substring(lastDotIndex + 1).toLowerCase();
	}
}
