/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package org.apache.fineract.accounting.glaccount.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.accounting.glaccount.domain.GLClassificationType;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class GlAccountEnumerations {

    public static EnumOptionData glClassificationType(final int id) {
        return glClassificationType(GLClassificationType.fromInt(id));
    }

    public static EnumOptionData glClassificationType(final GLClassificationType type) {
        final String codePrefix = "glClassificationType.";
        EnumOptionData optionData = null;
        switch (type) {
            case Cash:
                optionData = new EnumOptionData(GLClassificationType.Cash.getValue().longValue(),
                        codePrefix + GLClassificationType.Cash.getCode(), "Cash");
            break;

            case Bank:
                optionData = new EnumOptionData(GLClassificationType.Bank.getValue().longValue(),
                        codePrefix + GLClassificationType.Bank.getCode(), "Bank");
            break;

            case OtherJV:
                optionData = new EnumOptionData(GLClassificationType.OtherJV.getValue().longValue(),
                        codePrefix + GLClassificationType.OtherJV.getCode(), "Other JV");
            break;
        }
        return optionData;
    }

    public static EnumOptionData GlClassificationType(final GLClassificationType glClassificationType) {
        final EnumOptionData optionData = new EnumOptionData(glClassificationType.getValue().longValue(), glClassificationType.getCode(),
                glClassificationType.toString());
        return optionData;
    }

    public static List<EnumOptionData> glClassificationType(final GLClassificationType[] glClassificationTypes) {
        final List<EnumOptionData> optionDatas = new ArrayList<>();
        for (final GLClassificationType glClassificationType : glClassificationTypes) {
            optionDatas.add(glClassificationType(glClassificationType));
        }
        return optionDatas;
    }

}
