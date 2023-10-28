// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Hippo4j',
  tagline: '动态可观测线程池框架，为业务系统提高线上运行保障能力',
  url: 'https://hippo4j.cn',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/hippo4j_favicon.ico',
  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'hippo4j', // Usually your GitHub org/user name.
  projectName: 'hippo4j.github.io', // Usually your repo name.
  deploymentBranch: 'main',

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'zh',
    locales: ['en', 'zh'],
    localeConfigs: {
      en: {
        label: 'English',
        direction: 'ltr',
      },
      zh: {
        label: '简体中文',
        direction: 'ltr',
      },
    },
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // Do not automatically expand the menu
          sidebarCollapsed: true,
          /*editUrl: 'https://github.com/longtai-cn',*/
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  plugins: [
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'community',
        path: 'community',
        routeBasePath: 'community',
        sidebarPath: require.resolve('./sidebarsCommunity.js'),
      },
    ],
    async function myPlugin(context, options) {
      return {
        name: 'docusaurus-tailwindcss',
        configurePostCss(postcssOptions) {
          // Appends TailwindCSS and AutoPrefixer.
          postcssOptions.plugins.push(require('tailwindcss'));
          postcssOptions.plugins.push(require('autoprefixer'));
          return postcssOptions;
        },
      };
    },
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      autoCollapseCategories: true,
      announcementBar: {
        id: 'announcementBar-1', // Increment on change
        content:
          '⭐️ If you like hippo4j, give it a star on <a target="_blank" rel="noopener noreferrer" href="https://github.com/opengoofy/hippo4j">GitHub</a> ⭐️',
        backgroundColor: '#fafbfc',
        textColor: '#091E42',
        isCloseable: false,
      },
      navbar: {
        title: '',
        logo: {
          alt: 'Hippo4j 动态可观测线程池框架',
          src: 'img/hippo4j.png',
        },
        items: [
          {
            type: 'docSidebar',
            docId: 'intro',
            position: 'left',
            sidebarId: 'user_docs',
            label: '文档',
          },
          {
            to: '/community/contributor-guide',
            label: '社区',
            position: 'left',
            activeBaseRegex: `/community/`,
          },
          /*{ to: "/team", label: "团队", position: "left" },*/
          { to: '/users', label: '采用公司', position: 'left' },
          { to: '/group', label: '加群沟通', position: 'left' },
          /*{to: '/blog', label: '博客', position: 'left'},*/
          {
            href: 'http://console.hippo4j.cn/index.html',
            label: '控制台样例',
            position: 'left',
          },
          {
            href: 'https://magestack.cn',
            label: '🚀 拿个offer',
            position: 'left',
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownActiveClassDisabled: true,
          },

          { type: 'localeDropdown', position: 'right' },
          /*{
                                  href: 'https://gitee.com/mabaiwancn/hippo4j',
                                  label: 'Gitee',
                                  position: 'right',
                              },*/
          {
            href: 'https://github.com/opengoofy/hippo4j',
            className: 'header-github-link',
            'aria-label': 'GitHub repository',
            position: 'right',
          },

          /*{
                                  href: 'https://github.com/opengoofy/hippo4j',
                                  label: 'GitHub',
                                  position: 'right',
                              },*/
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Intro',
                to: '/docs/user_docs/intro',
              },
              {
                label: 'Config Mode',
                to: '/docs/user_docs/getting_started/config/hippo4j-config-start',
              },
              {
                label: 'Server Mode',
                to: '/docs/user_docs/getting_started/server/hippo4j-server-start',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Group',
                href: 'https://hippo4j.cn/group',
              },
              {
                label: 'WeChat',
                href: 'https://mp.weixin.qq.com/s/diVHYvwiuYH9aWpZDPc27g',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Gitee',
                href: 'https://gitee.com/opengoofy/hippo4j',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/opengoofy/hippo4j',
              },
            ],
          },
          {
            title: 'Links',
            items: [
              {
                label: '推广合作',
                href: 'https://hippo4j.cn/docs/user_docs/other/operation',
              },
            ],
          },
        ],
        copyright: `Copyright © 2021-2023 马丁版权所有 <a href="https://beian.miit.gov.cn">京ICP备2021038095号
</a>`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['java'],
      },
    }),
};

module.exports = config;
