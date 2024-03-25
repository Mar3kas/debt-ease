package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;

public interface EmailService {
    void sendNotificationEmail(DebtCase debtCase);
}
