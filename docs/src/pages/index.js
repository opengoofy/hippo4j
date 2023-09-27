import React from "react";
import { useEffect } from "react";
import clsx from "clsx";
import Link from "@docusaurus/Link";
// import Translate from "@docusaurus/Translate";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import Layout from "@theme/Layout";
// import HomepageFeatures from "@site/src/components/HomepageFeatures";
import ExecutionEnvironment from "@docusaurus/ExecutionEnvironment";

import LandingLayout from "@site/src/components/LandingPage";

import styles from "./index.module.css";

if (ExecutionEnvironment.canUseDOM) {
  var _hmt = _hmt || [];
  (function () {
    var hm = document.createElement("script");
    hm.src = "https://hm.baidu.com/hm.js?473eaadc06f3d63771f303df1fc29b58";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s);
  })();
}

function HomepageHeader() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <header className={clsx("hero hero--primary", styles.heroBanner)}>
      <div className="container">
        <h1 className="hero__title">{siteConfig.title}</h1>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="/docs/user_docs/intro"
          >
            快速开始 - 5min ⏱️
          </Link>
        </div>
      </div>
    </header>
  );
}

export default function Home() {
  const { siteConfig } = useDocusaurusContext();
  // useEffect(() => {
  //   // Set the content of the banner based on the  URL.
  //   let inner = location.href.includes("3000")
  //     ? '⭐️ If you like hippo4j, give it a star on  &nbsp; <a target="_blank" rel="noopener noreferrer" href="https://github.com/opengoofy/hippo4j">GitHub</a> &nbsp;⭐️ '
  //     : '⭐️ 源不易，如果 Hippo4j 对您有帮助，请在 <a target="_blank" rel="noopener noreferrer" href="https://github.com/opengoofy/hippo4j">GitHub</a> 上给它一个 Star 🌟';
  //   let el = document.querySelector('[class^="announcementBar_"]');
  //   el.innerHTML = inner;
  // }, []);
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="Description will go into a meta tag in <head />"
    >
      {/* <HomepageHeader /> */}
      <main>
        {/* <HomepageFeatures /> */}
        <LandingLayout />
      </main>
    </Layout>
  );
}
