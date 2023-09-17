import { useEffect, useMemo, useState } from 'react';
import { parse } from 'qs';
import useUrlState from '@ahooksjs/use-url-state';
import { FormInstance } from 'antd';

export function useFormStateToUrl<T extends Record<string, any>>(
  form: FormInstance,
  params?: T
): {
  urlState: any;
  isFirstMount: boolean;
  handleSetUrlState: () => void;
} {
  const [state, setState] = useState<any>();
  const [urlState, setUrlState] = useUrlState<any>();
  const [count, setCount] = useState<number>(0);

  useEffect(() => {
    const url = window.location.search.split('?')[1] ?? null;
    const urlParams = parse(url) as T;
    const result: Partial<T> = {};
    setState(urlParams);
    for (const key in params) {
      if (Object.prototype.hasOwnProperty.call(params, key)) {
        const paramValue = urlParams[key];
        if (paramValue ?? false) {
          if (typeof params[key] === 'number') {
            const parsedValue = parseFloat(paramValue);
            if (!isNaN(parsedValue)) {
              result[key] = parsedValue as T[keyof T];
            }
          } else {
            result[key] = paramValue as T[keyof T];
          }
        }
      }
    }
    form.setFieldsValue(result);
    setCount(count => count + 1);
  }, [setState, setCount, form, params]);

  const handleSetUrlState = () => {
    const values = form.getFieldsValue();
    setUrlState({ ...state, ...values });
  };

  const isFirstMount = useMemo(() => {
    return count === 1;
  }, [count]);

  return { urlState, isFirstMount, handleSetUrlState };
}
