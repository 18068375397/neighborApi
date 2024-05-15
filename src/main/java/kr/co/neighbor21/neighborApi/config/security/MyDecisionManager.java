package kr.co.neighbor21.neighborApi.config.security;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * Authorization management, which determines whether the current user has the permission for the request
 *
 * @author RudeCrab
 */
@Slf4j
@Component
public class MyDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        // If the authorization rule is empty, it means that this URL can access without authorization.
        if (Collections.isEmpty(configAttributes)) {
            return;
        }
        log.info("---DecisionManager---");
        // Determine whether the authorization rules match the permissions of the current user
        for (ConfigAttribute ca : configAttributes) {
//            authentication.getName();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                // If it matches, it means that the current user has this permission. End the method directly.
                if (Objects.equals(authority.getAuthority(), ca.getAttribute())) {
                    return;
                }
            }
        }
        // The representative does not have permission.
        throw new AccessDeniedException("No relevant permissions");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
