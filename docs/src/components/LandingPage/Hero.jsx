import React from "react";
import { Icon } from "@iconify/react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import GithubInfo from "./GithubInfo";
import Translate, { translate } from "@docusaurus/Translate";
import Link from "@docusaurus/Link";

const Hero = () => {
  return (
    <div className="hero-container text-black dark:text-white">
      <div className="py-10 mb-10">
        <div className="container px-20 lg:px-20 md:px-10  mx-auto flex flex-wrap flex-col md:flex-row items-center">
          {/* <!--Left Col--> */}
          <div className="flex flex-col w-full md:w-3/5 justify-center items-start text-center md:text-left">
            {/* title and desc */}
            <div>
              <h1 className="my-4 lg:my-8 md:my-6 text-5xl font-bold leading-tight text-transparent bg-gradient-to-r from-orange-400 via-orange-400 to-orange-500 bg-clip-text">
                Hippo4j
              </h1>

              <p className="leading-normal dark:text-white font-bold text-gray-800 md:pr-20 text-3xl md:text-3xl">
                <Translate
                  id="homepage.titleDescription1"
                  description="The homepage title description"
                >
                  Thread Pool Framework For Java
                </Translate>
              </p>
              <p className="leading-normal font-medium md:pr-20 text-gray-600 dark:text-gray-100 text-2xl mb-2">
                <Translate
                  id="homepage.titleDescription2"
                  description="The homepage title description"
                >
                  Enhancing the Operational Assurance Capability for Business
                  Systems Online.
                </Translate>
              </p>
            </div>

            {/* button group */}
            <div className="flex my-4 lg:my-8 md:my-6  w-full justify-center md:justify-start">
              <Link to="/docs/user_docs/intro" className="">
                <button className="mr-2 lg:mr-4 text-base lg:text-lg w-32 lg:w-40 hover:bg-orange-500 bg-orange-400 font-medium  py-2 px-4 rounded-md focus:outline-none shadow-none border-none cursor-pointer transition-all duration-300 ease-in-out">
                  <Translate
                    className="cursor-pointer"
                    id="homepage.startButton"
                    description="The homepage start button text"
                  >
                    Quick Start
                  </Translate>
                </button>
              </Link>
              <a href="https://github.com/opengoofy/hippo4j">
                <button className="ml-2 lg:mx-0 w-32 lg:w-40 border border-solid dark:border-gray-100 border-gray-200 bg-transparent hover:bg-gray-300 hover:bg-opacity-50 font-medium py-2 px-4 rounded-md focus:outline-none shadow-none cursor-pointer transition-all duration-300 ease-in-out">
                  <div className="flex cursor-pointer items-center justify-center">
                    <Icon
                      className="w-6 h-6 mr-2 rounded-full flex-shrink-0 dark:bg-white"
                      icon="devicon:github"
                    />
                    <span className="text-base lg:text-lg">GitHub</span>
                  </div>
                </button>
              </a>
            </div>
            {/* github info */}
            <div className="github-info w-full">
              <GithubInfo owner="opengoofy" repo="hippo4j" />
            </div>
          </div>

          {/* <!--Right image--> */}
          <div className="w-full md:w-2/5 text-center">
            <img
              className="w-full h-auto object-cover md:shadow-sm lg:h-80 md:h-96 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
              src={useBaseUrl("/img/hero.svg")}
              alt="Hippo4j System"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
