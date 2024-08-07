package prj.blockchain.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import prj.blockchain.api.model.User;

@Data
@NoArgsConstructor
public class UserDto {
    private String nickName;
    private String apiKey;
    private String apiSecret;
    private boolean isActive;

    @Builder
    public UserDto(String nickName, String apiKey, String apiSecret, boolean isActive){
        this.nickName = nickName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.isActive = isActive;
    }

    public static UserDto fromEntity(User user){
        return UserDto.builder()
                .nickName(user.getNickName())
                .build();
    }
}
