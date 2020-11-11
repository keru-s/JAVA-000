import java.util.concurrent.*;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 * <p>
 * 一个简单的代码参考：
 */
public class Homework03 {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        //int result = sum(); //这是得到的返回值
        int result;
        //result = sumByFuture();
        //result = sumByFutureTaskAndThread();
        //result = sumByFutureTaskAndExecutor();
        result = sumByCompletableFuture();
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        // 然后退出main线程
    }


    /**
     * 通过 CompletableFuture.supplyAsync 来开启线程池
     */
    public static int sumByCompletableFuture(){
        try {
            return CompletableFuture.supplyAsync(Homework03::sum).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    /**
     * 新建一个线程池,通过 submit 方法获取一个 Future，从而获取返回值
     */
    public static int sumByFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> result = service.submit(Homework03::sum);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        } finally {
            service.shutdown();
        }
    }


    /**
     * 创建一个 FutureTask，新建一个线程来执行
     */
    public static int sumByFutureTaskAndThread() {
        FutureTask<Integer> result = new FutureTask<>(Homework03::sum);
        new Thread(result).start();
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    /**
     * 创建一个 FutureTask，新建一个线程池来执行
     */
    public static int sumByFutureTaskAndExecutor() {
        FutureTask<Integer> result = new FutureTask<>(Homework03::sum);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(result);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        } finally {
            service.shutdown();
        }
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }
}
