package com.nexsol.tpa.core.domain;

public interface EmailSender {
    void send(String toEmail, String authCode);
}
