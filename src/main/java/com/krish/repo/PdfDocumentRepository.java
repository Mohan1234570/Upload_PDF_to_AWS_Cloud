package com.krish.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krish.entity.PdfDocument;

public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
	
}