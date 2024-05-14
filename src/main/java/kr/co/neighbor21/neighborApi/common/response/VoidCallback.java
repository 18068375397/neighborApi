package kr.co.neighbor21.neighborApi.common.response;

/**
 * CommonResponse 에서 쓰이는 Callback 객체. Callable 과 다르게 return void 이다.<br />
 *
 * @author jisu
 * @since 2023-08-07
 */
@FunctionalInterface
@Deprecated
public interface VoidCallback {
    void call();
}
