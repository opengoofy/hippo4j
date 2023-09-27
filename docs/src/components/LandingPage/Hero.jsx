import React from "react";
import { Icon } from "@iconify/react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import GithubInfo from "./GithubInfo";
import Translate, { translate } from "@docusaurus/Translate";
import Link from "@docusaurus/Link";

const Hero = () => {
  const bgUrl = useBaseUrl("/img/bg.jpg");

  return (
    <div className=" h-[100vh] lg:h-[65vh]  relative text-black dark:text-white">
      <div
        // style={{ backgroundImage: `url(${bgUrl})` }}
        className=" absolute inset-0 overflow-hidden bg-repeat"
      >
        {/* <div className="absolute inset-0 bg-gray-400 opacity-5 dark:opacity-0"></div> */}

        <div className=" container px-20 py-12 lg:py-16  md:py-8 lg:px-20 md:px-10  mx-auto flex flex-wrap flex-col lg:flex-row items-center">
          {/* <!--Left Col--> */}
          <div className="flex flex-col w-full lg:w-1/2 md:w-2/3 justify-center items-start text-center md:text-left">
            {/* title and desc */}
            <div>
              <h1 className="my-4 font-jakarta lg:my-4 md:my-4 text-5xl font-bold leading-tight ">
                Hippo4j ThreadPool
              </h1>

              {/* <p className="leading-normal dark:text-white font-bold text-gray-800 hero-image-url text-3xl md:text-3xl">
                <Translate
                  id="homepage.titleDescription1"
                  description="The homepage title description"
                >
                  Thread Pool Framework For Java
                </Translate>
              </p> */}
              <p className="leading-normal hero-image-url text-gray-600 dark:text-gray-100 text-xl mb-0">
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
              {/* <Link to="/docs/user_docs/intro" className="">
                <button className="mr-2 lg:mr-4 text-base lg:text-lg w-32 lg:w-40 hover:bg-blue-500 bg-blue-400 font-medium  py-2 px-4 rounded-md focus:outline-none shadow-none border-none cursor-pointer transition-all duration-300 ease-in-out">
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
                <button className="ml-2 lg:mx-0 w-32 lg:w-40 border-1 border-solid dark:border-gray-500 border-gray-400 bg-transparent hover:bg-gray-400 hover:bg-opacity-50 font-medium py-2 px-4 rounded-md focus:outline-none shadow-none cursor-pointer transition-all duration-300 ease-in-out">
                  <div className="flex cursor-pointer items-center justify-center">
                    <Icon
                      className="w-6 h-6 mr-2 rounded-full flex-shrink-0 dark:bg-white"
                      icon="devicon:github"
                    />
                    <span className="text-base lg:text-lg">GitHub</span>
                  </div>
                </button>
              </a> */}
              <Link
                to="/docs/user_docs/intro"
                class="relative mr-4 w-32 text-center lg:w-48 hover:no-underline inline-flex items-center justify-start py-3  overflow-hidden font-semibold text-white transition-all duration-150 ease-in-out rounded  bg-blue-500 group"
              >
                <span class="absolute bottom-0 left-0 w-full h-1 transition-all duration-150 ease-in-out bg-blue-600 group-hover:h-full"></span>
                {/* <span class="absolute right-0 pr-4 duration-200 ease-out group-hover:translate-x-12">
                  <svg
                    class="w-5 h-5 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M14 5l7 7m0 0l-7 7m7-7H3"
                    ></path>
                  </svg>
                </span> */}
                {/* <span class="absolute left-0 pl-2.5 -translate-x-12 group-hover:translate-x-0 ease-out duration-200">
                  <svg
                    class="w-5 h-5 text-gray-200"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M14 5l7 7m0 0l-7 7m7-7H3"
                    ></path>
                  </svg>
                </span> */}
                <span class="relative w-full text-center transition-colors duration-200 ease-in-out group-hover:text-white">
                  Start Learning
                </span>
              </Link>

              <a
                href="https://github.com/opengoofy/hippo4j"
                class="relative w-32 hover:no-underline lg:w-48 inline-block text-lg group"
              >
                <span class="relative z-10 block px-5 py-3 overflow-hidden font-medium leading-tight text-gray-800 transition-colors duration-300 ease-out border-2 border-gray-900 rounded-lg group-hover:text-white">
                  <span class="absolute inset-0 w-full h-full px-5 py-3 rounded-lg bg-gray-50"></span>
                  <span class="absolute left-0 w-48 h-48 -ml-2 transition-all duration-300 origin-top-right -rotate-90 -translate-x-full translate-y-12 bg-gray-900 group-hover:-rotate-180 ease"></span>
                  <span class="relative flex items-center justify-center">
                    {" "}
                    <Icon
                      className="w-6 h-6 mr-2 rounded-full flex-shrink-0 bg-white text-white"
                      icon="devicon:github"
                    />
                    Github
                  </span>
                  {/* <div className="flex cursor-pointer items-center justify-center">
                    <Icon
                      className="w-6 h-6 mr-2 rounded-full flex-shrink-0 dark:bg-white"
                      icon="devicon:github"
                    />
                    <span className="text-base lg:text-lg">GitHub</span>
                  </div> */}
                </span>
                <span
                  class="absolute bottom-0 right-0 w-full h-12 -mb-1 -mr-1 transition-all duration-200 ease-linear bg-gray-900 rounded-lg group-hover:mb-0 group-hover:mr-0"
                  data-rounded="rounded-lg"
                ></span>
              </a>
            </div>
            {/* github info */}
            <div className="github-info w-full">
              <GithubInfo owner="opengoofy" repo="hippo4j" />
            </div>

            {/* button test */}
            <div className="flex"></div>
          </div>

          {/* <!--Right image--> */}
          <div className="w-full lg:w-1/2 text-center lg:block ">
            <img
              className="hero-img w-full h-auto object-cover lg:h-80 md:h-96 dark:rounded-lg dark:shadow-lg dark:filter-brightness-75"
              // src={useBaseUrl("/img/hero-removebg.png")}
              alt="Hippo4j System"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Hero;
