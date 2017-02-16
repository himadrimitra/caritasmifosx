package com.finflux.task.template.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.service.OfficeReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.group.data.CenterData;
import org.apache.fineract.portfolio.group.service.CenterReadPlatformService;
import org.apache.fineract.portfolio.village.data.VillageData;
import org.apache.fineract.portfolio.village.service.VillageReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bank.data.BankData;
import com.finflux.reconcilation.bank.service.BankReadPlatformService;
import com.finflux.task.template.data.IdAndName;
import com.finflux.task.template.data.TaskConfigTemplateEntityData;
import com.finflux.task.template.data.TaskConfigTemplateEntityType;
import com.finflux.task.template.data.TaskConfigTemplateObject;

@Service
public class TaskConfigTemplateReadServiceImpl implements TaskConfigTemplateReadService 
{
    private final PlatformSecurityContext context;
    private final TaskConfigDataMapper taskConfigDataMapper = new TaskConfigDataMapper();
    private final JdbcTemplate jdbcTemplate;
    private final BankReadPlatformService bankReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final CenterReadPlatformService centerReadPlatformService;
    private final ClientReadPlatformService clientReadPlatformService;
    private final VillageReadPlatformService villageReadPlatformService;
    
    @Autowired
    public TaskConfigTemplateReadServiceImpl(final RoutingDataSource dataSource,
            final BankReadPlatformService bankReadPlatformService,
            final OfficeReadPlatformService officeReadPlatformService,
            final PlatformSecurityContext context,
            final CenterReadPlatformService centerReadPlatformService,
            final ClientReadPlatformService clientReadPlatformService,
            final VillageReadPlatformService villageReadPlatformService) 
    {
        this.jdbcTemplate=new JdbcTemplate(dataSource);
        this.bankReadPlatformService=bankReadPlatformService;
        this.officeReadPlatformService=officeReadPlatformService;
        this.context=context;
        this.centerReadPlatformService=centerReadPlatformService;
        this.clientReadPlatformService=clientReadPlatformService;
        this.villageReadPlatformService=villageReadPlatformService;
    }
    @Override
    public List<TaskConfigTemplateObject> retrieveTemplateData() 
    {
        final String sql = "select " + taskConfigDataMapper.schema();
        try {
            return this.jdbcTemplate.query(sql,taskConfigDataMapper);
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public TaskConfigTemplateObject readOneTask(Long templateId) {
        final String sql = "select " + taskConfigDataMapper.schema()+" where template.id=?";
        try {
            List<TaskConfigTemplateObject> templateObjects=this.jdbcTemplate.query(sql,taskConfigDataMapper,templateId);
            if(templateObjects != null && templateObjects.size() == 1)
            {
                return templateObjects.get(0);
            }
        } catch (final EmptyResultDataAccessException e) {
            return null;
        }
        return null;
    }
    @Override
    public TaskConfigTemplateEntityData retrieveTemplateEntities(Long templateId) 
    {
        List<IdAndName> idAndNameList=new ArrayList<IdAndName>();
        AppUser user=this.context.authenticatedUser();
        Office office=user.getOffice();
        TaskConfigTemplateObject taskConfigTemplate=this.readOneTask(templateId);
        TaskConfigTemplateEntityType entity=taskConfigTemplate.getEntity();
        TaskConfigTemplateEntityData taskConfigTemplateEntityData=null;
        if(TaskConfigTemplateEntityType.OFFICE.getValue()==entity.getValue())
        {
            List<OfficeData> offices=(List<OfficeData>) this.officeReadPlatformService.retrieveAllOfficesForDropdown();
            for(OfficeData officeData:offices)
            {
                IdAndName idAndName=new IdAndName(officeData.getId(),officeData.name());
                idAndNameList.add(idAndName);
            }
            taskConfigTemplateEntityData=new TaskConfigTemplateEntityData(entity, idAndNameList);
        }
        else if(TaskConfigTemplateEntityType.BANK.getValue()==entity.getValue())
        {
            List<BankData> banks=this.bankReadPlatformService.retrieveAllBanks();
            for(BankData bank:banks)
            {
                IdAndName idAndName=new IdAndName(bank.getId(),bank.getName());
                idAndNameList.add(idAndName);
            }
            taskConfigTemplateEntityData=new TaskConfigTemplateEntityData(entity, idAndNameList);
        }
        else if(TaskConfigTemplateEntityType.CENTER.getValue()==entity.getValue())
        {
            List<CenterData> centers=(List<CenterData>) this.centerReadPlatformService.retrieveAllForDropdown(office.getId());
            for(CenterData centerData:centers)
            {
                IdAndName idAndName=new IdAndName(centerData.getId(),centerData.getName());
                idAndNameList.add(idAndName);
            }
            taskConfigTemplateEntityData=new TaskConfigTemplateEntityData(entity, idAndNameList);
        }
        else if(TaskConfigTemplateEntityType.CLIENT.getValue()==entity.getValue())
        {
            List<ClientData> clients=(List<ClientData>) this.clientReadPlatformService.retrieveAllForLookupByOfficeId(office.getId());
            for(ClientData client:clients)
            {
                IdAndName idAndName=new IdAndName(client.id(),client.displayName());
                idAndNameList.add(idAndName);
            }
            taskConfigTemplateEntityData=new TaskConfigTemplateEntityData(entity, idAndNameList);
        }
        else if(TaskConfigTemplateEntityType.VILLAGE.getValue()==entity.getValue())
        {
            List<VillageData> villages=(List<VillageData>) this.villageReadPlatformService.retrieveVillagesForLookup(office.getId());
            for(VillageData village:villages)
            {
                IdAndName idAndName=new IdAndName(village.getVillageId(),village.getVillageName());
                idAndNameList.add(idAndName);
            }
            taskConfigTemplateEntityData=new TaskConfigTemplateEntityData(entity, idAndNameList);
        }
        
        return taskConfigTemplateEntityData;
    }
}
final class TaskConfigDataMapper implements RowMapper<TaskConfigTemplateObject> {

    private final String schemaSql;

    public TaskConfigDataMapper() {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("template.id as id,").append("template.name as name,").append("template.short_name as shortName,")
        .append("template.entity_type as entity,").append("template.task_config_id as task_config_id").append(" from f_task_config_template template");
        this.schemaSql = sqlBuilder.toString();
    }

    public String schema() {
        return this.schemaSql;
    }

    @Override
    public TaskConfigTemplateObject mapRow(ResultSet rs, int rowNum) throws SQLException {
        final long id=rs.getLong("id");
        final String name=rs.getString("name");
        final String shortName=rs.getString("shortName");
        final TaskConfigTemplateEntityType entity=TaskConfigTemplateEntityType.fromInt(rs.getInt("entity"));
        final Long taskConfigId=rs.getLong("task_config_id");
        return new TaskConfigTemplateObject(id, name, shortName, entity, taskConfigId);
    }
}

