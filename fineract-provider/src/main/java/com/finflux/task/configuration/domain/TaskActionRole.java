package com.finflux.task.configuration.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_task_action_role")
public class TaskActionRole extends AbstractPersistable<Long> {

	@Column(name = "task_action_id", nullable = false)
	private Long taskActionId;

	@Column(name = "role_id", nullable = true)
	private Long roleId;

	protected TaskActionRole() {}

	public Long getTaskActionId() {
		return taskActionId;
	}

	public void setTaskActionId(Long taskActionId) {
		this.taskActionId = taskActionId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}