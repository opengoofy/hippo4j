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

package cn.hippo4j.rpc.process;

import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Processor chain for easier processing of processors in different scenarios<br>
 * reference resources: spring HandlerExecutionChain
 *
 * @see ActivePostProcess
 */
@Slf4j
public final class ActiveProcessChain {

    /**
     * A collection of processors that will be applied to their assigned programs.
     * Processors will perform different actions on different occasions for both the server and the client,
     * but the execution period of that action must be the same
     */
    List<ActivePostProcess> processes;

    /**
     * index <br>
     * that identifies where the {@link ActivePostProcess#preHandler(Request)} processing is performed<br>
     * This allows for the fact that some processors will add shutable operations to the class<br>
     * eg: {@link java.io.Closeable}, The {@link ActivePostProcess#afterCompletion(Request, Response, Exception)}
     * operation is not performed after an exception if the preprocessor is not executed
     */
    int index = -1;

    public ActiveProcessChain(List<ActivePostProcess> processes) {
        this.processes = processes;
    }

    public ActiveProcessChain(ActivePostProcess... processes) {
        this((processes != null ? Arrays.asList(processes) : Collections.emptyList()));
    }

    /**
     * Apply postHandle methods of registered processes.
     */
    public boolean applyPreHandle(Request request) {
        for (int i = 0; i < this.processes.size(); i++) {
            ActivePostProcess handle = processes.get(i);
            if (!handle.preHandler(request)) {
                afterCompletion(request, null, null);
                return false;
            }
            this.index = i;
        }
        return true;
    }

    /**
     * Apply postHandle methods of registered processes.
     */
    public void applyPostHandle(Request request, Response response) {
        for (int i = processes.size() - 1; i >= 0; i--) {
            ActivePostProcess handle = processes.get(i);
            handle.postHandler(request, response);
        }
    }

    /**
     * Trigger afterCompletion callbacks on the mapped ActivePostProcess.
     * Will just invoke afterCompletion for all interceptors whose preHandle invocation
     * has successfully completed and returned true.
     */
    public void afterCompletion(Request request, Response response, Exception ex) {
        for (int i = this.index; i >= 0; i--) {
            ActivePostProcess handle = processes.get(i);
            try {
                handle.afterCompletion(request, response, ex);
            } catch (Throwable e) {
                log.error("HandlerInterceptor.afterCompletion threw exception", e);
            }
        }
    }

}
