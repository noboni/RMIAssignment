import java.rmi.RemoteException;

public class ProxyServer implements ProxyServerInterface {
    public Integer getServerId(Integer zone) throws RemoteException {
        ServiceInterface server = ServerSimulator.getServerById(zone);
        int queueSize = server.getQueueSize();
        //check if overloaded with client requests
        if (queueSize <= 20) {
            return zone;
        } else {
            //Find another server in the neighbour zone
            Integer tempZone = calZone(zone, true);
            server = ServerSimulator.getServerById(tempZone);
            if (server.getQueueSize() > 20) {
                return tempZone;
            }
            tempZone = calZone(zone, false);
            server = ServerSimulator.getServerById(tempZone);
            if (server.getQueueSize() > 20) {
                return tempZone;
            }

        }
        return zone;
    }

    /**
     * Calculate the client zone in which the
     * @param zone
     * @param isFirst
     * @return
     */

    private Integer calZone(int zone, boolean isFirst) {
        //Determine neighbour zone
        Integer finalZone;
        if (isFirst) {
            finalZone = zone + 1;
        } else {
            finalZone = zone + 4;
        }
        if (finalZone > 5) {
            finalZone = finalZone % 5;
        }
        System.out.println("Zone:" + finalZone);
        return finalZone;
    }
}
