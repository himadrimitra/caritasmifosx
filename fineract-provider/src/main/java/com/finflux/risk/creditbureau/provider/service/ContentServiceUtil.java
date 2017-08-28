package com.finflux.risk.creditbureau.provider.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.documentmanagement.data.DocumentData;
import org.apache.fineract.infrastructure.documentmanagement.data.FileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentServiceUtil {

    private final static Logger logger = LoggerFactory.getLogger(ContentServiceUtil.class);
    private final ContentRepositoryFactory contentRepositoryFactory;

    @Autowired
    public ContentServiceUtil(final ContentRepositoryFactory contentRepositoryFactory) {
        this.contentRepositoryFactory = contentRepositoryFactory;
    }

    public final String getContent(final String location) {
        String data = null;
        InputStream inputStream = null;
        if (StringUtils.isNotEmpty(location)) {
            final FileData fileData = this.contentRepositoryFactory.getRepository().fetchFile(new DocumentData(location));
            try {
                inputStream = fileData.file();
                byte[] receivedData = IOUtils.toByteArray(inputStream);
                data = new String(receivedData);
            } catch (IOException e) {
                logger.error("Error while opining the file:" + location);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error("Error while closing the file:" + location);
                    }
                }
            }
        }
        return data;
    }
}
