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
package org.apache.fineract.infrastructure.codes.data;

import java.io.Serializable;

/**
 * Immutable data object represent code-value data in system.
 */
public class CodeValueData implements Serializable {

    private final Long id;

    private final String name;

    @SuppressWarnings("unused")
    private final Integer position;

    @SuppressWarnings("unused")
    private final String description;
    
    private final Boolean isActive;
    
    @SuppressWarnings("unused")
    private final Integer codeScore;
    
    private final Boolean mandatory;
	
    @SuppressWarnings("unused")
    private final Long parentId;

    public static CodeValueData instance(final Long id, final String name, final Integer position, final Boolean isActive,
            final Integer codeScore, final boolean mandatory) {
        String description = null;
        Long parentId = null;
        return new CodeValueData(id, name, position, description, isActive, codeScore, mandatory, parentId);
    }

    public static CodeValueData instance(final Long id, final String name, final String description, 
            final boolean isActive, final boolean mandatory) {
        Integer position = null;
        final Integer codeScore = null;
        final Long parentId = null;
        return new CodeValueData(id, name, position, description,isActive, codeScore, mandatory, parentId);
    }
    
    public static CodeValueData instance(final Long id, final String name, final String description, 
            final boolean isActive) {
        Integer position = null;
        boolean mandatory = false;
        final Integer codeScore = null;
        final Long parentId = null;
        return new CodeValueData(id, name, position, description, isActive,codeScore, mandatory, parentId);
    }

    public static CodeValueData instance(final Long id, final String name) {
        String description = null;
        Integer position = null;
        boolean isActive = false;
        final Integer codeScore = null;
        boolean mandatory = false;
	final Long parentId = null;
        return new CodeValueData(id, name, position, description, isActive, codeScore, mandatory, parentId);
    }

    public static CodeValueData instance(final Long id, final String name, final Integer position, final String description,
            final boolean isActive, final Integer codeScore, final boolean mandatory, final Long parentId) {
        return new CodeValueData(id, name, position, description, isActive, codeScore, mandatory, parentId);
    }
    
    public static CodeValueData instanceIdAndName(final Long id, final String name) {
        String description = null;
        Integer position = null;
        Boolean isActive = null;
        final Long parentId = null;
        return new CodeValueData(id, name, position, description, isActive, parentId);
    }
    
    public static CodeValueData instance(final Long id, final String name, final Integer position, final String description, final boolean isActive, final Long parentId) {
        return new CodeValueData(id, name, position, description, isActive, parentId);
    }

    public static CodeValueData instance(final Long id, final String name, final boolean isActive) {
        String description = null;
        Integer position = null;
        final Integer codeScore = null;
        boolean mandatory = false;
        final Long parentId = null;
        return new CodeValueData(id, name, position, description, isActive, codeScore , mandatory, parentId);
    }

    private CodeValueData(final Long id, final String name, final Integer position, final String description, final Boolean isActive,
            final Integer codeScore, final boolean mandatory, final Long parentId) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.description = description;
        this.isActive = isActive;
        this.codeScore = codeScore;
        this.mandatory = mandatory;
        this.parentId = parentId;
    }

    public CodeValueData(final Long id, final String name, final Integer position, final String description, final Boolean isActive,
            final Long parentId) {
        final Integer codeScore = null;
        final Boolean mandatory = null;
        this.id = id;
        this.name = name;
        this.position = position;
        this.description = description;
        this.isActive = isActive;
        this.codeScore = codeScore;
        this.mandatory = mandatory;
        this.parentId = parentId;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    
    /**
     * @return the mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return isActive;
    }
}