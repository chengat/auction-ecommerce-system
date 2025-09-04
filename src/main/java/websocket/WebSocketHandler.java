package websocket;

import java.util.HashMap;
import java.util.Map;

public class WebSocketHandler {

    public static void sendBidUpdate(String auctionId, double newBidAmount, String bidderName) {
        String updateData = buildJsonString(Map.of(
            "type", "bid",
            "auctionId", auctionId,
            "bidAmount", newBidAmount,
            "bidder", bidderName
        ));

        WebSocket.broadcastAuctionUpdate(auctionId, updateData);
    }
    
    public static void sendPriceDecrement(String auctionId, double newDutchPrice) {
        String updateData = buildJsonString(Map.of(
            "type", "priceDecrement",
            "auctionId", auctionId,
            "newPrice", newDutchPrice
        ));

        WebSocket.broadcastAuctionUpdate(auctionId, updateData);
    }
    

    public static void sendAuctionEndNotification(String auctionId, String winnerName, double finalPrice) {
        String endData = buildJsonString(Map.of(
            "type", "auctionEnd",
            "auctionId", auctionId,
            "winnerName", winnerName,
            "finalPrice", finalPrice
        ));
        System.out.println(endData);
        WebSocket.broadcastAuctionUpdate(auctionId, endData);
    }

    private static String buildJsonString(Map<String, Object> data) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        boolean firstEntry = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!firstEntry) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\"")
                        .append(entry.getKey())
                        .append("\":");

            if (entry.getValue() instanceof String) {
                jsonBuilder.append("\"")
                            .append(entry.getValue())
                            .append("\"");
            } else {
                jsonBuilder.append(entry.getValue());
            }

            firstEntry = false;
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
