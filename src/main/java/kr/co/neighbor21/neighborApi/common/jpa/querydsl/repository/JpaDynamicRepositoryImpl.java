package kr.co.neighbor21.neighborApi.common.jpa.querydsl.repository;


import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.NoResultException;
import kr.co.neighbor21.neighborApi.common.exception.code.CommonErrorCode;
import kr.co.neighbor21.neighborApi.common.exception.custom.ServiceException;
import kr.co.neighbor21.neighborApi.common.jpa.baseEntity.BaseEntity;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.DefaultSort;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SaveLocalDateTime;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.SearchField;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.annotation.UpdateLocalDateTime;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.Operator;
import kr.co.neighbor21.neighborApi.common.jpa.querydsl.enumeration.SortOrder;
import kr.co.neighbor21.neighborApi.common.request.DynamicFilter;
import kr.co.neighbor21.neighborApi.common.request.DynamicSearchRequest;
import kr.co.neighbor21.neighborApi.common.request.DynamicSorter;
import kr.co.neighbor21.neighborApi.common.util.date.AljjabaegiDate;
import kr.co.neighbor21.neighborApi.common.util.regExp.RegExp;
import kr.co.neighbor21.neighborApi.common.variable.CommonVariables;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 동적인 컬럼 조회, 정렬, 페이징을 처리하기 위한 JpaDynamicRepository 구현체
 *
 * @param <T>  Entity type
 * @param <ID> Entity key type, @Id or @EmbeddedId
 * @author GEONLEE
 * @since 2023-07-26 GEONLEE - 생성<br />
 * 2023-07-27 GEONLEE - dynamic insert/update/delete 추가 <br />
 * 2023-07-28 GEONLEE - getBooleanBuilder, getOrderSpecifierList private -> public 으로 변경<br />
 * 2023-07-28 LCH      - 추가, 수정 시 시간 자동 등록 적용.<br />
 * 2023-08-02 GEONLEE - findDynamic 개선, 불필요 null 처리 제거, parameter 를 entity 로 변경 후 키 체크를 하는 방식으로 변경(key에 date type이 있을 경우 키값 추출 문제 때문)<br />
 * - getEntityByDto, getEntityByPersistenceContext 분기<br />
 * - convertToFilter 추가<br />
 * 2023-08-04 GEONLEE - getCastValue 메서드 추가, 형태에 따라 CAST 해서 BooleanExpression 에 추가 (String type 제외 안되던 문제 수정)<br />
 * 2023-08-08 GEONLEE - varchar date type between 처리 추가, profiles => local 일 경우에만 쿼리 print 되도록 설정<br />
 * 2023-08-09 GEONLEE - properties 에 접근하는 로직이 멀티 DB 환경에 문제가 있어, 우선 삭제<br />
 * 2023-08-10 GEONLEE - updateFromDto 사용 방식으로 변경 (null 로 update 되는 문제)<br />
 * 2023-08-11 GEONLEE - countDynamic parameter type object 로 변경<br />
 * 2023-08-16 HYUNMO - pathbuilder 생성자에 추가, 기존에 from alias가 소문자여서 다이나믹을 쓰지 않은 곳에서 alias가 달라서 오류발생. 패스빌더 디폴트로 변경<br />
 * 2023-07-28 LCH      - EQ에도 LocalDate, LocalDateTime 분기 추가.<br />
 * 2023-08-21 GEONLEE - page 처리 시 noOffset 로직 추가 및 조회 조건 설정 시 greater than, less than 추가<br />
 * 2023-08-21 YS Lim - setLocalDateTime()에서 LocalDate 타입도 처리할 수 있도록 수정<br />
 * 2023-10-11 GEONLEE - update/delete Dynamic -> return void로 변경하고 NoResultException 발생 시키도록 변경<br />
 * 2023-10-12 GEONLEE - getBooleanBuiler 날짜 타입 조회 방식 변경, getDateWhereExpression 메서드 추가 BETWEEN 제거 -> GOT, LT로 변경<br />
 * 날짜 타입이 아닌 경우 between 처리 방식 변경<br />
 * 2023-10-13 GEONLEE - insert/update/delete 처리 방식 변경 (참고 http://solwbs.neighbor21.co.kr:1177/redmine/issues/17204)<br />
 * 2023-11-23 GEONLEE - lIKE 문 공백 없이 조회되도록 개선<br />
 * 2024-02-19 GEONLEE - getFieldType 추가 (SearchField 설정 시 2 depth 이상 fieldTypeMap type 을 설정하지 못했음) depth 에 상관없이 되도록 개선<br />
 * 2024-03-11 GEONLEE - DynamicRepository 기능 단순화. insert/update/delete/관련 메서드 @Deprecated 처리<br />
 * 2024-03-22 GEONLEE - searchRequest, FilterDTO, SortDTO -> record 및 명칭 변경에 따른 작업<br />
 */
public class JpaDynamicRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JpaDynamicRepository<T, ID> {

    private final Logger LOGGER = LoggerFactory.getLogger(JpaDynamicRepositoryImpl.class);
    //    private final EntityManager entityManager; /*아직 미활용*/
    private final JPAQueryFactory queryFactory;
    private final PathBuilder<T> pathBuilder;
    private final Class<T> entityClass;
    private final HashMap<String, String> serializedMap;
    private final HashMap<String, String> fieldTypeMap;

    public JpaDynamicRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.queryFactory = new JPAQueryFactory(entityManager);
//        this.entityManager = entityManager;
        this.entityClass = entityInformation.getJavaType();
        this.pathBuilder = new PathBuilderFactory().create(this.entityClass);
        this.serializedMap = new HashMap<>();
        this.fieldTypeMap = new HashMap<>();
        setSerializedMap();
    }

    /**
     * queryFactory return 메서드, repository 마다 DI 하지 않기 위해 추가. Thread-safe 문제될 시 제거.
     *
     * @return JPAQueryFactory
     * @author GEONLEE
     * @since 2023-07-26
     */
    @Override
    public JPAQueryFactory getQueryFactory() {
        return this.queryFactory;
    }

    /**
     * 조회조건에 따른 Count 리턴 메서드
     *
     * @param parameter searchRequest
     * @return long 조회 개수
     * @author GEONLEE
     * @apiNote 2023-08-07 GEONLEE - fetch().size() 시간 오래 걸리는 문제 개선 fetchType.LAZY가 동작 안하는 듯
     * 2023-08-11 GEONLEE - parameter object 로 변경, 여러 타입 처리
     * @since 2023-07-26
     */
    @Override
    @SuppressWarnings("unchecked")
    public long countDynamic(Object parameter) throws NullPointerException {
        BooleanBuilder whereClause = null;
        if (parameter != null) {
            if (parameter instanceof DynamicSearchRequest searchRequest) {
                if (searchRequest.filter() != null) {
                    whereClause = getBooleanBuilder(searchRequest.filter());
                }
            } else if (parameter instanceof DynamicFilter filter) {
                ArrayList<DynamicFilter> filters = new ArrayList<>();
                filters.add(filter);
                whereClause = getBooleanBuilder(filters);
            } else if (parameter instanceof List) {
                if (((List<?>) parameter).size() > 0) {
                    if (((List<?>) parameter).get(0) instanceof DynamicFilter) {
                        whereClause = getBooleanBuilder((ArrayList<DynamicFilter>) parameter);
                    }
                }
            } else {
                whereClause = getBooleanBuilder(convertToFilter(parameter));
            }
        }
        JPAQuery<Long> query = queryFactory.select(this.pathBuilder.count()).from(this.pathBuilder).where(whereClause);
        printQuery(query.toString());
        Long size = query.fetchOne();
        return (size == null) ? 0 : size;
    }

    /**
     * Where, Order by 조건 처리 메서드 (paging X)
     *
     * @param parameter searchRequest
     * @return List<ENTITY>
     * @author GEONLEE
     * @apiNote 2023-08-02 GEONLEE - parameter 타입에 따라 처리 되도록 수정
     * * - Object 를 FilterDTO 로 변환 해서 Where 조건 추가 되도록 개선
     * * 2024-02-19 GEONLEE 변수 pattern variable 방식 으로 변경
     * @since 2023-07-26
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findDynamic(Object parameter) {
        BooleanBuilder whereClause = null;
        List<OrderSpecifier<String>> sortList = new ArrayList<>();
        if (parameter != null) {
            if (parameter instanceof DynamicSearchRequest searchRequest) {
                if (searchRequest.filter() != null) {
                    whereClause = getBooleanBuilder(searchRequest.filter());
                }
                if (searchRequest.sort() != null) {
                    sortList = getOrderSpecifierList(searchRequest.sort());
                }
            } else if (parameter instanceof DynamicFilter filter) {
                ArrayList<DynamicFilter> filters = new ArrayList<>();
                filters.add(filter);
                whereClause = getBooleanBuilder(filters);
            } else if (parameter instanceof DynamicSorter sort) {
                ArrayList<DynamicSorter> sorts = new ArrayList<>();
                sorts.add(sort);
                sortList = getOrderSpecifierList(sorts);
            } else if (parameter instanceof List) {
                if (((List<?>) parameter).size() > 0) {
                    if (((List<?>) parameter).get(0) instanceof DynamicFilter) {
                        whereClause = getBooleanBuilder((ArrayList<DynamicFilter>) parameter);
                    } else if (((List<?>) parameter).get(0) instanceof DynamicSorter) {
                        sortList = getOrderSpecifierList((ArrayList<DynamicSorter>) parameter);
                    }
                }
            } else {
                whereClause = getBooleanBuilder(convertToFilter(parameter));
            }
        }
        JPAQuery<T> query = queryFactory.selectFrom(this.pathBuilder)
                .where(whereClause)
                .orderBy(sortList.toArray(OrderSpecifier[]::new));
        printQuery(query.toString());
        return query.fetch();
    }

    /**
     * Where, Order by, paging 조건 처리 메서드
     *
     * @param parameter searchRequest
     * @return List<ENTITY>
     * @author GEONLEE
     * @apiNote 2023-08-02 GEONLEE parameter null 처리 추가<br />
     * 2023-08-21 GEONLEE noOffset 기능 추가. 현재 optional<br />
     * @since 2023-07-26
     */
    @Override
    public List<T> findDynamicWithPageable(DynamicSearchRequest parameter) {
        JPAQuery<T> query = null;
        BooleanBuilder whereClause = getBooleanBuilder(parameter.filter());
        List<OrderSpecifier<String>> sortList = getOrderSpecifierList(parameter.sort());
        if (parameter.skip() < 0 || parameter.take() < 0) {
            LOGGER.error("'skip' and 'take' must be positive(+). skip: {}, take: {}"
                    , parameter.skip(), parameter.take());
            throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, null);
        }
        long offset = (long) parameter.skip() * parameter.take();
        int limit = parameter.take();

        /* sortList(기본 혹은 default sort) 와 noOffsetFilter 가 있을 경우
         * noOffset 방식으로 데이터 조회
         * */

        ArrayList<DynamicFilter> noOffsetFilterList = parameter.noOffsetFilter();
        if (noOffsetFilterList != null && noOffsetFilterList.size() > 0) {
            LOGGER.info("noOffset search...");
            for (int i = 0, n = noOffsetFilterList.size(); i < n; i++) {
                if (noOffsetFilterList.get(i).operator() == null) {
                    noOffsetFilterList.set(i,
                            DynamicFilter.builder()
                                    .field(noOffsetFilterList.get(i).field())
                                    .value(noOffsetFilterList.get(i).value())
                                    .operator("lt").build());
                }
            }
            BooleanBuilder noOff = getBooleanBuilder(noOffsetFilterList);
            whereClause.and(noOff);
            query = queryFactory.selectFrom(this.pathBuilder)
                    .where(whereClause)
                    .orderBy(sortList.toArray(OrderSpecifier[]::new))
                    .limit(limit);
        } else {
            LOGGER.info("offset search...");
            query = queryFactory.selectFrom(this.pathBuilder)
                    .where(whereClause)
                    .orderBy(sortList.toArray(OrderSpecifier[]::new))
                    .offset(offset)
                    .limit(limit);
        }
        printQuery(query.toString());
        printQuery(query.toString());
        return query.fetch();
    }

    /**
     * FilterDTO 를 받아 조회 조건을 생성.
     *
     * @param filters ArrayList<FilterDTO>
     * @return BooleanBuilder
     * @author GEONLEE
     * @since 2023-7-24
     * 2023-07-28 GEONLEE - is null, is not null 추가
     * 2023-08-04 GEONLEE - LIKE 문 대소문자 구분없이 조회되도록 개선, 전체 적으로 형변환 메서드 호출 방식으로 변경
     * - getOperator().toLowerCase() 추가, in 문 JsonSyntaxException 추가
     * 2023-08-07 GEONLEE - in 문 문자 따옴표(") 때문에 조회 안되는 문제 수정 replace 처리
     * 2023-08-08 GEONLEE - BETWEEN 문 String date type 처리, integer, duoble 형 안되는 문제 수정 (DateTimeException 추가)
     * 2023-08-11 GEONLEE - 'null' string 처리 추가
     * 2023-08-21 GEONLEE - greater than, less than 추가
     * 2023-10-12 GEONLEE - 날짜 타입 조회 조건 생성 방식 변경, getDateWhereExpression 메서드 추가 BETWEEN 제거 -> GOT, LT로 변경
     * 날짜 타입이 아닌 경우 between 처리 방식 변경
     */
    public BooleanBuilder getBooleanBuilder(ArrayList<DynamicFilter> filters) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (filters != null) {
            for (DynamicFilter filter : filters) {
                try {
                    System.out.println(filter.field());
                    System.out.println(this.serializedMap.get(filter.field()));
                    PathBuilder<Object> pathBuilder = this.pathBuilder.get(this.serializedMap.get(filter.field()), Object.class);
                    Optional<Operator> oop = Operator.valueOfOperator(filter.operator().toLowerCase());
                    if (oop.isEmpty()) {
                        LOGGER.error("There is no '{}' operator. Check the 'filter' parameter in searchRequest."
                                , filter.operator());
                        throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, null);
                    }
                    BooleanExpression condition = null;
                    Operator operator = oop.get();
                    if (operator == Operator.EQUAL) {
                        if (filter.value() != null && !"null".equalsIgnoreCase(filter.value())) {
                            String type = fieldTypeMap.get(filter.field());
                            if ("LocalDateTime".equals(type)) {
                                condition = getDateWhereExpression(pathBuilder, filter.value());
                            } else {
                                condition = pathBuilder.eq(getCastValue(filter));
                            }
                        } else {
                            condition = pathBuilder.isNull();
                        }
                        whereClause.and(condition);
                    } else if (operator == Operator.NOT_EQUAL) {
                        if (filter.value() != null && !"null".equalsIgnoreCase(filter.value())) {
                            condition = pathBuilder.ne(getCastValue(filter));
                        } else {
                            condition = pathBuilder.isNotNull();
                        }
                        whereClause.and(condition);
                    } else if (operator == Operator.LIKE) {
                        StringPath pathBuilderString = this.pathBuilder.getString(this.serializedMap.get(filter.field()));
                        condition = Expressions.stringTemplate("REPLACE({0}, ' ', '')", pathBuilderString)
                                .likeIgnoreCase("%" + filter.value().replaceAll(RegExp.REMOVE_BLANK, "") + "%");
                        whereClause.and(condition);
                    } else if (operator == Operator.BETWEEN) {
                        String type = fieldTypeMap.get(filter.field());
                        if ("LocalDateTime".equals(type)) {
                            condition = getDateWhereExpression(pathBuilder, filter.value());
                        } else {
                            String[] dateValues = filter.value().split(",");
                            List<DynamicFilter> filterList = new ArrayList<>();
                            for (String value : dateValues) {
                                filterList.add(
                                        DynamicFilter
                                                .builder()
                                                .field(filter.field())
                                                .value(value).build()
                                );
                            }
                            condition = Expressions.booleanOperation(Ops.BETWEEN
                                    , pathBuilder, Expressions.constant(getCastValue(filterList.get(0))), Expressions.constant(getCastValue(filterList.get(1))));
                        }
                        whereClause.and(condition);
                    } else if (operator == Operator.IN) {
                        try {
                            List<Object> values = new ArrayList<>();
                            JsonArray strJsonArray = CommonVariables.GSON.fromJson(filter.value(), JsonArray.class);
                            for (Object value : strJsonArray) {
                                values.add(
                                        getCastValue(
                                                DynamicFilter.builder()
                                                        .field(filter.field())
                                                        .value(String.valueOf(value).replaceAll("\"", ""))
                                                        .build()
                                        )
                                );
                            }
                            condition = pathBuilder.in(values);
                            whereClause.and(condition);
                        } catch (JsonSyntaxException e) {
                            LOGGER.error("Array-type parameters are requested. Check your parameter. -> '{}'", filter.value());
                            throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, e);
                        }
                    } else if (operator == Operator.LE) {
                        condition = Expressions.booleanOperation(Ops.LOE
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.and(condition);
                    } else if (operator == Operator.L) {
                        condition = Expressions.booleanOperation(Ops.LT
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.and(condition);
                    } else if (operator == Operator.NL) {
                        condition = Expressions.booleanOperation(Ops.LT
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.andNot(condition);
                    } else if (operator == Operator.GE) {
                        condition = Expressions.booleanOperation(Ops.GOE
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.and(condition);
                    } else if (operator == Operator.G) {
                        condition = Expressions.booleanOperation(Ops.GT
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.and(condition);
                    } else if (operator == Operator.NG) {
                        condition = Expressions.booleanOperation(Ops.GT
                                , pathBuilder, Expressions.constant(getCastValue(filter)));
                        whereClause.andNot(condition);
                    }
                } catch (NullPointerException e) {
                    if (filter.field() == null) {
                        LOGGER.error("The parameter filter's key is invalid. Check if any of the fields are 'null'. -> {}"
                                , filters, e);
                    } else {
                        LOGGER.error("There is no '{}' field to filtering in {} entity. Check filter parameter in SearchRequest."
                                , filter.field(), this.entityClass.getSimpleName(), e);
                    }
                    throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
                } catch (IndexOutOfBoundsException e) {
                    LOGGER.error("Check this parameter: {}", filters.toString(), e);
                    throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, e);
                }
            }
        }
        return whereClause;
    }

    /**
     * sort 파라미터 처리, 없을 경우 defaultSort annotation 검색, 없을 경우 SearchField 첫번째 값으로 setting
     *
     * @param sortList ArrayList<SortDTO>
     * @return List<OrderSpecifier < String>>
     * @author GEONLEE
     * @since 2023-07-24<br />
     * 2023-07-28 GEONLEE - private to public 으로 변경<br />
     * 2023-08-11 GEONLEE - nullLast 추가<br />
     */
    public List<OrderSpecifier<String>> getOrderSpecifierList(ArrayList<DynamicSorter> sortList) {
        List<OrderSpecifier<String>> orderSpecifierList = new ArrayList<>();
        if (sortList != null) {
            if (sortList.size() > 0) {
                for (DynamicSorter dynamicSorter : sortList) {
                    try {
                        Optional<SortOrder> order = SortOrder.valueOfOrder(dynamicSorter.dir());
                        StringPath pathBuilder = this.pathBuilder.getString(this.serializedMap.get(dynamicSorter.field()));
                        if (order.isEmpty()) {
                            if (dynamicSorter.dir() == null) {
                                LOGGER.error("The parameter sort's key is invalid. Check if any of the fields are 'null'. -> {}"
                                        , dynamicSorter);
                            } else {
                                LOGGER.error("There is no '{}' dir to sorting. Check the 'sort' parameter in searchRequest."
                                        , dynamicSorter.dir());
                            }
                            throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, null);
                        }
                        OrderSpecifier<String> orderSpecifier;
                        switch (order.get()) {
                            case ASC -> {
                                orderSpecifier = pathBuilder.asc().nullsLast();
                                orderSpecifierList.add(orderSpecifier);
                            }
                            case DESC -> {
                                orderSpecifier = pathBuilder.desc().nullsLast();
                                orderSpecifierList.add(orderSpecifier);
                            }
                            default -> {
                            }
                        }
                    } catch (NullPointerException e) {
                        if (dynamicSorter.field() == null) {
                            LOGGER.error("The parameter sort's key is invalid. Check if any of the fields are 'null'. -> {}"
                                    , dynamicSorter);
                        } else {
                            LOGGER.error("There is no '{}' field to sorting in {} entity."
                                    , dynamicSorter.field(), this.entityClass.getSimpleName());
                        }
                        throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
                    }
                }
                return orderSpecifierList;
            }
        }
        /*Sort parameter 없는경우 Default sort 추가*/
        DefaultSort ds = this.entityClass.getAnnotation(DefaultSort.class);

        if (ds != null) {
            String[] cn = ds.columnName();
            SortOrder[] so = ds.dir();
            String fieldName = "";
            for (int i = 0, n = cn.length; i < n; i++) {
                try {
                    OrderSpecifier<String> orderSpecifier;
                    if (cn[i].contains(".")) {
                        String[] cnArray = cn[i].split("\\.");
                        fieldName = cnArray[cnArray.length - 1];
                    } else {
                        fieldName = cn[i];
                    }
                    StringPath pathBuilder = this.pathBuilder.getString(this.serializedMap.get(fieldName));
                    switch (so[i]) {
                        case ASC -> {
                            orderSpecifier = pathBuilder.asc();
                            orderSpecifierList.add(orderSpecifier);
                        }
                        case DESC -> {
                            orderSpecifier = pathBuilder.desc();
                            orderSpecifierList.add(orderSpecifier);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.error("Check @DefaultSort in {} class. Invalid index.", this.entityClass.getSimpleName());
                    throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
                } catch (NullPointerException e) {
                    LOGGER.error("There is no '{}' field to sorting in {} entity. Check the @DefaultSort in this class."
                            , fieldName, this.entityClass.getName());
                    throw new ServiceException(CommonErrorCode.NULL_POINTER, e);
                }
            }
        } else {
            /*default sort도 없는 경우 SearchField의 첫번 째 값으로 asc sort 추가*/
            Field[] fields = this.entityClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.getAnnotation(SearchField.class) != null) {
                    OrderSpecifier<String> orderSpecifier;
                    SearchField sf = f.getAnnotation(SearchField.class);
                    String[] columnNames = sf.columnName();
                    String field = columnNames[0];
                    String fieldName = "";
                    if (field.contains(".")) {
                        String[] cnArray = field.split("\\.");
                        fieldName = cnArray[cnArray.length - 1];
                    } else {
                        fieldName = field;
                    }
                    StringPath pathBuilder = this.pathBuilder.getString(this.serializedMap.get(fieldName));
                    orderSpecifier = pathBuilder.asc();
                    orderSpecifierList.add(orderSpecifier);
                    break;
                }
            }
        }
        return orderSpecifierList;
    }

    /**
     * depth 없는 Path를 얻기 위한 직렬화 Map을 생성하는 메서드
     *
     * @author GEONLEE
     * @since 2023-07-25<br />
     * 2023-08-04 GEONLEE - 타입에 따라 cast 하기 위한 fieldTypeMap 데이터 생성 기능 추가<br />
     * 2024-02-19 GEONLEE - 현재 Entity 에 한번의 조인 필드만 접근 가능한 문제 발생 -> 조인에 상관 없이 동작 하도록 수정 필요<br />
     * 2024-04-01 GEONLEE - BaseEntity 사용에 따른 컬럼 정보 추가<br />
     */
    private void setSerializedMap() {
        Field[] fields = this.entityClass.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation a : annotations) {
                if (a instanceof SearchField sf) {
                    String[] columnNames = sf.columnName();
                    for (String columnName : columnNames) {
                        if (!columnName.contains(".")) {
                            this.serializedMap.put(columnName, columnName);
                            this.fieldTypeMap.put(columnName, field.getType().getSimpleName());
                        } else {
                            String[] searchColumn = columnName.split("\\.");
                            String searchFieldName = searchColumn[searchColumn.length - 1];
                            this.serializedMap.put(searchFieldName, columnName);
                            String type = getFieldType(field.getType(), searchFieldName);
                            this.fieldTypeMap.put(searchFieldName, type);
                        }
                    }
                }
            }
        }
        /*add baseEntity field*/
        if (this.entityClass.getSuperclass() == BaseEntity.class) {
            Field[] baseEntityFields = BaseEntity.class.getDeclaredFields();
            for (Field field : baseEntityFields) {
                this.serializedMap.put(field.getName(), field.getName());
                this.fieldTypeMap.put(field.getName(), field.getType().getSimpleName());
            }
        }
    }

    /**
     * 조회 시 type 에 따라 casting 되도록 DTO class 의 Type 을 리턴 해주는 메서드
     *
     * @author GEONLEE
     * @since 2024-02-19<br />
     * 2024-03-08 GEONLEE - 기존 패키지 명으로 재귀 반복하던 부분을 java. jakarta. 로 변경
     */
    private String getFieldType(Class<?> clazz, String targetName) {
        for (Field subField : clazz.getDeclaredFields()) {
            if (subField.getName().equals(targetName)) {
                return subField.getType().getSimpleName();
            }
        }
        for (Field subField : clazz.getDeclaredFields()) {
            Class<?> subClass = subField.getType();
            if (!subClass.getPackageName().contains("java.") || !subClass.getPackageName().contains("jakarta.")) {
                String type = getFieldType(subClass, targetName);
                if (type != null) return type;
            }
        }
        return null;
    }

    /**
     * 엔티티에서 Id를 추출하는 메서드
     *
     * @return ID
     * @author GEONLEE
     * @since 2023-08-02
     */
    @SuppressWarnings("unchecked")
    private ID getEntityId(T entity) throws IllegalAccessException {
        Field[] entityFields = entity.getClass().getDeclaredFields();
        for (Field entityField : entityFields) {
            entityField.setAccessible(true);
            if (entityField.isAnnotationPresent(Id.class) || entityField.isAnnotationPresent(EmbeddedId.class)) {
                return (ID) entityField.get(entity);
            }
        }
        return null;
    }

    /**
     * entity 의 SaveLocalDateTime, UpdateLocalDateTime annotation 이 있는 필드에 현재 시간 추가 메서드
     *
     * @param newEntity 현재시간을 추가할 entity
     * @author lch
     * @since 2023-07-28<br />
     * 2023-09-14 YS Lim LocalDate 타입도 처리할 수 있도록 수정<br />
     */
    private void setLocalDateTime(T newEntity) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Class<?> clazz = newEntity.getClass();
            for (Field entityField : clazz.getDeclaredFields()) {
                entityField.setAccessible(true);

                /* 일반 필드 부분. */
                if (entityField.isAnnotationPresent(SaveLocalDateTime.class)) {
                    if (entityField.get(newEntity) == null) {
                        setFieldValue(entityField, newEntity, now);
                    }
                }
                if (entityField.isAnnotationPresent(UpdateLocalDateTime.class)) {
                    setFieldValue(entityField, newEntity, now);
                }

                /* 복합키 부분. */
                if (entityField.isAnnotationPresent(EmbeddedId.class)) {
                    Object keyEntity = entityField.get(newEntity);  // 어쩌구 KEY.
                    for (Field keyField : entityField.getType().getDeclaredFields()) {
                        keyField.setAccessible(true);
                        if (keyField.isAnnotationPresent(SaveLocalDateTime.class)) {
                            setFieldValue(keyField, keyEntity, now);
                        }
                        if (keyField.isAnnotationPresent(UpdateLocalDateTime.class)) {
                            setFieldValue(keyField, keyEntity, now);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("Save time error. -> ''");
        }
    }

    /**
     * 엔티티 필드 타입이 LocalDate 인 경우 LocalDate 타입의 현재시간으로 set
     *
     * @author YS Lim
     * @since 2023-09-14<br />
     */
    private void setFieldValue(Field field, Object entity, LocalDateTime value) throws IllegalAccessException {
        String fieldType = field.getType().getSimpleName();
        if ("LocalDate".equals(fieldType)) {
            field.set(entity, value.toLocalDate());
        } else {
            field.set(entity, value);
        }
    }

    /**
     * DTO 를 List<FilterDTO> 로 변환해주는 메서드, eq 조건으로 추가
     *
     * @author GEONLEE
     * @since 2023-08-02<br />
     */
    private ArrayList<DynamicFilter> convertToFilter(Object parameter) {
        ArrayList<DynamicFilter> filters = new ArrayList<>();
        Field[] fields = parameter.getClass().getDeclaredFields();
        for (Field field : fields) {
            String searchField = field.getName();
            String searchOperator = "eq";
            field.setAccessible(true);
            try {
                if (field.get(parameter) != null) {
                    filters.add(DynamicFilter.builder()
                            .field(searchField)
                            .operator(searchOperator)
                            .value(String.valueOf(field.get(parameter)))
                            .build());
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to convert parameter({}) to filterDTO.", parameter.getClass().getSimpleName());
                throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
            }
        }
        return filters;
    }

    /**
     * 필드 타입에 따라 형변환된 value 갑을 리턴하는 메서드
     *
     * @author GEONLEE
     * @since 2023-08-04<br />
     */
    private Object getCastValue(DynamicFilter filter) {
        String type = this.fieldTypeMap.get(filter.field());
        return switch (type) {
            case "Integer" -> Integer.valueOf(filter.value());
            case "Long" -> Long.valueOf(filter.value());
            case "Double" -> Double.valueOf(filter.value());
            case "LocalDateTime" -> AljjabaegiDate.dateTimeStringToLocalDateTime(filter.value(), "yyyyMMddHHmmss");
            case "localDate" -> AljjabaegiDate.dateStringToLocalDate(filter.value(), "yyyyMMdd");
            default -> filter.value();
        };
    }

    /**
     * dateString 매개변수를 받아 날짜 조회 조건을 생성하는 메서드
     * 날짜 컬럼 >= A and 날짜 컬럼 < B
     *
     * @param pathBuilder column 정보가 있는 pathBuilder
     * @param date        콤마가 있거나 없는 dateString ('20231006' or '20231006,20231007')
     * @author GEONLEE
     */
    private BooleanExpression getDateWhereExpression(PathBuilder<Object> pathBuilder, String date) throws IllegalArgumentException {
        List<LocalDateTime> betweenDate = AljjabaegiDate.getComputeRangeList(date);
        return Expressions.booleanOperation(Ops.GOE
                        , pathBuilder, Expressions.constant(betweenDate.get(0)))
                .and(Expressions.booleanOperation(Ops.LT
                        , pathBuilder, Expressions.constant(betweenDate.get(1))));
    }

    /**
     * 조회 쿼리 로그 출력 메서드
     *
     * @author GEONLEE
     * @since 2023-08-04<br />
     */
    private void printQuery(String query) {
        LOGGER.info("Request Query ▼\n" + query);
    }

    /**
     * dto 를 entity 로 변환하여 리턴하는 메서드
     *
     * @param parameter 매개변수
     * @param mapper    mapper class
     * @author GEONLEE
     * @since 2023-08-02<br />
     * 2023-08-10 GEONLEE - getEntityByDto 에서 dtoToEntity 로 메서드명 변경<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    private T dtoToEntity(Object parameter, Class<?> mapper) throws InvocationTargetException, IllegalAccessException {
        Object mapperInstance = Mappers.getMapper(mapper);
        Method toEntity;
        try {
            toEntity = mapperInstance.getClass().getDeclaredMethod("toEntity", parameter.getClass());
        } catch (NoSuchMethodException e) {
            LOGGER.error("There is no 'toEntity' method in {}({}) mapper."
                    , mapperInstance.getClass().getSimpleName(), parameter.getClass().getSimpleName());
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        }
        return (T) toEntity.invoke(mapperInstance, parameter);
    }

    /**
     * dtoToEntity 한 entity 로 키를 추출해 영속성 컨텍스트의 데이터를 조회
     *
     * @return Optional<T>
     * @author GEONLEE
     * @since 2023-08-02<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Deprecated
    private Optional<T> getEntityFromPersistenceContext(T entity) {
        ID id;
        try {
            id = getEntityId(entity);
        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to extract ID from entity. -> {}", entity.getClass().getSimpleName());
            throw new ServiceException(CommonErrorCode.INVALID_PARAMETER, e);
        }
        if (id == null) {
            return Optional.empty();
        } else {
            return findById(id);
        }
    }

    /**
     * 파라미터를 받아 파라미터에 있는 필드로 @id 혹은 @EmbeddedId 객체를 생성해서 리턴하는 메서드
     *
     * @param parameter Object DTO
     * @return Object (key)
     * @author GEONLEE
     * @since 2023-07-25<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    private ID getEntityIdFromDto(Object parameter) {
        try {
            Field[] entityFields = this.entityClass.getDeclaredFields();
            for (Field entityField : entityFields) {
                entityField.setAccessible(true);
                if (entityField.isAnnotationPresent(Id.class)) {
                    String idFieldName = entityField.getName();
                    Field dtoField = parameter.getClass().getDeclaredField(idFieldName);
                    dtoField.setAccessible(true);
                    return (ID) dtoField.get(parameter);
                } else if (entityField.isAnnotationPresent(EmbeddedId.class)) {
                    Class<?> embeddedIdClass = entityField.getType();
                    Constructor<?> constructor = embeddedIdClass.getDeclaredConstructor();
                    Object embeddedId = constructor.newInstance();
                    Field[] embeddedFields = embeddedId.getClass().getDeclaredFields();

                    for (Field embeddedField : embeddedFields) {
                        embeddedField.setAccessible(true);
                        String embeddedFieldName = embeddedField.getName();

                        Field dtoField = parameter.getClass().getDeclaredField(embeddedFieldName);
                        dtoField.setAccessible(true);

                        Object value = dtoField.get(parameter);
                        embeddedField.set(embeddedId, value);
                    }
                    return (ID) embeddedId;
                }
            }
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InstantiationException
                 | IllegalAccessError | InvocationTargetException e) {
            LOGGER.error("getEntityId Method Error", e);
        }
        return null;
    }

    /**
     * 리플렉션을 활용하여 동적인 insert 작업을 수행한다.
     *
     * @param parameter Object
     * @param mapper    mapper class
     * @author GEONLEE
     * @since 2023-07-27<br />
     * 2023-08-02 GEONLEE - 불필요 형변환 제거<br />
     * 2023-08-02 GEONLEE - parameter 를 entity 로 변경 후 키 체크를 하는 방식으로 변경 (key에 date type이 있을 경우 키값 추출 문제 때문)<br />
     * - getEntityByDto, getEntityByPersistenceContext 분기<br />
     * 2023-10-11 GEONLEE - return void로 변경하고 NoResultException 발생 시키도록 변경<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Override
    @Transactional
    @Deprecated
    public void insertDynamic(Object parameter, Class<?> mapper) throws ServiceException {
        T newEntity;
        try {
            newEntity = dtoToEntity(parameter, mapper);
            Optional<T> optionalEntity = getEntityFromPersistenceContext(newEntity);
            if (optionalEntity.isEmpty()) {
                setLocalDateTime(newEntity);
                save(newEntity);
            } else {
                LOGGER.error("There is data to insert already. -> '{}'", newEntity.getClass().getSimpleName());
                throw new DataIntegrityViolationException("There is data to insert already.");
            }
        } catch (IllegalAccessException | InvocationTargetException | ServiceException | NoResultException e) {
            LOGGER.error("Fail to convert dto to entity. {} -> {}"
                    , parameter.getClass().getSimpleName(), this.entityClass.getSimpleName());
            throw new ServiceException(CommonErrorCode.LOGIC_ERROR, e);
        }
    }

    /**
     * 리플렉션을 활용하여 동적인 update 작업을 수행한다.
     *
     * @author GEONLEE
     * @since 2023-07-27<br />
     * 2023-07-31 GEONLEE - dto to entity 로 save 하는 방식으로 변경, 기존에는 entity 값에 update 후 save.<br />
     * (필드가 빠질경우 null 로 update 되기 떄문, dto to entity 방식은 field 가 빠진경우 null 로 update 됨.)<br />
     * 2023-08-02 GEONLEE - parameter 를 entity 로 변경 후 키 체크를 하는 방식으로 변경 (key에 date type이 있을 경우 키값 추출 문제 때문)<br />
     * - getEntityByDto, getEntityByPersistenceContext 분기<br />
     * 2023-08-10 GEONLEE - updateFromDto 사용 방식으로 변경<br />
     * 2023-10-11 GEONLEE - return void로 변경하고 NoResultException 발생 시키도록 변경<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Override
    @Transactional
    @Deprecated
    public void updateDynamic(Object dto, Class<?> mapper) {
        try {
            T newEntityCreateWithDto = dtoToEntity(dto, mapper);
            Optional<T> optionalEntity = getEntityFromPersistenceContext(newEntityCreateWithDto);
            if (optionalEntity.isPresent()) {
                updateFromDto(dto, mapper, optionalEntity.get());
            } else {
                LOGGER.error("There is no entity to update. -> '{}'", newEntityCreateWithDto.getClass().getSimpleName());
                throw new DataIntegrityViolationException("There is no entity to update.");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Fail to update from dto. {} -> {}"
                    , dto.getClass().getSimpleName(), this.entityClass.getSimpleName());
            throw new ServiceException(CommonErrorCode.LOGIC_ERROR, e);
        }
    }

    /**
     * update 시 null 이 아닌 데이터만 update 처리한다.
     *
     * @param parameter DTO
     * @param mapper    Mapper
     * @param entity    Entity
     * @author GEONLEE
     * @since 2023-08-10<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Deprecated
    private void updateFromDto(Object parameter, Class<?> mapper, T entity) throws InvocationTargetException, IllegalAccessException {
        Object mapperInstance = Mappers.getMapper(mapper);
        Method updateFromDto;
        try {
            updateFromDto = mapperInstance.getClass().getDeclaredMethod("updateFromDto", parameter.getClass(), entity.getClass());
            updateFromDto.invoke(mapperInstance, parameter, entity);
        } catch (NoSuchMethodException e) {
            LOGGER.error("There is no 'updateFromDto' method in {}({}) mapper."
                    , mapperInstance.getClass().getSimpleName(), parameter.getClass().getSimpleName(), e);
            throw new ServiceException(CommonErrorCode.LOGIC_ERROR, e);
        }
    }

    /**
     * 리플렉션을 활용하여 동적인 single, multi delete 작업을 수행한다.
     *
     * @param parameter Object
     * @param mapper    mapper class
     * @author GEONLEE
     * @since 2023-07-27<br />
     * 2023-07-28 GEONLEE - 리턴타입 boolean 으로 변경<br />
     * 2023-08-02 GEONLEE - 불필요 형변환 제거<br />
     * - 키에 날짜 타입이 있을 경우 entity 로 변환 후 delete 하도록 개선<br />
     * 2023-10-11 GEONLEE - return void로 변경하고 NoResultException 발생 시키도록 변경<br />
     * 2024-03-11 GEONLEE - @Deprecated<br />
     */
    @Override
    @Transactional
    @Deprecated
    public void deleteDynamic(Object parameter, Class<?> mapper) throws ServiceException {
        if (parameter instanceof Set<?> deleteList) {
            Set<T> entityList = new HashSet<>();
            for (Object delDto : deleteList) {
                try {
                    ID entityId = getEntityIdFromDto(delDto);
                    if (entityId != null) {
                        Optional<T> optionalEntity = findById(entityId);
                        optionalEntity.ifPresent(entityList::add);
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        T curEntity = dtoToEntity(parameter, mapper);
                        Optional<T> optionalEntity = getEntityFromPersistenceContext(curEntity);
                        optionalEntity.ifPresent(entityList::add);
                    } catch (InvocationTargetException | IllegalAccessException ex) {
                        throw new ServiceException(CommonErrorCode.LOGIC_ERROR, e);
                    }
                }
            }
            if (entityList.size() == 0) {
                throw new DataIntegrityViolationException("There is no data to delete. Check the key value of parameter.");
            } else if (deleteList.size() != entityList.size()) {
                LOGGER.error("There are {}(ea) data to delete. Check the key value of parameter."
                        , deleteList.size() - entityList.size());
                throw new DataIntegrityViolationException("The deletion request and the number of data to be deleted do not match.");
            } else {
                deleteAll(entityList);
            }
        } else {
            try {
                ID entityId = getEntityIdFromDto(parameter);
                if (entityId != null) {
                    Optional<T> optionalEntity = findById(entityId);
                    if (optionalEntity.isPresent()) {
                        optionalEntity.ifPresent(this::delete);
                    } else {
                        LOGGER.error("There is no entity to delete. Check the key value of parameter.");
                        throw new DataIntegrityViolationException("There is no data to delete. Check the key value of parameter.");
                    }
                }
            } catch (IllegalArgumentException e) {
                try {
                    T curEntity = dtoToEntity(parameter, mapper);
                    Optional<T> optionalEntity = getEntityFromPersistenceContext(curEntity);
                    if (optionalEntity.isPresent()) {
                        optionalEntity.ifPresent(this::delete);
                    } else {
                        LOGGER.error("There is no data to delete. Check the key value of parameter.");
                        throw new DataIntegrityViolationException("There is no data to delete. Check the key value of parameter.");
                    }
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    throw new ServiceException(CommonErrorCode.LOGIC_ERROR, e);
                }
            }
        }
    }
}