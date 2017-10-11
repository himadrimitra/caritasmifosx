package com.finflux.pdcm.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.api.JsonQuery;

import com.finflux.pdcm.data.PostDatedChequeDetailData;
import com.finflux.pdcm.data.PostDatedChequeDetailSearchTemplateData;
import com.finflux.pdcm.data.PostDatedChequeDetailTemplateData;

public interface PostDatedChequeDetailReadPlatformService {

    PostDatedChequeDetailTemplateData template(final Integer entityTypeId, final Long entityId);

    Collection<PostDatedChequeDetailData> retrieveAll(final Integer entityTypeId, final Long entityId);

    PostDatedChequeDetailData retrieveOne(final Long pdcId);

    PostDatedChequeDetailSearchTemplateData searchTemplate();

    Collection<PostDatedChequeDetailData> searchPDC(final JsonQuery query);
}