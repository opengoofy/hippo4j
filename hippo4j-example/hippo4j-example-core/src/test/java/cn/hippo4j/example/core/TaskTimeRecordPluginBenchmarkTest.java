package cn.hippo4j.example.core;

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin;
import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import lombok.SneakyThrows;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * benchmark test for {@link cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin}
 */
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
@Threads(6)
public class TaskTimeRecordPluginBenchmarkTest {

    @SneakyThrows
    @Benchmark
    public void origin_200(Blackhole blackhole) {
        int threadCount = 200;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @SneakyThrows
    @Benchmark
    public void origin_50(Blackhole blackhole) {
        int threadCount = 50;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @SneakyThrows
    @Benchmark
    public void not_plugin_50(Blackhole blackhole) {
        int threadCount = 50;
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @SneakyThrows
    @Benchmark
    public void not_plugin_200(Blackhole blackhole) {
        int threadCount = 200;
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @SneakyThrows
    @Benchmark
    public void plugin_50(Blackhole blackhole) {
        int threadCount = 50;
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();
        executor.register(new TaskTimeRecordPlugin());

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @SneakyThrows
    @Benchmark
    public void plugin_200(Blackhole blackhole) {
        int threadCount = 200;
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            threadCount, threadCount, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(threadCount), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.prestartAllCoreThreads();
        executor.register(new TaskTimeRecordPlugin());

        List<Runnable> tasks = getTask(threadCount, blackhole);
        tasks.forEach(executor::execute);

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    private List<Runnable> getTask(int count, Blackhole blackhole) {
        List<Runnable> tasks = new ArrayList<>(count);
        for (int i = 1; i < count * 2; i++) {
            int index = i;
            tasks.add(() -> blackhole.consume(index));
        }
        return tasks;
    }

    public static void main(String[] args) throws Exception {
        Options opts = new OptionsBuilder()
            .include(TaskTimeRecordPluginBenchmarkTest.class.getSimpleName())
            .resultFormat(ResultFormatType.JSON)
            .build();
        new Runner(opts).run();
    }

}
