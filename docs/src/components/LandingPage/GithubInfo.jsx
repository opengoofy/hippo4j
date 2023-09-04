import React from "react";
import useGithubInfo from "./useGithubInfo";

const GithubInfo = ({ owner, repo }) => {
  const { favorites, language, forks, license } = useGithubInfo(owner, repo);

  return (
    <div className="github-info-container w-full md:justify-normal mb-4 justify-center gap-2 flex flex-wrap  items-center">
      <div className="flex items-center">
        <div className="dark:bg-gray-600 bg-gray-100 px-2">
          <span className="text-sm">star</span>
        </div>
        <div className="dark:bg-blue-600 bg-gray-200 font-medium px-2">
          <span id="repo-stars-count">{favorites || 4621}</span>
        </div>
      </div>

      <div className="flex items-center">
        <div className="px-2 bg-gray-100 dark:bg-gray-600">
          <span className="text-sm">language</span>
        </div>
        <div className="px-2 dark:bg-blue-600 bg-gray-200 font-medium">
          <span id="repo-languages-count font-medium">
            {language || "java"}
          </span>
        </div>
      </div>

      <div className="flex items-center">
        <div className="dark:bg-gray-600 bg-gray-100 px-2">
          <span className="text-sm">forks</span>
        </div>
        <div className="dark:bg-blue-600 bg-gray-200 px-2 font-medium">
          <span id="repo-forks-count">{forks || 1020}</span>
        </div>
      </div>

      <div className="flex items-center">
        <div className="px-2 bg-gray-100 dark:bg-gray-600">
          <span className="text-sm">license</span>
        </div>
        <div className="px-2 dark:bg-blue-600 bg-gray-200">
          <span className="text-sm font-medium" id="repo-license-name">
            Apache 2
          </span>
        </div>
      </div>
    </div>
  );
};

export default GithubInfo;
