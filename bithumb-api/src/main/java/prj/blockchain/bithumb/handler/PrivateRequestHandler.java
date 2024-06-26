package prj.blockchain.bithumb.handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import prj.blockchain.bithumb.dto.PrivateRequestDto;
import prj.blockchain.bithumb.util.ApiClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
public class PrivateRequestHandler {
    private final ApiClient apiClient;
    private final String USER_TRANSACTIONS_URL;
    private final String USER_INFO_URL;
    private final String USER_ORDER_URL;
    private final String USER_ORDER_DETAIl_URL;

    public PrivateRequestHandler(ApiClient apiClient,
                                 @Value("${url.bithumb.user.transactions}") String userTransactionsUrl,
                                 @Value("${url.bithumb.user.info}") String userInfoUrl,
                                 @Value("${url.bithumb.user.orders}") String userOrderUrl,
                                 @Value("${url.bithumb.user.order-detail}") String userOrderDetailUrl) {
        this.apiClient = apiClient;
        this.USER_TRANSACTIONS_URL = userTransactionsUrl;
        this.USER_ORDER_URL = userOrderUrl;
        this.USER_ORDER_DETAIl_URL = userOrderDetailUrl;
        this.USER_INFO_URL = userInfoUrl;
    }

    public Mono<ServerResponse> getAccountInfo(ServerRequest request) {
        PrivateRequestDto requestDto = PrivateRequestDto.fromRequest(request);
        HashMap<String, String> params = new HashMap<>();
        params.put("order_currency", requestDto.getOrderCurrency());
        apiClient.setAccessInfo(requestDto.getApiKey(), requestDto.getSecretKey());
        String response = apiClient.callApi(USER_INFO_URL, params);
        return ServerResponse.ok().body(Mono.just(response), String.class);
    }


    public Mono<ServerResponse> getTransactions(ServerRequest request) {
        PrivateRequestDto requestDto = PrivateRequestDto.fromRequest(request);
        HashMap<String, String> params = new HashMap<>();
        params.put("order_currency", requestDto.getOrderCurrency());
        params.put("payment_currency", requestDto.getOrderCurrency());
        apiClient.setAccessInfo(requestDto.getApiKey(), requestDto.getSecretKey());
        String response = apiClient.callApi(USER_TRANSACTIONS_URL, params);
        return ServerResponse.ok().body(Mono.just(response), String.class);
    }

    public Mono<ServerResponse> getOrderList(ServerRequest request) {
        PrivateRequestDto requestDto = PrivateRequestDto.fromRequest(request);
        String cryptocurrency = request.pathVariable("cryptocurrency");
        HashMap<String, String> params = new HashMap<>();
        params.put("order_currency", cryptocurrency);
        apiClient.setAccessInfo(requestDto.getApiKey(), requestDto.getSecretKey());
        String response = apiClient.callApi(USER_ORDER_URL, params);
        return ServerResponse.ok().body(Mono.just(response), String.class);
    }

    public Mono<ServerResponse> getOrderDetail(ServerRequest request) {
        PrivateRequestDto requestDto = PrivateRequestDto.fromRequest(request);
        String cryptocurrency = request.pathVariable("cryptocurrency");
        String orderId = request.pathVariable("orderid");
        HashMap<String, String> params = new HashMap<>();
        params.put("order_id", orderId);
        params.put("order_currency", cryptocurrency);
        apiClient.setAccessInfo(requestDto.getApiKey(), requestDto.getSecretKey());
        String response = apiClient.callApi(USER_ORDER_DETAIl_URL, params);
        return ServerResponse.ok().body(Mono.just(response), String.class);
    }
}
