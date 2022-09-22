import java.io.Serializable;
import java.util.List;

public class Request extends BaseRequest implements Serializable {
    private Integer serverId;
    private Listener listener;
    private Long turnAroundStartTime;
    private Long waitingStartTime;
    private Long executionStartTime;
    private boolean isCacheEnabledInClient;

    public Request() {
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

    public boolean isCacheEnabledInClient() {
        return isCacheEnabledInClient;
    }

    public void setCacheEnabledInClient(boolean cacheEnabledInClient) {
        isCacheEnabledInClient = cacheEnabledInClient;
    }
}
