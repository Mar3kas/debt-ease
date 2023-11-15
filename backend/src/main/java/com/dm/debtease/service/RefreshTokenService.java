package com.dm.debtease.service;

import com.dm.debtease.model.RefreshToken;

public interface RefreshTokenService {
    String createRefreshToken(String username);

    boolean validateRefreshToken(RefreshToken token);

    RefreshToken findByToken(String token);
}
