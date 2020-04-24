package com.many_thread;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * java中实现多线程方式有四种方式可以创建线程
 * new Thread的弊端
 * 线程生命周期的开销非常高。创建线程都会需要时间，延迟处理的请求，并且需要JVM和操作系统提供一些辅助操作。
 * 资源消耗。活跃的线程会消耗系统资源，尤其是内存。如果可运行的线程数量多于可用处理器的数量，那么有些线程将会闲置。大量空闲的线程会占用许多内存，给GC带来压力，而且大量线程在竞争CPU资源时会产生其他的性能开销。
 * 稳定性。在可创建线程的数量上存在一个限制，这个限制受多个因素的制约
 * 也就是说在一定的范围内增加线程的数量可以提高系统的吞吐率，但是如果超出了这个范围，再创建更多的线程只会降低程序的执行效率甚至导致系统的崩溃。
 * 所以有了线程池ThreadPoolExecutor
 * 线程池，从字面意义上看，是指管理一组同构工作线程的资源池。线程池是与工作队列(work queue)密切相关的，其中在工作队列保存了所有等待执行的任务。
 * 工作者线程的任务很简单:从工作队列中获取一个任务并执行任务，然后返回线程池等待下一个任务。(线程池启动初期线程不会启动，有任务提交(调用execute或submit)
 * 才会启动，直到到达最大数量就不再创建而是进入阻塞队列)。
 * 在线程池中执行任务"比"为每一个任务分配一个线程"优势更多。通过重用现有的线程而不是创建新线程，可以处理多个请求时分摊在创建线程和销毁过程中产生的巨大开销。
 * 另外一个额外的好处是，当请求到达时，工作线程通常已经存在，因此不会由于创建线程而延迟任务的执行，从而提高了性能。
 */
public class MainActivity extends AppCompatActivity {

    //FixedThreadPool的用法  采用LinkedBlockingQueue队列--基于链表的阻塞队列  创建一个定长线程池，每当提交一个任务时就创建一个线程，直到线程池的最大数量，这时线程池的规模将不再变化
//    private  static  final ExecutorService  bachTaskPool= Executors.newFixedThreadPool(2);    //使用了阻塞队列，超过池子容量的线程会在队列中等待
    //CachedThreadPool 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活收回空闲线程，若无可回收，则新建线程
//    private  static  final ExecutorService bachTaskPool=Executors.newCachedThreadPool();  //源码使用的是同步队列
    //创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。类似于单线程执行的效果一样。
//    private static  final ExecutorService bachTaskPool=Executors.newSingleThreadExecutor();//源码采用的阻塞队列模式
    //创建一个定长线程池(会指定容量初始化大小)，支持定时及周期性任务执行。可以实现一次性的执行延迟任务，也可以实现周期性的执行任务。
    private static final ScheduledExecutorService bachTaskPool = Executors.newScheduledThreadPool(2);//任务调度功能  源码使用的是延迟队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Timer();
        TestPoolDestory();
//        TestPoolDestroy();
        System.out.println("main end");
    }

    private void init() {
        /**.start 启动线程，但不是立即启动需CPU给当前线程分配的资源时间到齐，才会启动**/
        ReThead mReThead = new ReThead();
        mReThead.start();
        /**实现runnable 接口 并且初始化Thred ,然后创建新实例，并且调用start方法**/
        Thread mRun = new Thread(mRunnable);
        mRun.start();
        /**实现callable 接口 并且初始化Thred ,然后创建新实例，并且调用start方法**/
        FutureTask<Object> futureTask = new FutureTask<Object>(mCallable);
        Thread mCall = new Thread(futureTask);
        mCall.start();
        /**
         *  四.线程池，而线程池四种用法
         */
        for (int i = 0; i < 3; i++) {
            bachTaskPool.execute(mRunnable);
        }
    }


    /**
     * 1.继承 Thread 类重写run方法开辟线程
     */
    private class ReThead extends Thread {
        @Override
        public void run() {
            super.run();

        }
    }

    /**
     * 2.实现runnabel 方法
     */
    Runnable mRunnable = new Runnable() {
        /**
         * 　池子容量大小是2，所以前两个先被执行，第三个runable只是暂时的加到等待队列，前两个执行完成之后线程
         *  pool-1-thread-1空闲之后从等待队列获取runnable进行执行。
         *　定长线程池的大小最好根据系统资源进行设置。如Runtime.getRuntime().availableProcessors()
         */
        @Override
        public void run() {
            synchronized (this) {
                for (int i = 0; i < 5; i++) {
                    Log.e("threadName -> {},i->{} ", Thread.currentThread().getName() + "," + i);
                    try {
                        Thread.sleep(1 * 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    /**
     * 3.以上两种线程实现，都有一个缺点不能得到线程任务执行完了之后，无法获取返回结果，于是callable 接口跟futurehe futureTask配合取得返回结果
     */
    Callable<Object> mCallable = new Callable() {
        @Override
        public Object call() throws Exception {
            return null;
        }
    };

    /**
     * 比如想要实现在某一个时钟定时晚上11点执行任务，并且每天都执行
     */
    private void Timer() {
        ScheduledExecutorService bachTaskPool = Executors.newScheduledThreadPool(2);//任务调度功能  源码使用的是延迟队列
        long curDateSecneds = 0;
        try {
            String time = "11:00:00";
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            curDateSecneds = curDate.getTime();
        } catch (ParseException ignored) {
            // ignored
        }
        // 单位是s
        long initialDelay = (curDateSecneds - System.currentTimeMillis()) / 1000;
        int periodOneDaySeconds = 1 * 24 * 60 * 60;
        bachTaskPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, initialDelay, periodOneDaySeconds, TimeUnit.SECONDS);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.print("111111");
            Log.e("main", "111111");
        }
    };

    /**
     * 闭锁
     */
    private static void TestPoolDestory() {
        ExecutorService bachTaskPool = Executors.newFixedThreadPool(4);
        final CountDownLatch latch = new CountDownLatch(4);//闭锁关闭线程
        for (int i = 0; i < 4; i++) {
            bachTaskPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "进入run");
                    try {
                        Thread.sleep(4 * 1000);
                        System.out.println(Thread.currentThread().getName() + "退出run");
                        latch.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        try {
            latch.await();// 闭锁产生同步效果
            System.out.println("执行完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void TestPoolDestroy() {
        ExecutorService batchTaskPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            batchTaskPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + "进入run");
                        Thread.sleep(4 * 1000);
                        System.out.println(Thread.currentThread().getName() + "退出run");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            batchTaskPool.shutdown();
            batchTaskPool.awaitTermination(1, TimeUnit.DAYS);
            System.out.println("执行完毕");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
