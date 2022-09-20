public class ProxyServer implements ProxyServerInterface {
    public Integer getServerId(Integer zone) {
        Server server = ServerSimulator.getServerById(zone);
        if (server.getQueueSize() <= 20) {
            return zone;
        } else {
            Integer tempZone = calZone(zone, true);
            server = ServerSimulator.getServerById(tempZone);
            if (server.getQueueSize() <= 20) {
                return tempZone;
            }
            tempZone = calZone(zone, false);
            server = ServerSimulator.getServerById(tempZone);
            if (server.getQueueSize() <= 20) {
                return tempZone;
            }

        }
        return zone;
    }

    private Integer calZone(int zone, boolean isFirst) {
        Integer finalZone;
        if (isFirst) {
            finalZone = zone + 1;
        } else {
            finalZone = zone + 4;
        }
        if (finalZone > 5) {
            finalZone = finalZone % 5;
        }
        return finalZone;
    }
}
