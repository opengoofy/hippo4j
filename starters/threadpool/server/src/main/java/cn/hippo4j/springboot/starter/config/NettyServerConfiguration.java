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

package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.model.*;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.ServerBareTakeHandler;
import cn.hippo4j.rpc.handler.ServerTakeHandler;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.server.Server;
import cn.hippo4j.rpc.server.ServerSupport;
import cn.hippo4j.springboot.starter.controller.ThreadPoolAdapterController;
import cn.hippo4j.springboot.starter.controller.WebThreadPoolController;
import cn.hippo4j.springboot.starter.controller.WebThreadPoolRunStateController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class NettyServerConfiguration {

    ServerPort serverPort;

    final BootstrapProperties properties;
    final ThreadPoolAdapterController threadPoolAdapterController;
    final WebThreadPoolController webThreadPoolController;
    final WebThreadPoolRunStateController webThreadPoolRunStateController;

    private static final String GET_POOL_BASE_STATE = "getPoolBaseState";
    private static final String GET_POOL_RUN_STATE = "getPoolRunState";
    private static final String UPDATE_WEB_THREAD_POOL = "updateWebThreadPool";
    private static final String GET_ADAPTER_THREAD_POOL = "getAdapterThreadPool";
    private static final String UPDATE_ADAPTER_THREAD_POOL = "updateAdapterThreadPool";
    private static final String GET_WEB_POOL_RUN_STATE = "getWebPoolRunState";
    private static final String GET_THREAD_STATE_DETAIL = "getThreadStateDetail";

    @PostConstruct
    public void nettyServerPort() throws IOException {
        if (Boolean.FALSE.equals(properties.getEnableRpc())) {
            return;
        }
        this.serverPort = new ServerRandomPort();
        // getPoolBaseState
        ServerTakeHandler<String, Result<ThreadPoolBaseInfo>> getPoolBaseState =
                new ServerTakeHandler<>(GET_POOL_BASE_STATE, webThreadPoolController::getPoolBaseState);
        // getPoolRunState
        ServerBareTakeHandler<Result<ThreadPoolRunStateInfo>> getPoolRunState =
                new ServerBareTakeHandler<>(GET_POOL_RUN_STATE, webThreadPoolController::getPoolRunState);
        // updateWebThreadPool
        ServerTakeHandler<ThreadPoolParameterInfo, Result<Void>> updateWebThreadPool =
                new ServerTakeHandler<>(UPDATE_WEB_THREAD_POOL, webThreadPoolController::updateWebThreadPool);
        // getAdapterThreadPool
        ServerTakeHandler<ThreadPoolAdapterParameter, Result<ThreadPoolAdapterState>> getAdapterThreadPool =
                new ServerTakeHandler<>(GET_ADAPTER_THREAD_POOL, threadPoolAdapterController::getAdapterThreadPool);
        // updateAdapterThreadPool
        ServerTakeHandler<ThreadPoolAdapterParameter, Result<Void>> updateAdapterThreadPool =
                new ServerTakeHandler<>(UPDATE_ADAPTER_THREAD_POOL, threadPoolAdapterController::updateAdapterThreadPool);
        // getWebPoolRunState
        ServerTakeHandler<String, Result<ThreadPoolRunStateInfo>> getWebPoolRunState =
                new ServerTakeHandler<>(GET_WEB_POOL_RUN_STATE, webThreadPoolRunStateController::getPoolRunState);
        // getThreadStateDetail
        ServerTakeHandler<String, Result<List<ThreadDetailStateInfo>>> getThreadStateDetail =
                new ServerTakeHandler<>(GET_THREAD_STATE_DETAIL, webThreadPoolRunStateController::getThreadStateDetail);

        try (
                SimpleServerConnection connection = new SimpleServerConnection(
                        getPoolBaseState,
                        getPoolRunState,
                        updateWebThreadPool,
                        getAdapterThreadPool,
                        updateAdapterThreadPool,
                        getWebPoolRunState,
                        getThreadStateDetail);
                Server server = new ServerSupport(serverPort, connection)) {
            if (log.isInfoEnabled()) {
                log.info("Netty server started, binding to port {}", serverPort.getPort());
            }
            server.bind();
        }
    }

    public int getServerPort() {
        return serverPort == null ? 0 : serverPort.getPort();
    }

    /**
     * Safely create random ports
     */
    static class ServerRandomPort implements ServerPort {

        final int port = getRandomPort();

        @Override
        public int getPort() {
            return port;
        }

        private int getRandomPort() {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
