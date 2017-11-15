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
package org.apache.fineract.portfolio.group.domain;

import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.group.exception.GroupNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Wrapper for {@link GroupRepository} that adds NULL checking and Error
 * handling capabilities
 * </p>
 */
@Service
public class GroupRepositoryWrapper {

    private final GroupRepository repository;

    @Autowired
    public GroupRepositoryWrapper(final GroupRepository repository) {
        this.repository = repository;
    }

    public Group findOneWithNotFoundDetection(final Long id) {
        return findOneWithNotFoundDetection(id, false);
    }

    public Group findOneWithNotFoundDetection(final Long id, final boolean loadLazyEntities) {
        final Group entity = this.repository.findOne(id);
        if (entity == null) { throw new GroupNotFoundException(id); }
        if (loadLazyEntities) {
            Hibernate.initialize(entity.getClientMembers());
            Hibernate.initialize(entity.getGroupMembers());
            Hibernate.initialize(entity.getStaffHistory());
            Hibernate.initialize(entity.getParent());
            Hibernate.initialize(entity.getGroupRoles());
        }
        return entity;
    }

    public Group findByOfficeWithNotFoundDetection(final Long id, final Office office) {
        final Group group = findOneWithNotFoundDetection(id, true);
        if (group.getOffice().getId() != office.getId()) { throw new GroupNotFoundException(id); }
        return group;
    }

    public void save(final Group entity) {
        this.repository.save(entity);
    }

    public void saveAndFlush(final Group entity) {
        this.repository.saveAndFlush(entity);
    }

    public void delete(final Group entity) {
        this.repository.delete(entity);
    }
}