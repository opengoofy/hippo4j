// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'HIPPO-4J 文档',
    tagline: '动态可观测线程池框架，为业务系统提高线上运行保障能力',
    url: 'https://hippo4j.cn',
    baseUrl: '/',
    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',
    favicon: 'img/favicon.png',
    // GitHub pages deployment config.
    // If you aren't using GitHub pages, you don't need these.
    organizationName: 'hippo4j', // Usually your GitHub org/user name.
    projectName: 'hippo4j.github.io', // Usually your repo name.
    deploymentBranch: "main",

    // Even if you don't use internalization, you can use this field to set useful
    // metadata like html lang. For example, if your site is Chinese, you may want
    // to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'zh-CN',
        locales: ['zh-CN'],
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
                    sidebarCollapsed: false,
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

    themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
            autoCollapseCategories: true,
            announcementBar: {
                id: 'announcementBar-1', // Increment on change
                // content: `⭐️ If you like hippo4j, give it a star on <a target="_blank" rel="noopener noreferrer" href="https://gitee.com/mabaiwancn/hippo4j">Gitee</a>, thanks.`,
                content: `⭐️ 如果您喜欢 hippo4j，请在 <a target="_blank" rel="noopener noreferrer" href="https://gitee.com/mabaiwancn/hippo4j">Gitee</a> 或 <a target="_blank" rel="noopener noreferrer" href="https://github.com/opengoofy/hippo4j">GitHub</a> 上给它一个 star，谢谢！`,
                // content: `<a target="_blank" rel="noopener noreferrer" href="https://xiaomage.info/knowledge-planet/">👉 《小马哥的代码实战课》官方知识星球来啦！！！</a>`,
            },
            navbar: {
                title: '4J',
                logo: {
                    alt: 'HIPPO-4J 动态可观测线程池框架',
                    src: 'img/web.png',
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
                        type: 'docSidebar',
                        docId: 'contributor',
                        position: 'left',
                        sidebarId: 'community',
                        label: '社区',
                    },
                    {to: '/blog', label: '博客', position: 'left'},
                    /*{
                        type: 'docSidebar',
                        docId: 'intro',
                        position: 'left',
                        sidebarId: 'sponsor',
                        hideable: true,
                        label: '支持开源',
                    },*/
                    {
                        href: 'https://xiaomage.info/knowledge-planet',
                        label: '🥇知识星球',
                        position: 'right',
                    },
                    /* 国际化 */
                    /*{type: 'localeDropdown', position: 'right'},*/
                    {
                        href: 'http://console.hippo4j.cn/index.html',
                        label: '控制台样例',
                        position: 'right',
                    },
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
                                label: '简介',
                                to: '/docs/user_docs/intro',
                            },
                        ],
                    },
                    {
                        title: 'Community',
                        items: [
                            {
                                label: '加群沟通',
                                href: 'https://hippo4j.cn/docs/user_docs/other/group',
                            },
                            {
                                label: '微信公众号',
                                href: 'https://mp.weixin.qq.com/s/diVHYvwiuYH9aWpZDPc27g',
                            },
                        ],
                    },
                    {
                        title: 'More',
                        items: [
                            {
                                label: 'Gitee',
                                href: 'https://gitee.com/mabaiwancn/hippo4j',
                            },
                            {
                                label: 'GitHub',
                                href: 'https://github.com/opengoofy/hippo4j',
                            },
                            {
                                label: '公司登记',
                                href: 'https://github.com/opengoofy/hippo4j/issues/13',
                            },
                        ],
                    },
                ],
                copyright: `Copyright © 2021-2022 小马哥版权所有 <a href="https://beian.miit.gov.cn">京ICP备2021038095号-2
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
