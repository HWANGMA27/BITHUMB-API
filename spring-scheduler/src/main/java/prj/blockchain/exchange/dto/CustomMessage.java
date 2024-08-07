package prj.blockchain.exchange.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomMessage {

    private String className;

    private String methodName;

    @Override
    public String toString() {
        return "CustomMessage{" +
                "text='" + className + '\'' +
                ", priority=" + methodName +
                '}';
    }

}