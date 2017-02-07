/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.portfolio.deduplication.service;

import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.deduplication.data.DeduplicationData;

import java.util.Collection;
import java.util.Map;

public interface DeDuplicationService {
	
	void duplicationCheck(Map<String, Object> data, String table, boolean creation);

	Collection<ClientData> getDuplicationMatches(long clientId);

	Collection<DeduplicationData> getDedupWeightages();
}
