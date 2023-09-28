import React from "react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import Translate from "@docusaurus/Translate";
const Introduction = () => {
  return (
    <section className="mb-28 dark:text-white">
      <div className="introduction-container ">
        <div className="title-container w-full bg-[#EFEFEF] p-4 mb-12 flex flex-wrap dark:bg-[#242526]">
          <div className="left-image-container px-24  w-full md:w-1/2 sm:w-1/3">
            <img
              className="w-full -mt-8 h-64 object-contain  lg:h-64 md:h-96 dark:rounded-lg "
              alt="Hippo4j System"
              src={useBaseUrl("/img/introduction/t2.svg")}
            />
            {/* <div className="introduction-title-image h-64 w-full img-div"></div> */}
          </div>
          <div className="right-title-container py-4 px-16 md:px-4 md:pt-8 w-full md:w-1/2 sm:w-2/3 sm:px-8">
            <h2 className="w-full my-2 text-3xl font-medium leading-tight  dark:text-white ">
              <Translate
                id="homepage.secondTitle"
                description="The title for the introduction part"
              >
                What can Hippo4j do?
              </Translate>
            </h2>

            <p className="leading-normal mt-8 max-w-xl  md:pr-10  dark:text-white  mb-2">
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
            <div class="w-full sm:w-1/3 md:w-1/4 ">
              <img
                className="w-full -mt-4 h-auto sm:h-64 object-contain  lg:h-52 md:h-72 dark:rounded-lg "
                src={useBaseUrl("/img/introduction/p11.svg")}
                alt="Hippo4j System"
              />
            </div>
            <div class="w-full pr-4 sm:w-2/3 md:w-1/2  flex-grow  ">
              <h3 class="text-2xl   dark:text-white font-normal leading-none ">
                <Translate
                  id="homepage.introduction.fristPartTitle"
                  description="the title for the first introduction part"
                >
                  Dynamic Change
                </Translate>
              </h3>
              <div class="dark:text-white  mb-8 ">
                <p>
                  <Translate
                    className="cursor-pointer "
                    id="homepage.introduction.fristPartDesc1"
                    description="The first desc for fristPart "
                  >
                    Application runtime dynamically changes thread pool
                    parameters.
                  </Translate>{" "}
                </p>
              </div>
            </div>
          </div>
          <div class="flex flex-wrap md:-mt16  flex-col-reverse sm:flex-row">
            <div class="w-full flex-grow sm:w-2/3 md:w-2/3 md:pl-[28rem] sm:pl-8 mt-6 lg:pt-8">
              <div class="align-middle ">
                <h3 class="text-2xl   dark:text-white font-normal leading-none mb-3">
                  <Translate
                    id="homepage.introduction.secondPartTitle"
                    description="the title for the second introduction part"
                  >
                    Custom Alarm
                  </Translate>
                </h3>
                <div class="dark:text-white  mb-8">
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
                </div>
              </div>
            </div>
            <div class="w-full   sm:w-1/3 md:w-1/4  mt-6">
              <img
                className="w-full h-auto sm:h-64 object-contain  lg:h-52 md:h-96 dark:rounded-lg "
                src={useBaseUrl("/img/introduction/p22.svg")}
                alt="Hippo4j System"
              />
            </div>
          </div>

          <div class="flex mt-2 flex-wrap">
            <div class="w-full sm:w-1/3 md:w-1/4 ">
              <img
                className="w-full -mt-4 h-auto sm:h-64 object-contain  lg:h-60 md:h-72 dark:rounded-lg "
                src={useBaseUrl("/img/introduction/p33.svg")}
                alt="Hippo4j System"
              />
            </div>
            <div class="w-full pr-4 sm:w-2/3 md:w-1/2 md:mt-8 flex-grow  ">
              <h3 class="text-2xl   dark:text-white font-normal  leading-none ">
                <Translate
                  id="homepage.introduction.thirdPartTitle"
                  description="the title for the third introduction part"
                >
                  Operation Monitoring
                </Translate>
              </h3>
              <div class="dark:text-white  mb-8">
                <p className="m-0">
                  <Translate
                    className="cursor-pointer"
                    id="homepage.introduction.thirdPartDesc1"
                    description="The first desc for thirdPart "
                  >
                    Supports custom duration thread pool for data collection and
                    storage.
                  </Translate>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Introduction;
