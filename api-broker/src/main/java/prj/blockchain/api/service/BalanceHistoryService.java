package prj.blockchain.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import prj.blockchain.api.config.BithumbApiProperties;
import prj.blockchain.api.config.BithumbApiConfig;
import prj.blockchain.api.dto.CustomMessage;
import prj.blockchain.api.exception.DecryptFailException;
import prj.blockchain.api.exception.NotFoundQueueException;
import prj.blockchain.api.model.BalanceHistory;
import prj.blockchain.api.model.User;
import prj.blockchain.api.repository.BalanceHistoryRepository;
import prj.blockchain.api.util.CryptoUtil;
import prj.blockchain.api.util.BithumbJsonResponseConvert;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class BalanceHistoryService {
    private final BithumbApiConfig bithumbApiConfig;
    private final WebClient webClient;
    private final BithumbApiProperties bithumbApiProperties;
    private final BithumbJsonResponseConvert jsonResponseConvert;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final SlackMsgService slackMsgService;

    public void saveUserBalanceHistory(User user, String currency) {
        String action = "Save";
        String targetTable = "BalanceHistory";
        CustomMessage customMessage = CustomMessage.builder()
                .action(action)
                .targetTable(targetTable)
                .apiName(bithumbApiConfig.getName())
                .build();
        try {
            fetchUserBalance(user, currency)
                    .flatMap(balanceResponse -> {
                        return Mono.fromCallable(() -> {
                            balanceHistoryRepository.saveAll(balanceResponse);
                            return balanceResponse;
                        });
                    })
                    .doOnSuccess(balanceResponse -> {
                        customMessage.setSuccess(true);
                        customMessage.setAffectedData(String.valueOf(balanceResponse.size()));
                        slackMsgService.sendMessage(customMessage, slackMsgService.getSlackQueueTask().getRoutingKey().get("alert"));
                        log.info(customMessage.toString());
                    })
                    .doOnError(e -> {
                        customMessage.setSuccess(false);
                        slackMsgService.sendMessage(customMessage, slackMsgService.getSlackQueueTask().getRoutingKey().get("warning"));
                        log.error(customMessage.toString());
                    })
                    .subscribe();
        } catch (DecryptFailException e) {
            log.error("Error get user key :" + e.getMessage());
        } catch (NotFoundQueueException e) {
            log.error("Error while find slack message queue");
        }
    }


    public Mono<List<BalanceHistory>> fetchUserBalance(User user, String currency) throws DecryptFailException {
        String getBalanceEndPoint = String.join("/", bithumbApiProperties.getEndpoint().getBalance(), currency);

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
            SecretKey secretKey = bithumbApiConfig.getSecretKey();
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
