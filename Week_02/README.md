# 作业-周四

## 第一题

### 描述

使用GCLogAnalysis.java自己演练一遍串行/并行/CMS/G1的案例。

### 解答

首先是系统配置：

```shell
% sysctl -a |egrep 'machdep.*cpu.*count'
machdep.cpu.core_count: 4
machdep.cpu.thread_count: 8

% java -version
java version "1.8.0_271"
Java(TM) SE Runtime Environment (build 1.8.0_271-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.271-b09, mixed mode)
```

### 串行

```
% java -XX:+UseSerialGC -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis 
正在执行....
执行结束，共生成对象的次数为：11228
```

结果如下：

```shell
Memory: 4k page, physical 8388608k(33552k free)

/proc/meminfo:

CommandLine flags: -XX:InitialHeapSize=536870912 -XX:MaxHeapSize=536870912 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseSerialGC 
2020-10-25T20:43:09.230-0800: 0.455: [GC (Allocation Failure) 2020-10-25T20:43:09.231-0800: 0.455: [DefNew: 139704K->17472K(157248K), 0.0341374 secs] 139704K->43795K(506816K), 0.0342143 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2020-10-25T20:43:09.287-0800: 0.511: [GC (Allocation Failure) 2020-10-25T20:43:09.287-0800: 0.511: [DefNew: 157248K->17471K(157248K), 0.0367446 secs] 183571K->88298K(506816K), 0.0368105 secs] [Times: user=0.02 sys=0.01, real=0.04 secs] 
2020-10-25T20:43:09.338-0800: 0.562: [GC (Allocation Failure) 2020-10-25T20:43:09.338-0800: 0.562: [DefNew: 157247K->17468K(157248K), 0.0276339 secs] 228074K->131263K(506816K), 0.0277170 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2020-10-25T20:43:09.379-0800: 0.604: [GC (Allocation Failure) 2020-10-25T20:43:09.379-0800: 0.604: [DefNew: 156655K->17468K(157248K), 0.0269085 secs] 270450K->172957K(506816K), 0.0269881 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
……………………………………………………
2020-10-25T20:43:10.014-0800: 1.238: [GC (Allocation Failure) 2020-10-25T20:43:10.014-0800: 1.238: [DefNew: 139776K->139776K(157248K), 0.0000136 secs]2020-10-25T20:43:10.014-0800: 1.238: [Tenured: 349266K->333615K(349568K), 0.0350254 secs] 489042K->333615K(506816K), [Metaspace: 2735K->2735K(1056768K)], 0.0351272 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2020-10-25T20:43:10.064-0800: 1.289: [GC (Allocation Failure) 2020-10-25T20:43:10.064-0800: 1.289: [DefNew: 139776K->139776K(157248K), 0.0000144 secs]2020-10-25T20:43:10.064-0800: 1.289: [Tenured: 333615K->349386K(349568K), 0.0201245 secs] 473391K->362854K(506816K), [Metaspace: 2735K->2735K(1056768K)], 0.0202353 secs] [Times: user=0.02 sys=0.00, real=0.02 secs] 
2020-10-25T20:43:10.100-0800: 1.324: [Full GC (Allocation Failure) 2020-10-25T20:43:10.100-0800: 1.324: [Tenured: 349386K->349194K(349568K), 0.0296062 secs] 506231K->361172K(506816K), [Metaspace: 2735K->2735K(1056768K)], 0.0296861 secs] [Times: user=0.03 sys=0.00, real=0.02 secs] 
Heap
 def new generation   total 157248K, used 18186K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,  13% used [0x00000007a0000000, 0x00000007a11c2920, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
  to   space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
 tenured generation   total 349568K, used 349194K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
   the space 349568K,  99% used [0x00000007aaaa0000, 0x00000007bffa2870, 0x00000007bffa2a00, 0x00000007c0000000)
 Metaspace       used 2742K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 298K, capacity 386K, committed 512K, reserved 1048576K
```

对第一条 GC 日志进行分析

