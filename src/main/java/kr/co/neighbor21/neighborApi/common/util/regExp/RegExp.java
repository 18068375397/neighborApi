package kr.co.neighbor21.neighborApi.common.util.regExp;

/**
 * 정규식 설정 class
 *
 * @author GEONLEE
 * @since 2023-03-06<br />
 * 2023-09-01 jisu - ONLY_ENG 추가 <br/>
 */
public class RegExp {
    public static final String ONLY_NUMBER = "[^0-9]+";
    public static final String ONLY_ENG = "[^A-Za-z]+";
    // 공백 제거
    public static final String REMOVE_BLANK = "[^가-힣a-zA-Z0-9\\\\s]";
    // 아이디: 영문, 영문+숫자, 영문+특수문자, 영문+숫자+특수문자 조합 8자 이상
    public static final String ENG_OR_NUMBER = "^[A-Za-z[0-9]]{6,}$";
    // 이메일
    public static final String ONLY_EMAIL = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
    // 영문 숫자 특수문자 8자리 이상
    public static final String ENG_NUMBER_SPECIAL = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[/[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]/g]).{8,}$";
    // 영문 특수문자 10자리 이상
    public static final String ENG_SPECIAL = "^(?=.*[A-Za-z])(?=.*[/[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]/g])[[A-Za-z]/[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]/g]{10,}$";
    // 영문 숫자 10자리 이상
    public static final String ENG_NUMBER = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}$";
    // 특수문자 숫자 10자리 이상
    public static final String NUMBER_SPECIAL = "^(?=.*\\d)(?=.*[/[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]/g])[/[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@\\#$%&\\\\\\=\\(\\'\\\"]/g\\d]{10,}$";
    // 동일한 숫자 4자리 이상
    public static final String EXCLUDE_REPEAT_NUMBER = "(\\w)\\1\\1\\1";
}
