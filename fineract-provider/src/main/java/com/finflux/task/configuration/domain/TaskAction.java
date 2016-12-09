package com.finflux.task.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_task_action")
public class TaskAction extends AbstractPersistable<Long> {

	@Column(name = "action_group_id", nullable = false)
	private Long actionGroupId;

	@Column(name = "action", nullable = false)
	private Integer action;

	protected TaskAction() {}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public Long getActionGroupId() {
		return actionGroupId;
	}

	public void setActionGroupId(Long actionGroupId) {
		this.actionGroupId = actionGroupId;
	}
}