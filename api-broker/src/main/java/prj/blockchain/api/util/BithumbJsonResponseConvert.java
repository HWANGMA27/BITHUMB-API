package prj.blockchain.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import prj.blockchain.api.model.*;
import prj.blockchain.api.model.Currency;
import prj.blockchain.api.repository.NetworkRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class BithumbJsonResponseConvert {
    private final NetworkRepository networkRepository;
    private final ObjectMapper objectMapper;
    private final Exchange exchange = Exchange.BITHUMB;

    public List<Network> networkResponseMapping(String response) {
        System.out.println(response);
        List<Network> networkList = new ArrayList<>();
        try {
            log.info(this.getClass() + " executed");
            JsonNode root = objectMapper.readTree(response);
            if (root.has("data")) {
                JsonNode dataArray = root.get("data");
                if (dataArray.isArray()) {
                    for (JsonNode dataNode : dataArray) {
                        Network network = objectMapper.treeToValue(dataNode, Network.class);
                        network.updateExchangeInfo(exchange);
                        networkList.add(network);
                    }
                }
                networkList.forEach(network -> log.info("Network: " + network.getNetwork() + ", Network: " + network.getCurrency() + ", DepositStatus: "
                        + network.getDepositStatus() + ", WithdrawalStatus: " + network.getWithdrawalStatus()));
            }
            log.info(this.getClass() + " finished");
        } catch (IOException e) {
            log.error(this.getClass() + " error msg : " + e.getMessage());
        }
        return networkList;
    }

    public List<BalanceHistory> balanceResponseMapping(User user, String currency, String response) {
        log.info(response);
        String currencyLowercase = currency.toLowerCase(Locale.ROOT);
        Set<String> currencies;
        if(!currencyLowercase.equals("all")){
            currencies = new HashSet<>();
            currencies.add(currencyLowercase);
        } else {
            List<String> allNetworks = networkRepository.findDistinctCurrencyByExchange(exchange);
            currencies = allNetworks.stream().map(c -> c.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
        }

        List<BalanceHistory> balanceHistoryList = new ArrayList<>();
        try {
            log.info(this.getClass() + " executed");
            JsonNode root = objectMapper.readTree(response);
            if (root.has("data")) {
                JsonNode dataNode = root.get("data");
                for (String targetCurrency : currencies) {
                    if(dataNode.path("total_".concat(targetCurrency)).asText().equals("") || Objects.isNull(dataNode.path("total_".concat(targetCurrency)).asText())){
                        log.warn(targetCurrency + " : can not found data");
                    } else {
                        BigDecimal currencyTotal = new BigDecimal(dataNode.path("total_".concat(targetCurrency)).asText());
                        if (currencyTotal.compareTo(BigDecimal.ZERO) > 0) {
                            BalanceHistory balanceHistory = BalanceHistory.builder()
                                    .user(user)
                                    .exchange(exchange)
                                    .totalAsset(new BigDecimal(dataNode.get("total_".concat(targetCurrency)).asText()))
                                    .inUseAsset(new BigDecimal(dataNode.get("in_use_".concat(targetCurrency)).asText()))
                                    .availableAsset(new BigDecimal(dataNode.get("available_".concat(targetCurrency)).asText()))
                                    .assetType(targetCurrency).build();
                            balanceHistoryList.add(balanceHistory);
                            balanceHistoryList.forEach(balanceHistoryItem -> log.info("BalanceHistory " + targetCurrency + " : " + balanceHistoryItem.getTotalAsset()));
                        }
                    }
                }
            }
            log.info(this.getClass() + " finished");
        } catch (IOException e) {
            log.error(this.getClass() + " error msg : " + e.getMessage());
        }
        return balanceHistoryList;
    }

    public List<Currency> currencyResponseMapping(String response) {
        System.out.println(response);
        List<Currency> currencyList = new ArrayList<>();
        try {
            log.info(this.getClass() + " executed");
            JsonNode root = objectMapper.readTree(response);
            if (root.has("data")) {
                JsonNode dataArray = root.get("data");
                if (dataArray.isArray()) {
                    for (JsonNode dataNode : dataArray) {
                        Currency currency = objectMapper.treeToValue(dataNode, Currency.class);
                        currency.updateExchangeInfo(exchange);
                        currencyList.add(currency);
                    }
                }
                currencyList.forEach(network -> log.info("Network: " + network.getNetwork() + ", Currency: " + network.getCurrency()));
            }
            log.info(this.getClass() + " finished");
        } catch (IOException e) {
            log.error(this.getClass() + " error msg : " + e.getMessage());
        }
        return currencyList;
    }
}
