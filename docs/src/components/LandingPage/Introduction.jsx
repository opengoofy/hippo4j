import React from "react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import Translate from "@docusaurus/Translate";
const Introduction = () => {
  return (
    <section className=" border-b mt-4  mb-28 dark:text-white">
      <div className=" ">
        <div className="title-container w-full bg-gray-100 p-4 mb-4 flex flex-wrap dark:bg-gray-700">
          <div className="left-image-container w-full md:w-1/2 sm:w-1/3">
            <img
              className="w-full h-64 object-cover md:shadow-sm lg:h-80 md:h-96 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
              alt="Hippo4j System"
              src={useBaseUrl("/img/introduction/title-image.svg")}
            />
          </div>
          <div className="right-title-container py-4 px-16 md:px-4 md:pt-8 w-full md:w-1/2 sm:w-2/3 sm:px-8">
            <h2 className="w-full my-2 text-3xl font-large leading-tight text-transparent bg-gradient-to-r from-orange-400 via-orange-500 to-orange-600 bg-clip-text">
              <Translate
                id="homepage.secondTitle"
                description="The title for the introduction part"
              >
                What can Hippo4j do?
              </Translate>
            </h2>
            <p className=" my-4 leading-relaxed dark:text-white text-xl font-medium text-gray-800 md:pr-20 ">
              <Translate
                id="homepage.secondTitleDesc1"
                description="The p1 for the secondTitle part"
              >
                Hippo4j is a Java thread pool framework.
              </Translate>
            </p>
            <p className="leading-normal max-w-xl  md:pr-10 text-gray-600 dark:text-gray-100  mb-2">
              <Translate
                id="homepage.secondTitleDesc2"
                description="The p2 for the secondTitle part"
              >
                Provided functions such as dynamic adjustment of thread pool,
                custom alerts, and operation monitoring to help improve the
                operational support capabilities of business systems.
              </Translate>
            </p>
          </div>
        </div>

        <div class="container px-20 lg:px-20 md:px-10 three-part-container">
          <div class="flex flex-wrap pt-6">
            <div class="w-full pr-4 sm:w-2/3 md:w-1/2   ">
              <h3 class="text-2xl md:pb-6 text-gray-800 dark:text-white font-medium leading-none mb-3">
                <Translate
                  id="homepage.introduction.fristPartTitle"
                  description="the title for the first introduction part"
                >
                  Dynamic Change
                </Translate>
              </h3>
              <div class="dark:text-white text-gray-600 mb-8">
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.fristPartDesc1"
                    description="The first desc for fristPart "
                  >
                    Application runtime dynamically changes thread pool
                    parameters.
                  </Translate>{" "}
                </p>
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.fristPartDesc2"
                    description="The second desc for fristPart "
                  >
                    Including but not limited to core size, maximum threads,
                    blocking queue size, and rejection policy.
                  </Translate>
                </p>
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.fristPartDesc3"
                    description="The third desc for fristPart "
                  >
                    It supports differentiated thread pool configurations for
                    different nodes in the application cluster.
                  </Translate>
                </p>
              </div>
            </div>
            <div class="w-full sm:w-1/3 md:w-1/2 ">
              {/* <img class="mx-auto w-4/5 z-50" src="f1.png" /> */}
              <img
                className="w-full h-auto sm:h-64 object-cover md:shadow-sm lg:h-80 md:h-72 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
                src={useBaseUrl("/img/introduction/p1.svg")}
                alt="Hippo4j System"
              />
            </div>
          </div>
          <div class="flex flex-wrap pt-6 flex-col-reverse sm:flex-row">
            <div class="w-full sm:w-1/3 md:w-1/2  mt-6">
              <img
                className="w-full h-auto sm:h-64 object-cover md:shadow-sm lg:h-80 md:h-96 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
                src={useBaseUrl("/img/introduction/p2.svg")}
                alt="Hippo4j System"
              />
            </div>
            <div class="w-full sm:w-2/3 md:w-1/2 md:pl-8 sm:pl-8 mt-6">
              <div class="align-middle">
                <h3 class="text-2xl md:pb-6 text-gray-800 dark:text-white font-medium leading-none mb-3">
                  <Translate
                    id="homepage.introduction.secondPartTitle"
                    description="the title for the second introduction part"
                  >
                    Custom Alarm
                  </Translate>
                </h3>
                <div class="dark:text-white text-gray-600 mb-8">
                  <p>
                    <Translate
                      className="cursor-pointer"
                      id="homepage.introduction.secondPartDesc1"
                      description="The first desc for secondPart "
                    >
                      Application thread pool runtime point, providing four
                      alarm dimensions.
                    </Translate>
                  </p>
                  <p>
                    <Translate
                      className="cursor-pointer"
                      id="homepage.introduction.secondPartDesc2"
                      description="The second desc for secondPart "
                    >
                      Thread pool overload, blocking queue capacity, running for
                      too long, and rejection strategy alarm.
                    </Translate>{" "}
                  </p>
                  <p>
                    <Translate
                      className="cursor-pointer"
                      id="homepage.introduction.secondPartDesc3"
                      description="The third desc for secondPart "
                    >
                      It also supports non-repetitive alarms within a custom
                      time period.
                    </Translate>
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div class="flex flex-wrap pt-6">
            <div class="w-5/6 sm:w-2/3 md:w-1/2 sm:pr-8 md:pr-4">
              <h3 class="text-2xl md:py-6 text-gray-800 dark:text-white font-medium leading-none mb-3">
                <Translate
                  id="homepage.introduction.thirdPartTitle"
                  description="the title for the third introduction part"
                >
                  Operation Monitoring
                </Translate>
              </h3>
              <div class="dark:text-white text-gray-600 mb-8 md:pr-6">
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.thirdPartDesc1"
                    description="The first desc for thirdPart "
                  >
                    Supports custom duration thread pool for data collection and
                    storage.
                  </Translate>
                </p>
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.thirdPartDesc2"
                    description="The second desc for thirdPart "
                  >
                    while also supporting Prometheus, InfluxDB, and other
                    monitoring systems.
                  </Translate>
                </p>
                <p>
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.thirdPartDesc3"
                    description="The third desc for thirdPart "
                  >
                    Provides visualized dashboard monitoring metrics through
                    Grafana or built-in monitoring pages.
                  </Translate>
                </p>
              </div>
            </div>
            <div class="w-full sm:w-1/3 md:w-1/2 ">
              {/* <img class="mx-auto w-4/5 z-50" src="f3_r.png" /> */}
              <img
                className="w-full h-auto sm:h-64 object-cover md:shadow-sm lg:h-80 md:h-96 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
                src={useBaseUrl("/img/introduction/p3.svg")}
                alt="Hippo4j System"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Introduction;
