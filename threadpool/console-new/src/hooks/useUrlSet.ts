import useUrlState from '@ahooksjs/use-url-state';

export const useUrlSet = (options: { form: any }) => {
  const { form } = options;
  const [state, setState] = useUrlState({});

  const setUrl = () => {
    const params = form.getFieldsValue();
    Object.keys(params).forEach(key => {
      if (!params[key]) {
        params[key] = undefined;
      }
    });
    setState({ ...params });
  };

  return { setUrl };
};
