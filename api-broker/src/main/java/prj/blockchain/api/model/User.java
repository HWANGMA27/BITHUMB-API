package prj.blockchain.api.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prj.blockchain.api.dto.UserDto;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;
    private String apiKey;
    private String apiSecret;
    private boolean isActive;

    @Builder
    public User(UserDto userDto) {
        this.nickName = userDto.getNickName();
        this.apiKey = userDto.getApiKey();
        this.apiSecret = userDto.getApiSecret();
        this.isActive = true;
    }

    public void deActivate() {
        this.isActive = false;
    }
}
