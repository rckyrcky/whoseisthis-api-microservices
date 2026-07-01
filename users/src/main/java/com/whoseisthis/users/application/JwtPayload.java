package com.whoseisthis.users.application;

import com.whoseisthis.users.core.UserRole;

public record JwtPayload(Long id, UserRole role) {
}
