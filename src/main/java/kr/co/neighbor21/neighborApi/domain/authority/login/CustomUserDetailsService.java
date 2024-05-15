package kr.co.neighbor21.neighborApi.domain.authority.login;

import jakarta.transaction.Transactional;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.UnauthorizedException;
import kr.co.neighbor21.neighborApi.domain.operator.OperatorRepository;
import kr.co.neighbor21.neighborApi.entity.M_OP_OPERATOR;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 인증정보 서비스<br />
 * AuthController 에서 authenticationManagerBuilder 에 의해 호출되며 인증정보를 포함한<br />
 * Security UserDetail 정보를 리턴한다.<br />
 *
 * @author GEONLEE
 * @since 2022-11-11<br />
 * 2023-10-06 BITNA - 권한 없음 권한 로그인 실패하도록 수정<br />
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private static final String NO_AUTH = "AUTH000000";
    private final OperatorRepository operatorRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return operatorRepository.findOneByKeyUserId(userId).map(user -> createUser(userId, user))
                .orElseThrow(() -> new UsernameNotFoundException(userId + " -> not found."));
    }

    /**
     * Security User 정보를 생성한다.
     *
     * @author GEONLEE
     * @since 2023-03-06<br />
     * 2023-06-05 GEONLEE - 기존에 복수권한을 갖을 수 있던 코드에서 단일 권한으로 수정<br />
     * 2024-03-27 GEONLEE - BadCredentialsException -> UnauthorizedException 으로 변경<br />
     * BadCredentialsException 은 JwtAuthenticationEntryPoint 로 전달되기 때문에 불필요 로직을 타게 됨.<br />
     **/
    private User createUser(String userId, M_OP_OPERATOR oprEntity) {
        if (oprEntity.getAuthority() == null) {
            throw new UnauthorizedException(CommonErrorCode.FORBIDDEN, null);
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(oprEntity.getAuthority().getKey().getAuthorityId()));
        LOGGER.info(userId + " / Authority : {}", grantedAuthorities);
        return new User(
                oprEntity.getKey().getUserId(),
                oprEntity.getPassword(),
                grantedAuthorities
        );
    }
}