package com.finflux.entitylock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.finflux.common.constant.CommonConstants;
import com.finflux.common.domain.ActionType;
import com.finflux.common.sqlinjection.ValidateSQLInjection;
import com.finflux.common.util.FinfluxStringUtils;
import com.finflux.entitylock.api.EntityLockApiConstants;
import com.finflux.entitylock.exception.EntityLockedException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class LockOrUnlockEntityServiceImpl implements LockOrUnlockEntityService {

    private final JdbcTemplate jdbcTemplate;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LockOrUnlockEntityServiceImpl(final RoutingDataSource dataSource, final FromJsonHelper fromApiJsonHelper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    @Transactional
    public void lockEntity(final Long entityId, final EntityType entityType, final String businessEventName) {
        final ActionType actionType = ActionType.LOCK;
        final String conditionJsonString = getLockOrUnlockConfigVaues(businessEventName, entityType);
        processForLockOrUnlockEntity(entityId, actionType, conditionJsonString);
    }

    private void processForLockOrUnlockEntity(final Long entityId, final ActionType actionType, final String conditionJsonString) {
        if (StringUtils.isNotEmpty(conditionJsonString)) {
            final JsonElement element = this.fromApiJsonHelper.parse(conditionJsonString);
            final JsonArray jsonArray = element.getAsJsonArray();
            if (jsonArray != null && jsonArray.size() > 0) {
                final List<String> sqlStatements = new ArrayList<>();
                final HashMap<String, Object> params = new HashMap<>();
                params.put(CommonConstants.entityIdParam, entityId);
                for (int i = 0; i < jsonArray.size(); i++) {
                    final JsonElement elem = jsonArray.get(i).getAsJsonObject();
                    final String tableName = this.fromApiJsonHelper.extractStringNamed(EntityLockApiConstants.TABLE_NAME, elem);
                    final Long entityTypeId = this.fromApiJsonHelper.extractLongNamed(CommonConstants.entityTypeParam, elem);
                    final EntityType entityType = EntityType.fromInt(entityTypeId.intValue());
                    String sqlJoinCondition = null;
                    if (this.fromApiJsonHelper.parameterExists(EntityLockApiConstants.SQL_JOIN_CONDITION, elem)) {
                        sqlJoinCondition = this.fromApiJsonHelper.extractStringNamed(EntityLockApiConstants.SQL_JOIN_CONDITION, elem);
                        ValidateSQLInjection.validateSQLQuery(sqlJoinCondition);
                    }
                    final StringBuilder lockOrUnlockSqlQuery = getLockOrUnlockSqlQuery(entityType, actionType, tableName, sqlJoinCondition);
                    String sqlWhereCondition = this.fromApiJsonHelper.extractStringNamed(EntityLockApiConstants.SQL_WHERE_CONDITION, elem);
                    ValidateSQLInjection.validateSQLQuery(sqlWhereCondition);
                    sqlWhereCondition = FinfluxStringUtils.replaceTemplateText(sqlWhereCondition, CommonConstants.entityIdParam, params);
                    lockOrUnlockSqlQuery.append(sqlWhereCondition);
                    sqlStatements.add(lockOrUnlockSqlQuery.toString());
                }
                if (!sqlStatements.isEmpty()) {
                    this.jdbcTemplate.batchUpdate(sqlStatements.toArray(new String[0]));
                }
            }
        }
    }

    private StringBuilder getLockOrUnlockSqlQuery(final EntityType entityType, final ActionType actionType, final String tableName,
            final String sqlJoinCondition) {
        final StringBuilder lockOrUnlockSqlQuery = new StringBuilder(100);
        lockOrUnlockSqlQuery.append("UPDATE ").append(tableName).append(" ").append(entityType.getSystemName()).append(" ");
        if (sqlJoinCondition != null && sqlJoinCondition.trim().length() > 0) {
            lockOrUnlockSqlQuery.append(sqlJoinCondition);
        }
        lockOrUnlockSqlQuery.append(" ");
        if (actionType.isLock()) {
            lockOrUnlockSqlQuery.append(" SET ").append(entityType.getSystemName()).append(".is_locked = true ");
        } else {
            lockOrUnlockSqlQuery.append(" SET ").append(entityType.getSystemName()).append(".is_locked = false ");
        }
        return lockOrUnlockSqlQuery;
    }

    private String getLockOrUnlockConfigVaues(final String businessEventName, final EntityType entityType) {
        final StringBuilder sql = new StringBuilder(100);
        sql.append("select rluc.condition_json as conditionJson ");
        sql.append("from f_row_lock_or_unlock_configuration rluc ");
        sql.append("where rluc.entity_type = ? ");
        if (businessEventName != null) {
            sql.append("and rluc.business_event_name = ? ");
            return this.jdbcTemplate
                    .queryForObject(sql.toString(), String.class, new Object[] { entityType.getValue(), businessEventName });
        }
        return this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[] { entityType.getValue() });
    }

    @Override
    public void unlockEntity(final Long entityId, final EntityType entityType, final String businessEventName) {
        final ActionType actionType = ActionType.UNLOCK;
        final String conditionJsonString = getLockOrUnlockConfigVaues(businessEventName, entityType);
        processForLockOrUnlockEntity(entityId, actionType, conditionJsonString);
    }

    @Override
    public void validateEntityRecordLockedOrNot(final EntityType entityType, final boolean isLocked) {
        if (isLocked && entityType != null) {
            final HashMap<String, Object> params = new HashMap<>();
            params.put(CommonConstants.entityTypeParam, entityType.getCode());
            String globalisationMessageCode = "error.msg.{{entityType}}.is.locked";
            globalisationMessageCode = FinfluxStringUtils.replaceTemplateText(globalisationMessageCode, CommonConstants.entityTypeParam,
                    params);
            params.clear();
            params.put(CommonConstants.entityTypeParam, entityType.getDisplayName());
            String defaultUserMessage = "{{entityType}} is locked";
            defaultUserMessage = FinfluxStringUtils.replaceTemplateText(defaultUserMessage, CommonConstants.entityTypeParam, params);
            throw new EntityLockedException(globalisationMessageCode, defaultUserMessage, entityType.getDisplayName());
        }
    }

}
