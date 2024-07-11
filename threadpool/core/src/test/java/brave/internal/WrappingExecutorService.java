/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brave.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class WrappingExecutorService implements ExecutorService {

    protected WrappingExecutorService() {
    }

    protected abstract ExecutorService delegate();

    protected abstract <R> Callable<R> wrap(Callable<R> var1);

    protected abstract Runnable wrap(Runnable var1);

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().awaitTermination(timeout, unit);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate().invokeAll(this.wrap(tasks));
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().invokeAll(this.wrap(tasks), timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate().invokeAny(this.wrap(tasks));
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().invokeAny(this.wrap(tasks), timeout, unit);
    }

    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }

    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }

    public void shutdown() {
        this.delegate().shutdown();
    }

    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }

    public void execute(Runnable task) {
        this.delegate().execute(this.wrap(task));
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.delegate().submit(this.wrap(task));
    }

    public Future<?> submit(Runnable task) {
        return this.delegate().submit(this.wrap(task));
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate().submit(this.wrap(task), result);
    }

    <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        ArrayList<Callable<T>> result = new ArrayList(tasks.size());
        Iterator var3 = tasks.iterator();

        while (var3.hasNext()) {
            Callable<T> task = (Callable) var3.next();
            result.add(this.wrap(task));
        }

        return result;
    }
}
