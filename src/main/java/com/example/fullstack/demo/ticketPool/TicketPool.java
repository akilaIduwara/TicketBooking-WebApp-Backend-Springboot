package com.example.fullstack.demo.ticketPool;

import com.example.fullstack.demo.ticket.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class TicketPool {
    private final int maxTicketCapacity;
    private final List<Ticket> ticketArrayList;
    private int ticketsSold = 0;
    private final int totalTickets;
    private boolean simulationComplete = false;

    public TicketPool(int maxTicketCapacity, int totalTickets) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.ticketArrayList = Collections.synchronizedList(new ArrayList<>());
        this.totalTickets = totalTickets;
    }

    public synchronized void shutdown() {
        simulationComplete = true;
        notifyAll(); // Wake up all waiting threads
    }

    public synchronized void addTicket(Ticket ticket) {
        while (!simulationComplete && ticketArrayList.size() >= maxTicketCapacity) {
            try {
                System.out.println("Vendor waiting, ticket pool full: " + Thread.currentThread().getName());
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return; // Exit gracefully if interrupted
            }
        }
        if (simulationComplete) return; // Exit if simulation is shutting down
        ticketArrayList.add(ticket);
        System.out.println("Ticket added by " + Thread.currentThread().getName() +
                ". Current pool size: " + ticketArrayList.size());
        notifyAll();
    }

    public synchronized Ticket buyTicket() {
        while (!simulationComplete && ticketArrayList.isEmpty()) {
            try {
                System.out.println("Customer waiting, no tickets available: " + Thread.currentThread().getName());
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null; // Exit gracefully if interrupted
            }
        }
        if (simulationComplete) return null; // Exit if simulation is shutting down
        Ticket ticket = ticketArrayList.remove(0);
        System.out.println("Ticket bought by " + Thread.currentThread().getName() +
                ". Current pool size: " + ticketArrayList.size());
        notifyAll();
        return ticket;
    }
}
