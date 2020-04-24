# ManyThread
Java 中 实现多线程的四种方式
*一.继承 Thread 类重写run方法开辟线程，start 启动线程，但不是立即启动需CPU给当前线程分配的资源时间到齐，才会启动
*二.实现Runnable 方法,并且初始化Thread实例，并且调用start 方法开启线程
*三.以上两种线程实现，都有一个缺点不能得到线程任务执行完了之后，无法获取返回结果，于是callable 接口跟futurehe futureTask配合取得返回结果实现callable 接口， 然后创建新实例，并且调用start方法
*四.线程池这里重点说明一下为什么要使用线程池
 *实现以上三种实现多线程都是new Thread实例，而这有很多弊端比如1.线程生命周期开销非常大，创建线程都会需要时间延迟处理的请求，需要虚拟机和操作系统提供一些辅助操作
 ，2.资源消耗，活跃的线程会消耗系统资源，尤其是内存。如果可运行的线程数量多于可用处理器的数量，那么有些线程将会闲置。大量空闲的线程会占用许多内存，给GC带来压力，而且大量线程在竞争CPU资源时会产生其他的
 性能开销3.稳定性线程的数量是受到一定限制，随着不断增加可以提高系统的吞吐率（指令条数/执行时间），但如果超出预期的范围再创建更多线程只会降低程序的执行效率甚至导致系统崩溃。
 *所以在线程频繁创建的情况就有了线程池，它类似于工作队列相当于一组管理线程工作的资源池，获取任务并执行任务，然后返回线程池等待下一个任务，线程池启动初期线程不会启动，有任务提交(调用execute或submit)
 才会启动，直到到达最大数量就不再创建而是进入阻塞队列，优点通过重用现有的线程而不是创建新线程，可以处理多个请求时分摊在创建线程和销毁过程中产生的巨大开销，另外当请求到达时，工作线程通常已经存在，因此不会由于创建线程而延迟任务的执行，从而提高了性能。
 *线程池的四种方式：
 *FixedThreadPool的用法  采用基于链表的阻塞队列  创建一个定长线程池，每当提交一个任务时就创建一个线程，直到线程池的最大数量，这时线程池的规模将不再变化
  private  static  final ExecutorService  bachTaskPool= Executors.newFixedThreadPool(2);    //使用了阻塞队列，超过池子容量的线程会在队列中等待
  *CachedThreadPool 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活收回空闲线程，若无可回收，则新建线程  
  private  static  final ExecutorService bachTaskPool=Executors.newCachedThreadPool();  //源码使用的是同步队列
 * SingleThreadPool 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。类似于单线程执行的效果一样。
  private static  final ExecutorService bachTaskPool=Executors.newSingleThreadExecutor();//源码采用的阻塞队列模式
  *ScheduledThreadPool 创建一个定长线程池(会指定容量初始化大小)，支持定时及周期性任务执行。可以实现一次性的执行延迟任务，也可以实现周期性的执行任务。
   private static final ScheduledExecutorService bachTaskPool = Executors.newScheduledThreadPool(2);//任务调度功能  源码使用的是延迟队列
   */