```
2020-10-25T20:43:09.230-0800: 0.455: [GC (Allocation Failure) 2020-10-25T20:43:09.231-0800: 0.455: [DefNew: 139704K->17472K(157248K), 0.0341374 secs] 139704K->43795K(506816K), 0.0342143 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
```

- `0.455`:GC事件启动时间，这个时间的起始值是JVM的启动时间。

- `GC`: GC类型，GC 代表的类型是 minor GC，Full GC 代表了 Major GC。

- `(Allocation Failure)`:引起 GC 的原因

- `DefNew`:代表这是年轻代的GC数据

- `[Times: user=0.02 sys=0.01, real=0.03 secs]` ：整个GC花费的时间。

  - user：这次收集期间，GC线程花费的时间。

  - sys：操作系统调用或等待系统事件所花费的时间

  - real：整个应用停止的时间。

    由于本次使用的是串行 GC，即使用单线程进行GC，因此该时间=user+sys

可以看到，整个新生代，从 139704K->17472K ，88.8%->11.1%，整个新生代回收了 77.7% 的内存。

对最后一条 GC 日志进行分析

```
2020-10-25T20:43:10.100-0800: 1.324: [Full GC (Allocation Failure) 2020-10-25T20:43:10.100-0800: 1.324: [Tenured: 349386K->349194K(349568K), 0.0296062 secs] 506231K->361172K(506816K), [Metaspace: 2735K->2735K(1056768K)], 0.0296861 secs] [Times: user=0.03 sys=0.00, real=0.02 secs] 
```

- `Tenured`：代表这是老年代的GC数据

最后一次 Full GC，基本上没有回收到老年代的空间，如果继续执行下去，估计会产生OOM的现象。

### 并行 GC

```
$ java -XX:+UseParallelGC -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行....
执行结束，共生成对象的次数为：11693
```

结果如下：

```
2020-10-27T22:19:17.003-0800: 0.183: [GC (Allocation Failure) [PSYoungGen: 131584K->21496K(153088K)] 131584K->44729K(502784K), 0.0182557 secs] [Times: user=0.02 sys=0.08, real=0.02 secs] 
2020-10-27T22:19:17.037-0800: 0.216: [GC (Allocation Failure) [PSYoungGen: 153080K->21487K(153088K)] 176313K->83601K(502784K), 0.0208308 secs] [Times: user=0.02 sys=0.10, real=0.02 secs] 
2020-10-27T22:19:17.072-0800: 0.251: [GC (Allocation Failure) [PSYoungGen: 153071K->21500K(153088K)] 215185K->119697K(502784K), 0.0147490 secs] [Times: user=0.03 sys=0.07, real=0.01 secs] 
………………
2020-10-27T22:19:17.718-0800: 0.898: [Full GC (Ergonomics) [PSYoungGen: 60416K->0K(118272K)] [ParOldGen: 332034K->336012K(349696K)] 392450K->336012K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0234481 secs] [Times: user=0.16 sys=0.00, real=0.03 secs] 
2020-10-27T22:19:17.748-0800: 0.927: [Full GC (Ergonomics) [PSYoungGen: 60076K->0K(118272K)] [ParOldGen: 336012K->339125K(349696K)] 396088K->339125K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0239748 secs] [Times: user=0.16 sys=0.00, real=0.03 secs] 
2020-10-27T22:19:17.778-0800: 0.958: [Full GC (Ergonomics) [PSYoungGen: 59879K->0K(118272K)] [ParOldGen: 339125K->338941K(349696K)] 399005K->338941K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0258684 secs] [Times: user=0.15 sys=0.01, real=0.03 secs] 
2020-10-27T22:19:17.810-0800: 0.990: [Full GC (Ergonomics) [PSYoungGen: 60416K->0K(118272K)] [ParOldGen: 338941K->341279K(349696K)] 399357K->341279K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0245127 secs] [Times: user=0.16 sys=0.00, real=0.02 secs] 
2020-10-27T22:19:17.841-0800: 1.021: [Full GC (Ergonomics) [PSYoungGen: 60416K->0K(118272K)] [ParOldGen: 341279K->342519K(349696K)] 401695K->342519K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0207659 secs] [Times: user=0.14 sys=0.00, real=0.02 secs] 
2020-10-27T22:19:17.868-0800: 1.047: [Full GC (Ergonomics) [PSYoungGen: 60222K->0K(118272K)] [ParOldGen: 342519K->343290K(349696K)] 402741K->343290K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0251082 secs] [Times: user=0.18 sys=0.00, real=0.03 secs] 
2020-10-27T22:19:17.899-0800: 1.078: [Full GC (Ergonomics) [PSYoungGen: 59970K->0K(118272K)] [ParOldGen: 343290K->344903K(349696K)] 403260K->344903K(467968K), [Metaspace: 2735K->2735K(1056768K)], 0.0248958 secs] [Times: user=0.17 sys=0.00, real=0.03 secs] 
Heap
 PSYoungGen      total 118272K, used 58477K [0x00000007b5580000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 60416K, 96% used [0x00000007b5580000,0x00000007b8e9b788,0x00000007b9080000)
  from space 57856K, 0% used [0x00000007bc780000,0x00000007bc780000,0x00000007c0000000)
  to   space 56320K, 0% used [0x00000007b9080000,0x00000007b9080000,0x00000007bc780000)
 ParOldGen       total 349696K, used 344903K [0x00000007a0000000, 0x00000007b5580000, 0x00000007b5580000)
  object space 349696K, 98% used [0x00000007a0000000,0x00000007b50d1cd0,0x00000007b5580000)
 Metaspace       used 2742K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 298K, capacity 386K, committed 512K, reserved 1048576K
```

