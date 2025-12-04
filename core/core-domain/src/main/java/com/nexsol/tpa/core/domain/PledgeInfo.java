package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.BondSendStatus;
import lombok.Builder;

@Builder
public record PledgeInfo(
        String bankName,
        String managerName,
        String managerPhone,
        Long amount,
        Address address,
        BondSendStatus bondSendStatus,
        String remark
) {}