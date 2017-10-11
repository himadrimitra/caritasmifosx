package org.apache.fineract.infrastructure.documentmanagement.domain;

import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentRepositoryWrapper {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentRepositoryWrapper(final DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document findOneWithNotFoundDetection(final String entityType, final Long entityId, final Long documentId) {
        Document document = this.documentRepository.findOne(documentId);
        if (document == null) throw new DocumentNotFoundException(entityType, entityId, documentId);
        return document;
    }

    public Document save(final Document document) {
        return this.documentRepository.save(document);
    }

    public Document saveAndFlush(final Document document) {
        return this.documentRepository.saveAndFlush(document);
    }

    public void delete(final Document document) {
        this.documentRepository.delete(document);
    }
    
    public Document findDocumentByReportIdentifier(final String entityType, final Long entityId, final Long reportIdentifier) {
        Document document = this.documentRepository.findDocumentByReportIdentifier(reportIdentifier);
        if (document == null) throw new DocumentNotFoundException(entityType, entityId, reportIdentifier);
        return document;
    }
}
