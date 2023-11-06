import React from 'react';
import { useEffect } from 'react';
import { Icon } from '@iconify/react';
import useBaseUrl from '@docusaurus/useBaseUrl';
import GithubInfo from './GithubInfo';
import Translate, { translate } from '@docusaurus/Translate';
import Link from '@docusaurus/Link';
const Hero = () => {
  return (
    <header className="hero hero--primary heroBanner_UJJx dark:bg-[#1B1B1D]">
      <div className="my-container dark:text-white">
        <div className="row">
          <div className="col col--6">
            <h1 className="hero__logo text-5xl font-medium dark:text-white">
              Hippo4j Thread Pool
            </h1>
            <p className="hero__subtitle">
              <Translate
                id="homepage.titleDescription2"
                description="The homepage title description"
              >
                Enhancing the Operational Assurance Capability for Business
                Systems Online.
              </Translate>
            </p>
            <div className="social_VnSH">
              <GithubInfo owner="opengoofy" repo="hippo4j" />
            </div>
            <div className="buttons_pzbO">
              {/* button group */}
              <div className="flex  w-full justify-center">
                <Link
                  to="/docs/user_docs/intro"
                  className="relative mr-4 w-32 text-center lg:w-48 hover:no-underline inline-flex items-center justify-start py-4  overflow-hidden font-semibold text-white transition-all duration-150 ease-in-out rounded  bg-blue-500 group"
                >
                  <span className="absolute bottom-0 left-0 w-full h-1 transition-all duration-150 ease-in-out bg-blue-600 group-hover:h-full"></span>

                  <span className="relative w-full text-center transition-colors duration-200 ease-in-out group-hover:text-white">
                    Start Learning
                  </span>
                </Link>

                <a
                  href="https://github.com/opengoofy/hippo4j"
                  className="relative w-32 hover:no-underline lg:w-48 inline-block text-lg group"
                >
                  <span className="relative z-10 block px-5 py-4 overflow-hidden font-medium leading-tight text-gray-800 transition-colors duration-300 ease-out border-2 border-gray-900 rounded-lg group-hover:text-white">
                    <span className="absolute inset-0 w-full h-full px-5 py-4 rounded-lg bg-gray-100"></span>
                    <span className="absolute left-0 w-52 h-48 -ml-2 transition-all duration-300 origin-top-right -rotate-90 -translate-x-full translate-y-12 bg-gray-900 group-hover:-rotate-180 ease"></span>
                    <span className="relative flex items-center justify-center">
                      {' '}
                      <Icon
                        className="w-6 h-6 mr-2 rounded-full flex-shrink-0 bg-white text-white"
                        icon="devicon:github"
                      />
                      Github
                    </span>
                  </span>
                  <span
                    className="absolute bottom-0 right-0 w-full h-12 -mb-1 -mr-1 transition-all duration-200 ease-linear bg-gray-900 rounded-lg group-hover:mb-0 group-hover:mr-0"
                    data-rounded="rounded-lg"
                  ></span>
                </a>
              </div>
            </div>
          </div>
          <div className="col col--6">
            <h1 className="hero__title">
              <img
                className="homeImg_cEyn max-w-[440px] mt-12 -ml-8"
                src={useBaseUrl('/img/hero/hero-removebg.png')}
                alt="homepage"
              />
            </h1>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Hero;
