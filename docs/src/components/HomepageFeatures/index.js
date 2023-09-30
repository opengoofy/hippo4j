import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: '动态变更',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        应用运行时动态变更线程池参数，包括不限于核心、最大线程、阻塞队列大小和拒绝策略等，支持应用集群下不同节点线程池配置差异化
      </>
    ),
  },
  {
    title: '自定义报警',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        应用线程池运行时埋点，提供四种报警维度，线程池过载、阻塞队列容量、运行超长以及拒绝策略报警，并支持自定义时间内不重复报警
      </>
    ),
  },
  {
    title: '运行监控',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        支持自定义时长线程池运行数据采集存储，同时也支持 Prometheus、InfluxDB 等采集监控，通过
        Grafana 或内置监控页面提供可视化大屏监控运行指标
      </>
    ),
  },
];

function Feature({ Svg, title, description }) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
