package kr.co.neighbor21.neighborApi.config.security;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import kr.co.neighbor21.neighborApi.entity.key.M_OP_AUTHORITY_KEY;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Component
public class MySecurityMetadataSource implements SecurityMetadataSource {

    @Getter
    private static final Set<M_OP_AUTHORITY> AUTHORITIES = new HashSet<>();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        log.info("---MySecurityMetadataSource---");
        M_OP_AUTHORITY mOpAuthority = new M_OP_AUTHORITY();
        mOpAuthority.setKey(new M_OP_AUTHORITY_KEY("AUTH000001"));
        mOpAuthority.setAuthorityCode("POST:/v1/operator/search");
        mOpAuthority.setAuthorityName("tt");
        mOpAuthority.setDescription("test a interface");


        AUTHORITIES.add(mOpAuthority);

        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        // Traverse all permission resources to match the permissions required by the current request
        for (M_OP_AUTHORITY authority : AUTHORITIES) {
            String[] split = authority.getAuthorityCode().split(":");
            AntPathRequestMatcher ant = new AntPathRequestMatcher(split[1]);
            if (request.getMethod().equals(split[0]) && ant.matches(request)) {
                return Collections.singletonList(new SecurityConfig(authority.getKey().getAuthorityId()));
            }
        }

        // This means that the request can be accessed without authorization.
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
