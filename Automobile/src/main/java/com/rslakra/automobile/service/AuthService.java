package com.rslakra.automobile.service;

import com.rslakra.appsuite.spring.service.AbstractService;
import com.rslakra.automobile.domain.entities.AutoUser;
import com.rslakra.automobile.dto.LoginRequest;

import java.util.Optional;

/**
 * @author Rohtash Lakra
 * @created 4/26/23 2:06 PM
 */
public interface AuthService extends AbstractService<AutoUser, Long> {

    /**
     * @param autoUser
     * @return
     */
    public AutoUser register(AutoUser autoUser);

    /**
     * @param loginRequest
     * @return
     */
    public Optional<AutoUser> login(LoginRequest loginRequest);

}
