package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.Pledge;
import com.nexsol.tpa.core.enums.BondSendStatus;

public record PledgeRequest(String bankName, String managerName, String managerPhone, Long amount, String address,
		BondSendStatus bondStatus, String remark) {

	public Pledge toPledge() {
		return Pledge.builder()
			.bankName(bankName)
			.managerName(managerName)
			.managerPhone(managerPhone)
			.amount(amount)
			.address(address)
			.bondSendStatus(bondStatus)
			.remark(remark)
			.build();
	}
}
