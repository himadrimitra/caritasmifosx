package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class GroupAndClientChargeNotInSynWithMeeting extends
AbstractPlatformDomainRuleException {
public GroupAndClientChargeNotInSynWithMeeting(Long groupId) {
super("error.msg.group.not.in.syn.with.client.charge.meeting",
		"and group with id " + groupId
				+ "not in sync with client charge meeting", groupId);
}
}