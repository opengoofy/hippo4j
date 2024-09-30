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

package cn.hippo4j.agent.plugin.spring.common.monitor;

import cn.hippo4j.agent.plugin.spring.common.conf.SpringBootConfig;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * This class is responsible for exposing Prometheus metrics via an HTTP endpoint.
 * It initializes the Prometheus registry, binds it to the global metrics registry,
 * and starts an HTTP server to serve the metrics data.
 */
public class MonitorMetricEndpoint {

    private static final ILog LOGGER = LogManager.getLogger(MonitorHandlersConfigurator.class);

    /**
     * Starts the Prometheus metrics HTTP server.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Initializes the PrometheusMeterRegistry with the default configuration.</li>
     *     <li>Binds the Prometheus registry to the global CompositeMeterRegistry.</li>
     *     <li>Attempts to start an HTTP server on the configured port to expose the Prometheus metrics.</li>
     * </ul>
     * If the port is not configured, or if there is an error starting the server, appropriate error messages
     * are logged, and the method returns without starting the server.
     * </p>
     */
    public static void startPrometheusEndpoint() {

        // Initialize the Prometheus registry
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // Bind the Prometheus registry to the global Metrics registry
        CompositeMeterRegistry globalRegistry = Metrics.globalRegistry;
        globalRegistry.add(prometheusRegistry);

        // Get the configured port for the Prometheus metrics HTTP server
        Integer port = SpringBootConfig.Spring.Dynamic.Thread_Pool.Monitor.AGENT_MICROMETER_PORT;
        if (port == null) {
            LOGGER.error(
                    "[Hippo4j-Agent] Failed to start Prometheus metrics endpoint server. Please configure the exposed endpoint by adding: spring.dynamic.thread-pool.monitor.agent-micrometer-port=xxx to the configuration file");
            return;
        }

        // Create the HTTP server
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            LOGGER.error("[Hippo4j-Agent] Failed to start Prometheus metrics endpoint server", e);
            return;
        }

        // Register the /actuator/prometheus context to handle metrics requests
        server.createContext("/actuator/prometheus", exchange -> {
            String response = prometheusRegistry.scrape(); // Get metrics data in Prometheus format
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        // Start the server
        server.start();
        LOGGER.info("[Hippo4j-Agent] Prometheus metrics server started on port {}", port);
    }

}
