package com.finflux.bulkoperations;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.sun.jersey.multipart.FormDataMultiPart;

public interface BulkCollectionWritePlatformService {

    Long createBulkTransactionStatement(final FormDataMultiPart formParams) throws InvalidFormatException, IOException;

}
