import React from "react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import Translate from "@docusaurus/Translate";
const Introduction = () => {
  return (
    <section className=" border-b mb-28">
      <div className="container px-20 lg:px-20 md:px-10 ">
        <h2 className="w-full my-2 text-5xl dark:text-white font-medium leading-tight text-gray-800">
          {/* Hippo4j能做什么 */}
          <Translate
            id="homepage.secondTitle"
            description="a title for introduciton part"
          >
            What can Hippo4j do?
          </Translate>
        </h2>
        <div className="w-1/2 mb-6">
          <div className=" h-1 w-64 my-0 py-0 rounded-t bg-gradient-to-r from-orange-400 to-blue-500 mb-4"></div>
        </div>

        <div className="third-part-desc grid md:grid-cols-2 grid-cols-1 gap-4">
          <div className="p-4 md:p-6 bg-gray-100  dark:bg-[#252525] rounded-lg">
            <h3 className="text-3xl text-gray-800 dark:text-white font-medium leading-none mb-3">
              {/* 动态变更 */}
              <Translate
                id="homepage.introduction.fristPartTitle"
                description="the title for the first introduction part"
              >
                Dynamic Change
              </Translate>
            </h3>
            <p className="text-gray-600 text-xl  dark:text-white ">
              {/* 应用运行时动态变更线程池参数，包括不限于核心、最大线程、阻塞队列大小和拒绝策略等，支持应用集群下不同节点线程池配置差异化 */}
              <Translate
                id="homepage.introduction.fristPartDesc"
                description="the desc for the first introduction part"
              >
                Application runtime dynamically changes thread pool parameters,
                including but not limited to core size, maximum threads,
                blocking queue size, and rejection policy. It supports
                differentiated thread pool configurations for different nodes in
                the application cluster.
              </Translate>
              <br />
              <br />
            </p>
          </div>
          <div className="p-4 md:p-6 bg-gray-100  dark:bg-[#252525] rounded-lg">
            <h3 className="text-3xl text-gray-800 dark:text-white font-medium leading-none mb-3">
              {/* 自定义报警 */}
              <Translate
                id="homepage.introduction.secondPartTitle"
                description="the title for the second introduction part"
              >
                Custom Alarm
              </Translate>
            </h3>
            <p className="text-gray-600 text-xl dark:text-white">
              {/* 应用线程池运行时埋点，提供四种报警维度，线程池过载、阻塞队列容量、运行超长以及拒绝策略报警，并支持自定义时间内不重复报警 */}
              <Translate
                id="homepage.introduction.secondPartDesc"
                description="the desc for the second introduction part"
              >
                Application thread pool runtime point, providing four alarm
                dimensions: thread pool overload, blocking queue capacity,
                running for too long, and rejection strategy alarm. It also
                supports non-repetitive alarms within a custom time period.
              </Translate>
              <br />
              <br />
            </p>
          </div>
          <div className="p-4 md:p-6 bg-gray-100  dark:bg-[#252525] rounded-lg">
            <h3 className="text-3xl text-gray-800 dark:text-white  font-medium leading-none mb-3">
              {/* 运行监控 */}
              <Translate
                id="homepage.introduction.thirdPartTitle"
                description="the title for the third introduction part"
              >
                Operation Monitoring
              </Translate>
            </h3>
            <p className="text-gray-600 text-xl dark:text-white">
              {/* 支持自定义时长线程池运行数据采集存储，同时也支持
      Prometheus、InfluxDB 等采集监控，通过 Grafana
      或内置监控页面提供可视化大屏监控运行指标 */}
              <Translate
                id="homepage.introduction.thirdPartDesc"
                description="the desc for the third introduction part"
              >
                Supports custom duration thread pool for data collection and
                storage, while also supporting Prometheus, InfluxDB, and other
                monitoring systems. Provides visualized dashboard monitoring
                metrics through Grafana or built-in monitoring pages.
              </Translate>
              <br />
              <br />
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Introduction;
