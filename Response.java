import java.io.Serializable;
import java.util.List;
public class Response implements Serializable {
    private int result;
    private String methodName;
    private List<String> parameters;
    private Long turnAroundTime;
    private Long executionTime;
    private Long waitingTime;
    private String serverId;

    public long getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

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

    public Long getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(Long turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
