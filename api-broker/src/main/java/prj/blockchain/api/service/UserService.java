package prj.blockchain.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.blockchain.api.config.BithumbApiConfig;
import prj.blockchain.api.dto.UserDto;
import prj.blockchain.api.exception.BadRequestException;
import prj.blockchain.api.model.User;
import prj.blockchain.api.repository.UserRepository;
import prj.blockchain.api.util.CryptoUtil;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final BithumbApiConfig bithumbApiConfig;
    private final UserRepository userRepository;

    @Transactional
    public UserDto addUser(UserDto userDto) {
        try {
            SecretKey secretKey = bithumbApiConfig.getSecretKey();
            String encryptedApiKey = CryptoUtil.encrypt(userDto.getApiKey(), secretKey); // 사용자 키 암호화
            String encryptedApiSecret = CryptoUtil.encrypt(userDto.getApiSecret(), secretKey); // 사용자 secret 암호화
            userDto.setApiKey(encryptedApiKey);
            userDto.setApiSecret(encryptedApiSecret);
            return UserDto.fromEntity(userRepository.save(new User(userDto)));
        }catch (Exception e) {
            log.error(e.getMessage());
            return new UserDto();
        }
    }

    @Transactional
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found"));
        user.deActivate();
        return UserDto.fromEntity(user);
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUser() { return userRepository.findAll();}
}
