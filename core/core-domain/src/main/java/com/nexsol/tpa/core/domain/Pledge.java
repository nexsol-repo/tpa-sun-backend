package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.BondSendStatus;
import lombok.Builder;

@Builder
public record Pledge(String bankName, String managerName, String managerPhone, BondSendStatus bondSendStatus, String remark,
					 Long amount, String address) {
}