从上面的日志，尤其是最后一部分连续的 Full GC，可以看到两件有意思的时间：

1. 首先是 GC 的用时，`[Times: user=0.15 sys=0.00, real=0.02 secs]`，这里 user 使用了 0.15，而实际的 GC 时间却只有 0.02，这说明我们的 GC 过程是并行，并且因为采取了并行的方式，实际的 GC 时间大大降低了。

2. GC 的原因不再是 Allocation Failure，而是 **Ergonomics** ，这个单词，翻译成中文是 人体工学 的意思，**在JVM中的垃圾收集器中的Ergonomics就是负责自动的调解gc暂停时间和吞吐量之间的平衡，让虚拟机性能更好的一种做法**。

   对于注重吞吐量的收集器来说，在某个generation被过渡使用之前，GC ergonomics就会启动一次GC，以保证更好的性能。

   查看日志，可以看到，最后的连续 Full GC 时，老年代的空间已经几乎消耗殆尽了， JVM 估算出下次分配可能会发生无法分配的问题，因此提前触发一次 Full GC，这就是为什么分配原因写着 Ergonomics ，而每次 Full GC 并没有让老年代产生大量的可分配空间，因此就出现了连续的 Full GC 情况了。

### CMS

```
% java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行....
执行结束，共生成对象的次数为：11879
```

结果如下：

