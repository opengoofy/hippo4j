import React from 'react';
import useGithubInfo from './useGithubInfo';

const GithubInfo = ({ owner, repo }) => {
  const { favorites, language, forks, license } = useGithubInfo(owner, repo);

  return (
    <p className="">
      <div className="widget">
        <a
          className="btn hover:no-underline hover:text-[#24292f] outline-none"
          href="https://github.com/opengoofy/hippo4j"
          rel="noopener"
          target="_blank"
          aria-label="Star buttons/github-buttons on GitHub"
        >
          <svg
            viewBox="0 0 16 16"
            width="14"
            height="14"
            className="octicon octicon-mark-github"
            aria-hidden="true"
          >
            <path d="M8 0c4.42 0 8 3.58 8 8a8.013 8.013 0 0 1-5.45 7.59c-.4.08-.55-.17-.55-.38 0-.27.01-1.13.01-2.2 0-.75-.25-1.23-.54-1.48 1.78-.2 3.65-.88 3.65-3.95 0-.88-.31-1.59-.82-2.15.08-.2.36-1.02-.08-2.12 0 0-.67-.22-2.2.82-.64-.18-1.32-.27-2-.27-.68 0-1.36.09-2 .27-1.53-1.03-2.2-.82-2.2-.82-.44 1.1-.16 1.92-.08 2.12-.51.56-.82 1.28-.82 2.15 0 3.06 1.86 3.75 3.64 3.95-.23.2-.44.55-.51 1.07-.46.21-1.61.55-2.33-.66-.15-.24-.6-.83-1.23-.82-.67.01-.27.38.01.53.34.19.73.9.82 1.13.16.45.68 1.31 2.69.94 0 .67.01 1.3.01 1.49 0 .21-.15.45-.55.38A7.995 7.995 0 0 1 0 8c0-4.42 3.58-8 8-8Z"></path>
          </svg>
          &nbsp;<span>Star</span>
        </a>
        <a
          className="social-count hover:no-underline outline-none w-10"
          href="https://github.com/opengoofy/hippo4j"
          rel="noopener"
          target="_blank"
          aria-label="1029 stargazers on GitHub"
        >
          {favorites || 0}
        </a>
      </div>
      <div class="widget ml-4">
        <a
          class="btn hover:no-underline hover:text-[#24292f] outline-none"
          href="https://github.com/opengoofy/hippo4j"
          rel="noopener"
          target="_blank"
          aria-label="Fork opengoofy/hippo4j on GitHub"
        >
          <svg
            viewBox="0 0 16 16"
            width="14"
            height="14"
            class="octicon octicon-repo-forked"
            aria-hidden="true"
          >
            <path d="M5 5.372v.878c0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75v-.878a2.25 2.25 0 1 1 1.5 0v.878a2.25 2.25 0 0 1-2.25 2.25h-1.5v2.128a2.251 2.251 0 1 1-1.5 0V8.5h-1.5A2.25 2.25 0 0 1 3.5 6.25v-.878a2.25 2.25 0 1 1 1.5 0ZM5 3.25a.75.75 0 1 0-1.5 0 .75.75 0 0 0 1.5 0Zm6.75.75a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Zm-3 8.75a.75.75 0 1 0-1.5 0 .75.75 0 0 0 1.5 0Z"></path>
          </svg>
          &nbsp;<span>Fork</span>
        </a>
        <a
          className="social-count hover:no-underline outline-none w-10"
          href="https://github.com/opengoofy/hippo4j"
          rel="noopener"
          target="_blank"
          aria-label="1029 stargazers on GitHub"
        >
          {forks || 0}
        </a>
      </div>
    </p>
  );
};

export default GithubInfo;
