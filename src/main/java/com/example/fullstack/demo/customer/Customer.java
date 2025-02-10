package com.example.fullstack.demo.customer;
import com.example.fullstack.demo.ticket.Ticket;
import com.example.fullstack.demo.ticketPool.TicketPool;

public class Customer implements Runnable {
    private final TicketPool ticketArrayList;
    private final int customerRetrievalRate;
    private final int quantity;
    private final boolean simulationRunning; // Track if the simulation is running

    public Customer(TicketPool ticketArrayList, int customerRetrievalRate, int quantity, boolean simulationRunning) {
        this.ticketArrayList = ticketArrayList;
        this.customerRetrievalRate = customerRetrievalRate;
        this.quantity = quantity;
        this.simulationRunning = simulationRunning; // Initialize the simulation running flag
    }

    @Override
    public void run() {
        for (int i = 0; i < quantity; i++) {
            if (Thread.currentThread().isInterrupted() || !simulationRunning) {
                System.out.println(Thread.currentThread().getName() + " stopped.");
                break;
            }
            Ticket ticket = ticketArrayList.buyTicket();
            try {
                Thread.sleep(customerRetrievalRate * 1000L); // Simulate retrieval delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.out.println(Thread.currentThread().getName() + " interrupted during sleep.");
                break;
            }
        }
    }
}




