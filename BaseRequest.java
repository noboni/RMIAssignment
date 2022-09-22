import java.io.Serializable;
import java.util.List;

public class BaseRequest implements Serializable {
    private String methodName;
    private List<String> parameters;
    private Integer zone;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }
}
