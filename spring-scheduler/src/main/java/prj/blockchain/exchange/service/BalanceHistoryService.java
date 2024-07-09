package prj.blockchain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import prj.blockchain.exchange.config.ApiProperties;
import prj.blockchain.exchange.config.CryptoConfig;
import prj.blockchain.exchange.exception.DecryptFailException;
import prj.blockchain.exchange.model.BalanceHistory;
import prj.blockchain.exchange.model.User;
import prj.blockchain.exchange.repository.BalanceHistoryRepository;
import prj.blockchain.exchange.util.CryptoUtil;
import prj.blockchain.exchange.util.JsonResponseConvert;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Log4j2
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BalanceHistoryService {
    private final CryptoConfig cryptoConfig;
    private final WebClient webClient;
    private final ApiProperties endPoint;
    private final JsonResponseConvert jsonResponseConvert;
    private final BalanceHistoryRepository balanceHistoryRepository;

    @Transactional
    public void saveUserBalanceHistory(User user, String currency) {
        try {
            fetchUserBalance(user, currency)
                    .flatMap(balanceResponse -> {
                        return Mono.fromCallable(() -> {
                            balanceHistoryRepository.saveAll(balanceResponse);
                            log.info("Saved " + balanceResponse.size() + " network responses to the database.");
                            return balanceResponse;
                        });
                    })
                    .onErrorResume(e -> {
                        log.error("Error saving balance history: " + e.getMessage());
                        return Mono.empty();
                    })
                    .subscribe();
        } catch (DecryptFailException e) {
            log.error("Error get user key :" + e.getMessage());
        }
    }


    public Mono<List<BalanceHistory>> fetchUserBalance(User user, String currency) throws DecryptFailException {
        String getBalanceEndPoint = String.join("/", endPoint.getBalance(), currency);

        HttpHeaders apiKeyHeaders = getApiKeyHeaders(user);
        return webClient.get()
            .uri(getBalanceEndPoint)
            .headers(httpHeaders -> httpHeaders.addAll(apiKeyHeaders))
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> jsonResponseConvert.balanceResponseMapping(user, currency, response))
            .doOnError(error -> {
                log.error("Error: " + error.getMessage());
            });
    }

    private HttpHeaders getApiKeyHeaders(User user) throws DecryptFailException {
        try {
            SecretKey secretKey = cryptoConfig.getSecretKey();
            String apiKey = CryptoUtil.decrypt(user.getApiKey(), secretKey);
            String apiSecret =  CryptoUtil.decrypt(user.getApiSecret(), secretKey);

            HttpHeaders headers = new HttpHeaders();
            headers.add("api-key", apiKey);
            headers.add("api-secret", apiSecret);
            return headers;
        } catch (Exception e) {
            throw new DecryptFailException(e.getMessage());
        }
    }

}
