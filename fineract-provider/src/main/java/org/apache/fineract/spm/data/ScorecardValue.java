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
package org.apache.fineract.spm.data;

public class ScorecardValue {

    private Long id;
    private Long questionId;
    private Long responseId;
    private Integer value;
    private String questionName;
    private String answerName;

    public String getAnswerName() {
        return this.answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    public String getQuestionName() {
        return this.questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public ScorecardValue() {
        super();
    }

    public ScorecardValue(final Long questionId, final Long responseId, final Integer value, final String questionName,
            final String answerName) {
        super();
        this.questionId = questionId;
        this.responseId = responseId;
        this.value = value;
        this.questionName = questionName;
        this.answerName = answerName;
    }

    public ScorecardValue(final Long id, final Long questionId, final Long responseId, final Integer value) {
        this.id = id;
        this.questionId = questionId;
        this.responseId = responseId;
        this.value = value;
    }
    
    public ScorecardValue(final Long id, final Long questionId, final String questionName, final Long responseId, final String answerName,
            final Integer value) {
        this.id = id;
        this.questionId = questionId;
        this.questionName = questionName;
        this.responseId = responseId;
        this.answerName = answerName;
        this.value = value;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
