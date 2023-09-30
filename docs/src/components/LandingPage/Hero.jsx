import React from "react";
import { Icon } from "@iconify/react";
import useBaseUrl from "@docusaurus/useBaseUrl";
import GithubInfo from "./GithubInfo";
import Translate, { translate } from "@docusaurus/Translate";
import Link from "@docusaurus/Link";

const Hero = () => {
  const bgUrl = useBaseUrl("/img/bg.jpg");

  return (
    <div className=" h-[100vh] lg:h-[58vh]  relative text-black dark:text-white">
      <div className=" absolute inset-0 overflow-hidden bg-repeat">
        {/* <div className="absolute inset-0 bg-gray-400 opacity-5 dark:opacity-0"></div> */}

        <div className=" container px-20 py-12 lg:py-8  md:py-8 lg:px-20 md:px-10  mx-auto flex flex-wrap flex-col lg:flex-row items-center">
          {/* <!--Left Col--> */}
          <div className="flex flex-col w-full lg:w-1/2 md:w-2/3 justify-center items-start text-center md:text-left">
            {/* title and desc */}
            <div>
              <h1 className="my-4 font-jakarta lg:my-8 md:my-4 text-5xl font-bold leading-tight ">
                Hippo4j Thread Pool
              </h1>

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
              <Link
                to="/docs/user_docs/intro"
                class="relative mr-4 w-32 text-center lg:w-48 hover:no-underline inline-flex items-center justify-start py-3  overflow-hidden font-semibold text-white transition-all duration-150 ease-in-out rounded  bg-blue-500 group"
              >
                <span class="absolute bottom-0 left-0 w-full h-1 transition-all duration-150 ease-in-out bg-blue-600 group-hover:h-full"></span>

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
