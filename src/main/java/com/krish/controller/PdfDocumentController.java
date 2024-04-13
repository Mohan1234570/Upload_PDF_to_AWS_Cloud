package com.krish.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.krish.entity.PdfDocument;
import com.krish.service.PdfDocumentService;

@RestController
public class PdfDocumentController {
	
	@Autowired
	private PdfDocumentService pdfDocumentService;

	public PdfDocumentController(PdfDocumentService pdfDocumentService) {
		this.pdfDocumentService = pdfDocumentService;
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadPdfDocument(@RequestParam("file") MultipartFile file) {
		try {
			PdfDocument pdfDocument = pdfDocumentService.uploadPdfDocument(file);
			return ResponseEntity.ok(pdfDocument.getUrl());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the PDF document.");
		}
	}
}