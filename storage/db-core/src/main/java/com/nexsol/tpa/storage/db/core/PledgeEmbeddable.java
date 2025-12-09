package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.Pledge;
import com.nexsol.tpa.core.enums.BondSendStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class PledgeEmbeddable {

	private String pledgeBankName;

	private String pledgeManagerName;

	private String pledgeManagerPhone;

	private Long pledgeAmount;

	private String pledgeAddress;

	@Enumerated(EnumType.STRING)
	private BondSendStatus pledgeBondStatus;

	private String pledgeRemark;

	public PledgeEmbeddable(Pledge domain) {
		this.pledgeBankName = domain.bankName();
		this.pledgeManagerName = domain.managerName();
		this.pledgeManagerPhone = domain.phone();
		this.pledgeAmount = domain.amount();
		this.pledgeAddress = domain.address();
		this.pledgeRemark = domain.remark();
		this.pledgeBondStatus = domain.bondSendStatus();

	}

	public Pledge toDomain() {
		return Pledge.builder()
			.bankName(pledgeBankName)
			.managerName(pledgeManagerName)
			.phone(pledgeManagerPhone)
			.amount(pledgeAmount)
			.address(pledgeAddress)
			.bondSendStatus(pledgeBondStatus)
			.remark(pledgeRemark)
			.build();
	}

}