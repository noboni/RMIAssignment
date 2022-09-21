import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    private String methodName;
    private List<String> parameters;
    private Integer zone;
    private Integer serverId;
    private Listener listener;
    private Long turnAroundStartTime;
    private Long waitingStartTime;
    private Long executionStartTime;

    public Request() {
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

    public int getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Long getTurnAroundStartTime() {
        return turnAroundStartTime;
    }

    public void setTurnAroundStartTime(Long turnAroundStartTime) {
        this.turnAroundStartTime = turnAroundStartTime;
    }

    public Long getWaitingStartTime() {
        return waitingStartTime;
    }

    public void setWaitingStartTime(Long waitingStartTime) {
        this.waitingStartTime = waitingStartTime;
    }

    public Long getExecutionStartTime() {
        return executionStartTime;
    }

    public void setExecutionStartTime(Long executionStartTime) {
        this.executionStartTime = executionStartTime;
    }
}
