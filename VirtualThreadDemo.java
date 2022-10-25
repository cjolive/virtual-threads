import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        System.out.printf("Regular thread run %d\n", runThreads(executor));

        executor = Executors.newVirtualThreadPerTaskExecutor();

        System.out.printf("Virtual thread run %d\n", runThreads(executor));

        System.out.println("Run complete");
    }

    public static long runThreads(ExecutorService executor) {
        System.out.printf("Using %S\n", executor.getClass().getName());
        long runTime = 0;
        try {
            List<Callable<Integer>> tasks = new ArrayList<>();

            for (int i = 0; i <= 1000; i++) {
                final int iteration = i;
                tasks.add(() -> {
                    Thread.sleep(Duration.ofSeconds(1));
                    return iteration;
                });
            }

            long startTime = System.currentTimeMillis();

            List<Future<Integer>> futures = executor.invokeAll(tasks);

            long sum = 0;
            for (Future<Integer> future : futures) {
                sum += future.get();
            }
            System.out.printf("Sum %s\n", sum);
            runTime = System.currentTimeMillis() - startTime;
            executor.shutdown();
        } catch (Exception e) {
            System.out.printf("Unexpected error %s\n", e);
        }
        return runTime;
    }

}