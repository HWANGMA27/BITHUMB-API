package prj.blockchain.bithumb.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import prj.blockchain.bithumb.handler.PublicRequestHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class PublicRequestRouter {

    @Bean
    public RouterFunction<ServerResponse> tickerInfoRoute(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/ticker/{cryptocurrency}"), requestHandler::getTicker);
    }

    @Bean
    public RouterFunction<ServerResponse> statusRoute(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/status/{cryptocurrency}"), requestHandler::getStatus);
    }

    @Bean
    public RouterFunction<ServerResponse> networkInfoRoute(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/status/{cryptocurrency}"), requestHandler::getNetworkStatus);
    }

    @Bean
    public RouterFunction<ServerResponse> orderBookoRoute(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/orderbook/{cryptocurrency}/{paymentCurrency}"), requestHandler::getOrderBook);
    }

    @Bean
    public RouterFunction<ServerResponse> currencyPriceRoute(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/price/{cryptocurrency}/{paymentCurrency}"), requestHandler::getOrderBook);
    }

    @Bean
    public RouterFunction<ServerResponse> currencyWithdrawMin(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/withdraw/{cryptocurrency}/min"), requestHandler::getMinWithdrawAmount);
    }

    @Bean
    public RouterFunction<ServerResponse> networkInfo(PublicRequestHandler requestHandler) {
        return RouterFunctions.route(GET("/networks/info"), requestHandler::getNetworkInfo);
    }
}
