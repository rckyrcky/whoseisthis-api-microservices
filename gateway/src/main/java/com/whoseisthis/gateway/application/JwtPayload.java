package com.whoseisthis.gateway.application;

import com.whoseisthis.gateway.user.core.UserRole;

public record JwtPayload(Long id, UserRole role) {
}
