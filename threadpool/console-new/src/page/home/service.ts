const fetchData = async () => {
  await new Promise(resolve => {
    resolve(100);
  });
};

const service = { fetchData };

export default service;
