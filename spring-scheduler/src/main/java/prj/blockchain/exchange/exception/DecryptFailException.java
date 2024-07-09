package prj.blockchain.exchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DecryptFailException extends RuntimeException {
    public DecryptFailException(String message) {
        super(message);
    }
}