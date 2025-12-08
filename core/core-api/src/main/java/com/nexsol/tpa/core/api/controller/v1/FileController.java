package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.FileResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.DocumentFile;
import com.nexsol.tpa.core.domain.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/file")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@PostMapping("/insurance")
	public ApiResponse<FileResponse> uploadInsurance(@RequestPart("file") MultipartFile file) throws IOException {

		DocumentFile uploadedFile = fileService.uploadInsurance(file.getInputStream(), file.getOriginalFilename(),
				file.getSize(), file.getContentType());

		return ApiResponse.success(FileResponse.of(uploadedFile));
	}

	@PostMapping("/signature")
	public ApiResponse<FileResponse> uploadSignature(@RequestPart("file") MultipartFile file) throws IOException {

		DocumentFile uploadedFile = fileService.uploadSignature(file.getInputStream(), file.getOriginalFilename(),
				file.getSize(), file.getContentType());

		return ApiResponse.success(FileResponse.of(uploadedFile));
	}

	@DeleteMapping
	public ApiResponse<Object> deleteFile(@RequestParam("key") String key) {

		fileService.deleteFile(key);

		return ApiResponse.success();
	}

}