```
2020-10-27T22:50:38.952-0800: 1.000: [GC (CMS Initial Mark) [1 CMS-initial-mark: 349376K(349568K)] 354369K(506816K), 0.0003068 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.952-0800: 1.001: [CMS-concurrent-mark-start]
2020-10-27T22:50:38.953-0800: 1.002: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.953-0800: 1.002: [CMS-concurrent-preclean-start]
2020-10-27T22:50:38.954-0800: 1.003: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.954-0800: 1.003: [CMS-concurrent-abortable-preclean-start]
2020-10-27T22:50:38.954-0800: 1.003: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.954-0800: 1.003: [GC (CMS Final Remark) [YG occupancy: 22502 K (157248 K)]2020-10-27T22:50:38.954-0800: 1.003: [Rescan (parallel) , 0.0002160 secs]2020-10-27T22:50:38.954-0800: 1.003: [weak refs processing, 0.0000122 secs]2020-10-27T22:50:38.954-0800: 1.003: [class unloading, 0.0001466 secs]2020-10-27T22:50:38.954-0800: 1.003: [scrub symbol table, 0.0002356 secs]2020-10-27T22:50:38.955-0800: 1.004: [scrub string table, 0.0000813 secs][1 CMS-remark: 349376K(349568K)] 371878K(506816K), 0.0007501 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.955-0800: 1.004: [CMS-concurrent-sweep-start]
2020-10-27T22:50:38.955-0800: 1.004: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.955-0800: 1.004: [CMS-concurrent-reset-start]
2020-10-27T22:50:38.955-0800: 1.004: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:38.971-0800: 1.020: [GC (Allocation Failure) 2020-10-27T22:50:38.971-0800: 1.020: [ParNew: 156767K->156767K(157248K), 0.0000182 secs]2020-10-27T22:50:38.971-0800: 1.020: [CMS: 348029K->349385K(349568K), 0.0356724 secs] 504796K->351216K(506816K), [Metaspace: 2735K->2735K(1056768K)], 0.0357882 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2020-10-27T22:50:39.007-0800: 1.056: [GC (CMS Initial Mark) [1 CMS-initial-mark: 349385K(349568K)] 354725K(506816K), 0.0002246 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:39.007-0800: 1.056: [CMS-concurrent-mark-start]
2020-10-27T22:50:39.008-0800: 1.057: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:39.008-0800: 1.057: [CMS-concurrent-preclean-start]
2020-10-27T22:50:39.010-0800: 1.058: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2020-10-27T22:50:39.010-0800: 1.058: [CMS-concurrent-abortable-preclean-start]
2020-10-27T22:50:39.010-0800: 1.058: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:39.010-0800: 1.059: [GC (CMS Final Remark) [YG occupancy: 22667 K (157248 K)]2020-10-27T22:50:39.010-0800: 1.059: [Rescan (parallel) , 0.0002352 secs]2020-10-27T22:50:39.010-0800: 1.059: [weak refs processing, 0.0000140 secs]2020-10-27T22:50:39.010-0800: 1.059: [class unloading, 0.0001520 secs]2020-10-27T22:50:39.010-0800: 1.059: [scrub symbol table, 0.0002546 secs]2020-10-27T22:50:39.010-0800: 1.059: [scrub string table, 0.0000920 secs][1 CMS-remark: 349385K(349568K)] 372053K(506816K), 0.0008241 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:39.011-0800: 1.059: [CMS-concurrent-sweep-start]
2020-10-27T22:50:39.011-0800: 1.060: [CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2020-10-27T22:50:39.011-0800: 1.060: [CMS-concurrent-reset-start]
2020-10-27T22:50:39.011-0800: 1.060: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
Heap
 par new generation   total 157248K, used 74436K [0x00000007a0000000, 0x00000007aaaa0000, 0x00000007aaaa0000)
  eden space 139776K,  53% used [0x00000007a0000000, 0x00000007a48b1290, 0x00000007a8880000)
  from space 17472K,   0% used [0x00000007a9990000, 0x00000007a9990000, 0x00000007aaaa0000)
  to   space 17472K,   0% used [0x00000007a8880000, 0x00000007a8880000, 0x00000007a9990000)
 concurrent mark-sweep generation total 349568K, used 349385K [0x00000007aaaa0000, 0x00000007c0000000, 0x00000007c0000000)
 Metaspace       used 2742K, capacity 4486K, committed 4864K, reserved 1056768K
  class space    used 298K, capacity 386K, committed 512K, reserved 1048576K
```

从上面的日志可以得到几个结论：

1. CMS 采取的是分步回收的方式进行 GC 的，由于每个步骤分的比较细，且可以与业务进行并发，对业务的影响较小。
2. CMS 在老年代快要满的时候，相比并行GC，有更好的性能，可以看到，在整个阶段，CMS 每一步的用时都在 0.00 sec 的精度以下的。

