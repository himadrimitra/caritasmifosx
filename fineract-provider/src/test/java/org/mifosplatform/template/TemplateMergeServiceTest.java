/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mifosplatform.template;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.template.domain.Template;
import org.apache.fineract.template.service.TemplateMergeService;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mifosplatform.portfolio.loanaccount.LoanScheduleTestDataHelper;
import org.mifosplatform.portfolio.loanaccount.MonetaryCurrencyBuilder;

public class TemplateMergeServiceTest {

    private Template template;
    private final static String TEST_FILE = "src/test/resources/template.mustache";
    private static TemplateMergeService tms;

    @BeforeClass
    public static void init() {
        tms = new TemplateMergeService(
//                new FromJsonHelper()
                );
    }

    @Ignore
    @Test
    public void compileHelloTemplate() throws Exception {
        final String name = "TemplateName";
        final String text = "Hello Test for Template {{template.name}}!";

        this.template = new Template(name, text, null, null, null);

        final HashMap<String, Object> scopes = new HashMap<>();
        scopes.put("template", this.template);

        String output = "";
        output = tms.compile(this.template, scopes);
        assertEquals("Hello Test for Template TemplateName!", output);
    }

    @Ignore
    @Test
    public void compileLoanSummary() throws IOException {

        final LocalDate july2nd = new LocalDate(2012, 7, 2);
        final MonetaryCurrency usDollars = new MonetaryCurrencyBuilder().withCode("USD").withDigitsAfterDecimal(2).build();
        final List<LoanRepaymentScheduleInstallment> installments = LoanScheduleTestDataHelper.createSimpleLoanSchedule(july2nd, usDollars);

        final File file = new File(TEST_FILE);
        final DataInputStream dis = new DataInputStream(new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        dis.readFully(bytes);
        final String content = new String(bytes, "UTF-8");

        this.template = new Template("TemplateName", content, null, null, null);

        final HashMap<String, Object> scopes = new HashMap<>();
        scopes.put("installments", installments);

        final String output = tms.compile(this.template, scopes);

        dis.close();

        System.out.println(output);
        dis.close();
    }

}
