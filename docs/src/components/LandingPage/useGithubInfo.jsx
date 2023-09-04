import { useState, useEffect } from "react";

const useGithubInfo = (owner, repo) => {
  const [favorites, setFavorites] = useState(0);
  const [language, setLanguage] = useState("");
  const [forks, setForks] = useState(0);
  const [license, setLicense] = useState("");

  useEffect(() => {
    fetch(`https://api.github.com/repos/${owner}/${repo}`)
      .then((response) => response.json())
      .then((data) => {
        setFavorites(data.stargazers_count);
        setLanguage(data.language);
        setForks(data.forks_count);
        setLicense(data.license?.name ?? "");
      })
      .catch((error) => {
        console.error("Error fetching API data:", error);
      });
  }, [owner, repo]);

  return {
    favorites,
    language,
    forks,
    license,
  };
};
export default useGithubInfo;
