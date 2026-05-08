package Auction.example;

import Auction.example.enums.ItemCondition;
import Auction.example.model.auction.Auction;
import Auction.example.model.item.Electronics.Electronics;
import Auction.example.model.item.items.Item;
import Auction.example.model.user.Bidder;
import Auction.example.model.user.Seller;
import Auction.example.model.user.User;
import dao.DatabaseManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class ServerApplication {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.loadDataOnStartup();

        Runtime.getRuntime().addShutdownHook(new Thread(dbManager::saveAllData));
        System.out.println("Server listening on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket, dbManager)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private final DatabaseManager dbManager;

    ClientHandler(Socket socket, DatabaseManager dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Request: " + request);
                handleRequest(request, out);
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }

    private void handleRequest(String request, PrintWriter out) {
        String[] parts = request.split("\\|", -1);
        String command = parts[0].trim().toUpperCase(Locale.ROOT);

        try {
            // Protocol socket dạng COMMAND|tham_so_1|tham_so_2 để client JavaFX gọi đơn giản.
            switch (command) {
                case "LOGIN" -> handleLogin(parts, out);
                case "GET_AUCTIONS" -> handleGetAuctions(out);
                case "REGISTER" -> handleRegister(parts, out);
                case "CREATE_AUCTION" -> handleCreateAuction(parts, out);
                case "START_AUCTION" -> handleStartAuction(parts, out);
                case "CANCEL_AUCTION" -> handleCancelAuction(parts, out);
                case "PLACE_BID" -> handlePlaceBid(parts, out);
                case "PAYMENT" -> handlePayment(parts, out);
                case "TOPUP" -> handleTopUp(parts, out);
                default -> out.println("ERROR|Unknown command");
            }
        } catch (Exception e) {
            out.println("ERROR|" + sanitize(e.getMessage()));
        }
    }

    private void handleLogin(String[] parts, PrintWriter out) {
        if (parts.length != 3) {
            out.println("ERROR|LOGIN format: LOGIN|username|password");
            return;
        }

        boolean valid = dbManager.getUserManager().authenticate(parts[1], parts[2]);
        out.println(valid ? "SUCCESS" : "FAIL");
    }

    private void handleGetAuctions(PrintWriter out) {
        Collection<Auction> auctions = dbManager.getAuctionManager().getAllAuctions();
        if (auctions.isEmpty()) {
            out.println("AUCTION_LIST");
            return;
        }

        StringBuilder response = new StringBuilder("AUCTION_LIST");
        for (Auction auction : auctions) {
            response.append("|").append(formatAuction(auction));
        }
        out.println(response);
    }

    private void handleRegister(String[] parts, PrintWriter out) {
        if (parts.length < 6) {
            out.println("ERROR|REGISTER format: REGISTER|BIDDER|username|password|fullname|email|balance");
            return;
        }

        String role = parts[1].trim().toUpperCase(Locale.ROOT);
        String username = parts[2].trim();
        String password = parts[3];
        String fullname = parts[4].trim();
        String email = parts[5].trim();
        String id = nextUserId();

        User user;
        if ("BIDDER".equals(role)) {
            double balance = parts.length >= 7 && !parts[6].isBlank() ? Double.parseDouble(parts[6]) : 0.0;
            user = new Bidder(id, username, password, fullname, email, balance);
        } else if ("SELLER".equals(role)) {
            user = new Seller(id, username, password, fullname, email);
        } else {
            out.println("ERROR|Only BIDDER or SELLER registration is supported");
            return;
        }

        dbManager.addUser(user);
        out.println("OK|USER_CREATED|" + id);
    }

    private void handleCreateAuction(String[] parts, PrintWriter out) {
        if (parts.length != 8) {
            out.println("ERROR|CREATE_AUCTION format: CREATE_AUCTION|auctionId|sellerId|itemName|description|startPrice|durationMinutes|minIncrement");
            return;
        }

        String auctionId = parts[1].isBlank() ? nextAuctionId() : parts[1].trim();
        String sellerId = parts[2].trim();
        Optional<User> seller = dbManager.getUserManager().findById(sellerId);
        if (seller.isEmpty() || !(seller.get() instanceof Seller)) {
            out.println("ERROR|Seller not found");
            return;
        }

        String itemName = parts[3].trim();
        String description = parts[4].trim();
        double startPrice = Double.parseDouble(parts[5]);
        long duration = Long.parseLong(parts[6]);
        double minIncrement = Double.parseDouble(parts[7]);

        Electronics item = new Electronics(
                "ITEM-" + auctionId,
                itemName,
                description,
                startPrice,
                ItemCondition.USED,
                0,
                "Unknown"
        );
        Auction auction = new Auction(auctionId, sellerId, item, startPrice, duration, minIncrement);
        dbManager.addAuction(auction);
        out.println("OK|AUCTION_CREATED|" + auctionId);
    }

    private void handleStartAuction(String[] parts, PrintWriter out) throws Exception {
        if (parts.length != 2) {
            out.println("ERROR|START_AUCTION format: START_AUCTION|auctionId");
            return;
        }

        Auction auction = dbManager.getAuctionManager().getAuction(parts[1]);
        auction.start();
        dbManager.saveAuctions();
        out.println("OK|AUCTION_STARTED|" + auction.getAuctionId());
    }

    private void handleCancelAuction(String[] parts, PrintWriter out) throws Exception {
        if (parts.length < 2) {
            out.println("ERROR|CANCEL_AUCTION format: CANCEL_AUCTION|auctionId|reason");
            return;
        }

        String reason = parts.length >= 3 ? parts[2] : "Canceled by server command";
        Auction auction = dbManager.getAuctionManager().getAuction(parts[1]);
        auction.cancel(reason);
        dbManager.saveAuctions();
        out.println("OK|AUCTION_CANCELED|" + auction.getAuctionId());
    }

    private void handlePlaceBid(String[] parts, PrintWriter out) throws Exception {
        if (parts.length != 4) {
            out.println("ERROR|PLACE_BID format: PLACE_BID|auctionId|bidderId|amount");
            return;
        }

        String auctionId = parts[1].trim();
        String bidderId = parts[2].trim();
        double amount = Double.parseDouble(parts[3]);

        Optional<User> bidder = dbManager.getUserManager().findById(bidderId);
        if (bidder.isEmpty() || !(bidder.get() instanceof Bidder)) {
            out.println("ERROR|Bidder not found");
            return;
        }

        dbManager.getAuctionManager().placeBid(auctionId, (Bidder) bidder.get(), amount);
        dbManager.saveAuctions();
        out.println("OK|BID_PLACED|" + auctionId + "|" + amount);
    }

    private void handlePayment(String[] parts, PrintWriter out) throws Exception {
        if (parts.length != 4) {
            out.println("ERROR|PAYMENT format: PAYMENT|auctionId|winnerId|amount");
            return;
        }

        boolean paid = dbManager.getAuctionManager()
                .processPayment(parts[1], parts[2], Double.parseDouble(parts[3]));
        if (paid) {
            dbManager.saveAuctions();
            out.println("OK|PAYMENT_SUCCESS|" + parts[1]);
        } else {
            out.println("FAIL|PAYMENT_REJECTED");
        }
    }

    private void handleTopUp(String[] parts, PrintWriter out) {
        if (parts.length != 3) {
            out.println("ERROR|TOPUP format: TOPUP|bidderId|amount");
            return;
        }

        Optional<User> user = dbManager.getUserManager().findById(parts[1]);
        if (user.isEmpty() || !(user.get() instanceof Bidder bidder)) {
            out.println("ERROR|Bidder not found");
            return;
        }

        bidder.topUPBalance(Double.parseDouble(parts[2]));
        dbManager.saveUsers();
        out.println("OK|TOPUP_SUCCESS|" + bidder.getId() + "|" + bidder.getBalance());
    }

    private String formatAuction(Auction auction) {
        Item item = auction.getAuctionItem();
        String itemName = item == null ? "No item" : item.getName();
        // Dùng "~" để tách field trong từng auction, còn "|" để tách nhiều auction trong response.
        return String.join("~",
                sanitize(auction.getAuctionId()),
                sanitize(itemName),
                String.valueOf(auction.getCurrentPrice()),
                auction.getState().name(),
                String.valueOf(auction.getRemainingTimeMillis()),
                sanitize(auction.getSellerId())
        );
    }

    private String nextUserId() {
        return "U" + (dbManager.getUserManager().getAllUsers().size() + 1 + System.currentTimeMillis() % 100000);
    }

    private String nextAuctionId() {
        return "AUC" + (dbManager.getAuctionManager().getAllAuctions().size() + 1 + System.currentTimeMillis() % 100000);
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", " ").replace("~", " ").replace("\n", " ").replace("\r", " ");
    }
}
