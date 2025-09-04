package auction;

import catalogue.CatalogueModule;
import exceptions.DatabaseConnectionException;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import auctionClasses.Auction;
import auctionClasses.DutchAuction;

public class AuctionScheduler {
    private Timer timer;
    public final static long incrementMillisecUnit = TimeUnit.SECONDS.toMillis(10);

    public AuctionScheduler(AuctionModule module) {
        this.timer = new Timer(true);
    }

    /**
     * Schedules an auction for lifecycle management.
     * 
     * @param auction The auction to schedule
     */
    public void scheduleAuction(Auction auction) {
        long delay = auction.getAuctionClose() - System.currentTimeMillis();
        if (delay < 0) {
            delay = 0;
        }
        timer.schedule(new AuctionTask(auction), delay);

        // If Dutch Auction, schedule price decrements
        if (auction instanceof DutchAuction) {
            schedulePriceDecrements((DutchAuction) auction);
        }
    }

    /**
     * Schedules price decrements for Dutch Auctions.
     * 
     * @param auction The Dutch Auction
     */
    public void schedulePriceDecrements(DutchAuction auction) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    synchronized (auction) {
                    	DutchAuction freshAuction = (DutchAuction) new CatalogueModule().getAuction(auction.getAuctionId()).getAuction();
                        if (freshAuction.isClosed()) {
                            this.cancel();
                            return;
                        }
                        double oldPrice = freshAuction.getStartingPrice();
                        freshAuction.decrementPrice();
                        System.out.println("Lowering price from "+oldPrice +" to " + auction.getStartingPrice());
                        // If the current price reaches the reserve price
                        if (freshAuction.getStartingPrice() <= freshAuction.getReservePrice()) {
                            // TODO: currently extending auction by 1 minute after reserve price is reached,
                            // may need to be changed
                        	freshAuction.setAuctionClose(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
                            this.cancel(); // Stop further decrements
                        }
                    }
                } catch (DatabaseConnectionException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, incrementMillisecUnit, incrementMillisecUnit);
    }

    /**
     * Inner class to handle auction closure.
     */
    private class AuctionTask extends TimerTask {
        private Auction auction;

        public AuctionTask(Auction auction) {
            this.auction = auction;
        }

        @Override
        public void run() {
        	System.out.println("Scheduler closing " + auction.getAuctionId());
        	new CatalogueModule().getAuction(auction.getAuctionId()).getAuction().close();
        }
    }

}
