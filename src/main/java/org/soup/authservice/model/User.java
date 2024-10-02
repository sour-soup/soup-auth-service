package org.soup.authservice.model;

import java.util.UUID;

public record User(UUID id, String username) {
}
