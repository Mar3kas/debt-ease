package com.dm.debtease.service;

import com.dm.debtease.model.Admin;

public interface AdminService {
    Admin getAdminByUsername(String username);
}
