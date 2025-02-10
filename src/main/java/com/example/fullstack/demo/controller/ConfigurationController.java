package com.example.fullstack.demo.controller;

import com.example.fullstack.demo.customer.Customer;
import com.example.fullstack.demo.entity.SystemConfiguration;
import com.example.fullstack.demo.repository.SystemConfigurationRepository;
import com.example.fullstack.demo.ticketPool.TicketPool;
import com.example.fullstack.demo.vendor.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class ConfigurationController {

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    private final List<Thread> runningThreads = new ArrayList<>(); // Track running threads
    private volatile boolean simulationRunning = false; // Flag to control simulation


    @PostMapping("/stop")
    public String stopSimulation() {
        simulationRunning = false; // Signal threads to stop

        for (Thread thread : runningThreads) {
            try {
                thread.interrupt(); // Interrupt the thread
                thread.join(500);  // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                return "Simulation interrupted while stopping.";
            }
        }

        runningThreads.clear(); // Clear thread list after stopping
        System.out.println("Simulation has been stopped.");
        return "Simulation has been stopped.";
    }

    @PutMapping("/start")
    public String startSimulation(@RequestBody SystemConfiguration systemConfiguration) {
        int maxTicketCapacity = systemConfiguration.getMaxTicketCapacity();
        int totalNoOfTickets = systemConfiguration.getTotalNoOfTickets();
        int ticketReleaseRate = systemConfiguration.getTicketReleaseRate();
        int customerRetrievalRate = systemConfiguration.getCustomerRetrievalRate();

        systemConfigurationRepository.save(systemConfiguration);

        // Initialize TicketPool
        TicketPool ticketPool = new TicketPool(maxTicketCapacity, totalNoOfTickets);

        // Set simulation running flag
        simulationRunning = true;

        // Start vendor threads
        for (int i = 1; i <= 2; i++) {
            Vendor vendor = new Vendor(totalNoOfTickets, ticketReleaseRate, ticketPool, simulationRunning) {
                @Override
                public void run() {
                    while (simulationRunning) {
                        super.run();
                    }
                }
            };
            Thread vendorThread = new Thread(vendor, "Vendor-" + i);
            runningThreads.add(vendorThread);
            vendorThread.start();
        }

        // Start customer threads
        for (int i = 1; i <= 2; i++) {
            Customer customer = new Customer(ticketPool, customerRetrievalRate, 2, simulationRunning) {
                @Override
                public void run() {
                    while (simulationRunning) {
                        super.run();
                    }
                }
            };
            Thread customerThread = new Thread(customer, "Customer-" + i);
            runningThreads.add(customerThread);
            customerThread.start();
        }

        return "Simulation started with the following parameters:\n" +
                "Max Ticket Capacity: " + maxTicketCapacity + "\n" +
                "Total No. of Tickets: " + totalNoOfTickets + "\n" +
                "Ticket Release Rate: " + ticketReleaseRate + "\n" +
                "Customer Retrieval Rate: " + customerRetrievalRate;
    }
}