package de.juniorjacki.utils.logger.processing;


import de.juniorjacki.utils.logger.FileWriter;
import de.juniorjacki.utils.logger.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class QueueProcessor {

    private static final BlockingQueue<Log> QUEUE = new LinkedBlockingQueue<>(5000);
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static final Thread worker;

    static {
        worker = new Thread(QueueProcessor::run, "Logger-Worker");
        worker.setDaemon(true);
        worker.start();
        Runtime.getRuntime().addShutdownHook(new Thread(QueueProcessor::shutdownAndWait, "Logger-ShutdownHook"));
    }

    private static void run() {
        while (running.get() || !QUEUE.isEmpty()) {
            try {
                Log queuedLog = QUEUE.poll(100, TimeUnit.MILLISECONDS);
                if (queuedLog != null) {
                    queuedLog.process();
                }
            } catch (Throwable ignored) {}
        }
    }

    public static void enqueue(Log task) {
        QUEUE.offer(task);
    }

    public static void shutdownAndWait() {
        System.out.println("Shutting down");
        running.set(false);
        try {
            worker.join(); //blocks until queue is empty
        } catch (InterruptedException ignored) {}
        FileWriter.disable();
        System.out.println("Shut down");
    }
}
