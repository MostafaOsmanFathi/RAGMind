package com.ragmind.ragbackend.security.Principal;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final String email;

    public StompPrincipal(String name) {
        this.email = name;
    }

    @Override
    public String getName() {
        return email;
    }
}