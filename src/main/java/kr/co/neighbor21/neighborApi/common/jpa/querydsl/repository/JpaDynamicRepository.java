package kr.co.neighbor21.neighborApi.common.jpa.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.neighbor21.neighborApi.common.request.DynamicFilter;
import kr.co.neighbor21.neighborApi.common.request.DynamicSearchRequest;
import kr.co.neighbor21.neighborApi.common.request.DynamicSorter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JpaRepository 를 확장하여 SearchDTO 를 활용하는 dynamicSearch 등의 메서드를 추가할 수 있는 Interface
 *
 * @param <T>  Entity type
 * @param <ID> Entity key type, @Id or @EmbeddedId
 * @author GEON LEE
 * @since 2023-07-26<br />
 * 2023-07-27 GEONLEE - insertDynamic, updateDynamic, deleteDynamic 추가<br />
 * 2023-08-02 GEONLEE - findDynamic parameter object 로 변경<br />
 */
@NoRepositoryBean
public interface JpaDynamicRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    long countDynamic(Object parameter);

    List<T> findDynamic(Object parameter);

    List<T> findDynamicWithPageable(DynamicSearchRequest parameter);

    JPAQueryFactory getQueryFactory();

    void insertDynamic(Object parameter, Class<?> mapper);

    void updateDynamic(Object parameter, Class<?> mapper);

    void deleteDynamic(Object parameter, Class<?> mapper);

    List<OrderSpecifier<String>> getOrderSpecifierList(ArrayList<DynamicSorter> sortList);

    BooleanBuilder getBooleanBuilder(ArrayList<DynamicFilter> filters);

}