### G1 GC

#### 512m

```
% java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
正在执行....
执行结束，共生成对象的次数为：11879
```

```
% java -XX:+UseG1GC -Xms512m -Xmx512m -Xloggc:gc.demo.log -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis 
正在执行....
执行结束，共生成对象的次数为：9675
```

#### 4g

```
% java -XX:+UseG1GC -Xms2g -Xmx2g -Xloggc:gc.demo.log -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行....
执行结束，共生成对象的次数为：12942
```

```
% java -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g -Xloggc:gc.demo.log -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
正在执行....
执行结束，共生成对象的次数为：10829
```

可以看出

1. 在堆内存较小的时候，CMS 比 G1 更有效率。
2. 当堆内存变大之后，G1 比 CMS 更有效率。

## 第2题

### 描述

使用压测工具(wrk或sb)，演练gateway-server-0.0.1-SNAPSHOT.jar 示例。

### 解答

#### wrk 参数说明

参数：

- -t 需要模拟的线程数
- -c 需要模拟的连接数
- --timeout 超时的时间
- -d 测试的持续时间

结果：

- Latency：响应时间
- Req/Sec：每个线程每秒钟的完成的请求数
- Avg：平均
- Max：最大
- Stdev：标准差
- +/- Stdev： 正负一个标准差占比（结果的离散程度，越大越不稳定）
- Requests/sec：QPS（每秒请求数）
- Transfer/sec：每秒传输数量

### 使用 CMS 启用服务

```bash
 % java -jar -XX:+UseConcMarkSweepGC -Xms2g -Xmx2g gateway-server-0.0.1-SNAPSHOT.jar 
```

wrk 测试结果

```
% wrk -t8 -c40 -d60s http://localhost:8088/api/hello
Running 1m test @ http://localhost:8088/api/hello
  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     8.07ms   42.17ms  558.62ms   97.03%
    Req/Sec     2.93k    454.63     6.98k    86.83%
  1377209 requests in 1.00m, 164.43MB read
Requests/sec:  22916.63
Transfer/sec:      2.74MB
```

### 使用 G1 启动服务

```
% java -jar -XX:+UseG1GC -Xms2g -Xmx2g gateway-server-0.0.1-SNAPSHOT.jar
```

Wrk 测试结果

```
% wrk -t8 -c40 -d60s http://localhost:8088/api/hello
Running 1m test @ http://localhost:8088/api/hello
  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.73ms   19.21ms 288.04ms   91.59%
    Req/Sec     2.23k     0.96k    7.06k    55.97%
  1060298 requests in 1.00m, 126.59MB read
Requests/sec:  17646.74
Transfer/sec:      2.11MB
```

### 使用并行 GC

```
% java -jar -XX:+UseParallelGC -Xms2g -Xmx2g gateway-server-0.0.1-SNAPSHOT.jar
```

wrk 测试结果

```
% wrk -t8 -c40 -d60s http://localhost:8088/api/hello
Running 1m test @ http://localhost:8088/api/hello
  8 threads and 40 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     6.70ms   18.49ms 313.90ms   92.82%
    Req/Sec     2.52k     1.01k    5.79k    69.99%
  1197958 requests in 1.00m, 143.02MB read
Requests/sec:  19955.20
Transfer/sec:      2.38MB
```



### 结论

由上面的测试结果来看

- G1：最大时延最小，吞吐量最小，对于 P99 的用户最友好，因此适用于对于吞吐量要求没那么高，但响应时长要求高的场景。
- CMS：最大时延大，甚至能到其他 GC 的两倍，但是吞吐量最大，适用于批处理较多的程序。
- 并行GC：在吞吐量和时延上取得了平衡，作为 JDK8 的默认 GC 是有道理的。



## 第三题

### 描述

根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 的总结

### 总结



# 作业-周六

## 第二题

### 描述

写一段代码，使用 HttpClient 或 OkHttp 访问 [http://localhost:8801 ](http://localhost:8801/)。

### 解答

