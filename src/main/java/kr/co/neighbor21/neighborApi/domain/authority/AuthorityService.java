package kr.co.neighbor21.neighborApi.domain.authority;

import kr.co.neighbor21.neighborApi.common.response.GenerateResponse;
import kr.co.neighbor21.neighborApi.common.response.structure.ItemsResponse;
import kr.co.neighbor21.neighborApi.domain.authority.record.AuthoritySearchRequest;
import kr.co.neighbor21.neighborApi.domain.authority.record.AuthoritySearchResponse;
import kr.co.neighbor21.neighborApi.entity.M_OP_AUTHORITY;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper = AuthorityMapper.INSTANCE;

    public ResponseEntity<ItemsResponse<AuthoritySearchResponse>> getAuthorityList(AuthoritySearchRequest parameter) {
        GenerateResponse<AuthoritySearchResponse> generateResponse = new GenerateResponse<>();
        return generateResponse.generateItemsResponse(() -> {

            List<M_OP_AUTHORITY> authorities = authorityRepository.findAllByAuthorityNameLike("%" + parameter.authorityName() + "%");
            return authorityMapper.toSearchResponseList(authorities);
        });

    }
}
