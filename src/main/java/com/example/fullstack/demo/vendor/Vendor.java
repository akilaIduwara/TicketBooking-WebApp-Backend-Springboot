package com.example.fullstack.demo.vendor;
import com.example.fullstack.demo.ticket.Ticket;
import com.example.fullstack.demo.ticketPool.TicketPool;

import java.math.BigDecimal;

public class Vendor implements Runnable {
    private final int totalTickets;
    private final int ticketReleaseRate;
    private final TicketPool ticketArrayList;
    private final  boolean simulationRunning; // Track if the simulation is running

    public Vendor(int totalTickets, int ticketReleaseRate, TicketPool ticketArrayList, boolean simulationRunning) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.ticketArrayList = ticketArrayList;
        this.simulationRunning = simulationRunning; // Initialize the simulation running flag
    }

    @Override
    public void run() {
        for (int ticketCount = 1; ticketCount <= totalTickets; ticketCount++) {
            if (Thread.currentThread().isInterrupted() || !simulationRunning) {
                System.out.println(Thread.currentThread().getName() + " stopped.");
                break;
            }
            Ticket ticket = new Ticket(ticketCount, "Simple Event", new BigDecimal("1000"));
            ticketArrayList.addTicket(ticket);
            try {
                Thread.sleep(ticketReleaseRate * 1000L); // Simulate ticket release delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.out.println(Thread.currentThread().getName() + " interrupted during sleep.");
                break;
            }
        }
    }
}



