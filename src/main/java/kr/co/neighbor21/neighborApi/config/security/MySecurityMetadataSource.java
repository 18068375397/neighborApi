package kr.co.neighbor21.neighborApi.config.security;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.neighbor21.neighborApi.domain.authority.AuthorityRepository;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.*;


@Slf4j
@Component
public class MySecurityMetadataSource implements SecurityMetadataSource {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Getter
    private static final Set<M_OP_AUTHORITY> AUTHORITIES = new HashSet<>();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        log.info("---MySecurityMetadataSource---");
        List<M_OP_AUTHORITY> authorities = authorityRepository.findAll();
        AUTHORITIES.addAll(authorities);

        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        // Traverse all permission resources to match the permissions required by the current request
        for (M_OP_AUTHORITY authority : AUTHORITIES) {
            String[] split = authority.getAuthorityCode().split(":");
            AntPathRequestMatcher ant = new AntPathRequestMatcher(split[1]);
            if (request.getMethod().equals(split[0]) && ant.matches(request)) {
                return Collections.singletonList(new SecurityConfig(authority.getAuthorityId()));
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
