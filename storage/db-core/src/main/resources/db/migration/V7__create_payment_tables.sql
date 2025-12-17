-- Payment 테이블 생성
CREATE TABLE payment (
                         id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                         application_id  BIGINT       NOT NULL COMMENT '청약서 ID',
                         user_id         BIGINT       NOT NULL COMMENT '사용자 ID',
                         amount          BIGINT       NOT NULL COMMENT '결제 금액',
                         method          VARCHAR(50)  NOT NULL COMMENT '결제 수단 (CARD, TRANSFER ...)',
                         payment_status  VARCHAR(20)  NOT NULL COMMENT '결제 상태 (PENDING, COMPLETED, CANCELED)',
                         paid_at         DATETIME(6)           COMMENT '결제 승인 일시',
                         status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '엔티티 상태',
                         created_at      DATETIME(6)  NOT NULL COMMENT '생성 일시',
                         updated_at      DATETIME(6)  NOT NULL COMMENT '수정 일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='결제 정보';


-- PaymentCancel 테이블 생성 (취소 이력)
CREATE TABLE payment_cancel (
                                id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'PK',
                                payment_id    BIGINT        NOT NULL COMMENT '대상 결제 ID',
                                user_id       BIGINT        NOT NULL COMMENT '취소 요청자 ID',
                                refund_amount BIGINT        NOT NULL COMMENT '환불 금액',
                                reason        VARCHAR(255)  NOT NULL COMMENT '취소 사유',
                                canceled_at   DATETIME(6)           COMMENT '취소 일시',
                                status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '엔티티 상태',
                                created_at    DATETIME(6)   NOT NULL COMMENT '생성 일시',
                                updated_at    DATETIME(6)   NOT NULL COMMENT '수정 일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='결제 취소 이력';
