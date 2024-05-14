package kr.co.neighbor21.neighborApi.common.response;

/**
 * GenerateResponse 에서 쓰이는 Callback 객체.<br />
 * VoidCallback 과 다르게 return 이 있다.<br />
 *
 * @param <V> the result type of method call
 * @author jisu
 * @since 2023-08-30<br />
 */
@FunctionalInterface
public interface Callback<V> {
    V call();
}
