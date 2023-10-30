import React from 'react';
import useBaseUrl from '@docusaurus/useBaseUrl';
import Translate from '@docusaurus/Translate';
const Introduction = () => {
  return (
    <section className="dark:text-white">
      <div className="introduction-container ">
        <div className="title-container  w-full  bg-[#EFEFEF] dark:text-white  flex flex-wrap dark:bg-[#242526]">
          <div className="my-container">
            <div className="row">
              <div className="col col--6  hfeatureImage_Eu74">
                <img
                  width="560"
                  height="315"
                  style={{ width: '560px', height: '315px' }}
                  src={useBaseUrl('/img/introduction/t.jpg')}
                />
              </div>
              <div className="col col--6 hfeatureContent_iAnb">
                <h3 className="hfeatureTitle_i6jH text-4xl font-medium mb-8">
                  <Translate
                    id="homepage.secondTitle"
                    description="The title for the introduction part"
                  >
                    What can Hippo4j do?
                  </Translate>
                </h3>
                <p>
                  <Translate
                    id="homepage.secondTitleDesc2"
                    description="The p1 for the secondTitle part"
                  >
                    Provided functions such as dynamic adjustment of thread
                    pool, custom alerts.
                  </Translate>
                </p>
                <p>
                  <Translate
                    id="homepage.secondTitleDesc3"
                    description="The p2 for the secondTitle part"
                  >
                    Help improve the operational support capabilities of your
                    business system.
                  </Translate>
                </p>
              </div>
            </div>
          </div>
        </div>
        <section className="section_rXKr dark:text-white">
          <div className="my-container">
            <div className="row">
              <div className="my-container">
                <div className="row feature_gF_R">
                  <div className="col col--3">
                    <div className="text--center">
                      <img
                        className="featureImage_FjLv"
                        src={useBaseUrl('/img/introduction/p11.svg')}
                        alt="feture-[object Object]"
                      />
                    </div>
                  </div>
                  <div className="col col--9 featureDesc_r01v">
                    <div>
                      <h2>
                        {' '}
                        <Translate
                          id="homepage.introduction.fristPartTitle"
                          description="the title for the first introduction part"
                        >
                          Dynamic Change
                        </Translate>
                      </h2>
                      <div>
                        <p>
                          <Translate
                            className=" "
                            id="homepage.introduction.fristPartDesc1"
                            description="The first desc for fristPart "
                          >
                            Application runtime dynamically changes thread pool
                            parameters.
                          </Translate>{' '}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="my-container">
                <div className="row feature_gF_R featureReverse_QJY9">
                  <div className="col col--3">
                    <div className="text--center">
                      <img
                        className="featureImage_FjLv"
                        src={useBaseUrl('/img/introduction/p22.svg')}
                        alt="feture-[object Object]"
                      />
                    </div>
                  </div>
                  <div className="col col--9 featureDesc_r01v">
                    <div>
                      <h2>
                        {' '}
                        <Translate
                          id="homepage.introduction.secondPartTitle"
                          description="the title for the second introduction part"
                        >
                          Custom Alarm
                        </Translate>
                      </h2>
                      <div>
                        <p>
                          <Translate
                            className="cursor-pointer"
                            id="homepage.introduction.secondPartDesc1"
                            description="The first desc for secondPart "
                          >
                            Application thread pool runtime point, providing
                            four alarm dimensions.
                          </Translate>
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="my-container">
                <div className="row feature_gF_R">
                  <div className="col col--3">
                    <div className="text--center">
                      <img
                        className="featureImage_FjLv"
                        src={useBaseUrl('/img/introduction/p33.svg')}
                        alt="feture-[object Object]"
                      />
                    </div>
                  </div>
                  <div className="col col--9 featureDesc_r01v">
                    <div>
                      <h2>
                        <Translate
                          id="homepage.introduction.thirdPartTitle"
                          description="the title for the third introduction part"
                        >
                          Operation Monitoring
                        </Translate>
                      </h2>
                      <div>
                        <p>
                          <Translate
                            className="cursor-pointer"
                            id="homepage.introduction.thirdPartDesc1"
                            description="The first desc for thirdPart "
                          >
                            Supports custom duration thread pool for data
                            collection and storage.
                          </Translate>
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </section>
  );
};

export default Introduction;
