package com.krish.service;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.krish.entity.PdfDocument;
import com.krish.repo.PdfDocumentRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class PdfDocumentService {
	
	@Autowired
	private S3Client s3Client;
	
	@Autowired
	private PdfDocumentRepository pdfDocumentRepository;
	
	private String bucketName;
	
    public PdfDocumentService () {
    	
    }
	
	public PdfDocumentService(S3Client s3Client, PdfDocumentRepository pdfDocumentRepository,
			@Value("${aws.s3.bucketName}") String bucketName) {
		this.s3Client = s3Client;
		this.pdfDocumentRepository = pdfDocumentRepository;
		this.bucketName = bucketName;
	}
	
	public void generatePdf(HttpServletResponse response) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Hello, World!");
        contentStream.endText();
        contentStream.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=generated.pdf");
        document.save(response.getOutputStream());
        document.close();
    }

	public PdfDocument uploadPdfDocument(MultipartFile file) throws IOException {
		String fileName = UUID.randomUUID().toString();
		String key = fileName + ".pdf";

		s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(),
				RequestBody.fromBytes(file.getBytes()));

		String url = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toExternalForm();

		PdfDocument pdfDocument = new PdfDocument();
		pdfDocument.setName(file.getOriginalFilename());
		pdfDocument.setUrl(url);
		
		 return pdfDocumentRepository.save(pdfDocument);
		 
	}
}
