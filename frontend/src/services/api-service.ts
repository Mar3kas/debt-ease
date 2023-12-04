import React from "react";
import { IApiError } from "../shared/models/ApiError";
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

const API_BASE_URL = "http://localhost:8080/api";

async function handleResponse<T>(
  response: AxiosResponse<T>
): Promise<T | IApiError | null> {
  if (!response.status) {
    const responseJson: IApiError = JSON.parse(response.request.response);

    if (responseJson.statusCode === 422) {
      if (
        responseJson.description.includes("JSON") ||
        responseJson.description.includes("CSV")
      ) {
        return responseJson;
      } else {
        const regex = /(\w+)\.(\w+): (.*?)(,|$)/g;
        let match;
        let messages = new Map<string, string>();

        while ((match = regex.exec(responseJson.description)) !== null) {
          messages.set(match[2], match[3]);
        }

        responseJson.description = JSON.stringify(Object.fromEntries(messages));

        return responseJson;
      }
    } else if (
      responseJson.statusCode === 401 ||
      responseJson.statusCode === 403
    ) {
      const mappedError = {
        statusCode: responseJson.statusCode,
        time: new Date(),
        message: responseJson.statusCode === 401 ? "Unauthorized" : "Forbidden",
        description:
          responseJson.statusCode === 401 ? "Unauthorized" : "Forbidden",
      };

      return mappedError;
    }

    return responseJson;
  } else if (response.status === 204) {
    const noContentError: IApiError = {
      statusCode: 204,
      time: new Date(),
      message: "No Content",
      description: "Deleted successfully",
    };

    return noContentError;
  } else if (response.data) {
    return response.data;
  }

  return JSON.parse(response.request.response);
}

const axiosConfig: AxiosRequestConfig = {
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
};

const buildUrl = (
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {}
) => {
  let url = `${API_BASE_URL}/${endpoint}`;

  Object.entries(pathVariables).forEach(([key, value]) => {
    const placeholder = `{${key}}`;
    url = url.replace(placeholder, String(value));
  });

  return url;
};

export const useGet = <T>(
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {},
  shouldRefetch: boolean = false
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<IApiError | null>(null);

  React.useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token");
        setLoading(true);
        setError(null);
        const url = buildUrl(endpoint, pathVariables);
        const response = await axios.get(url, {
          ...axiosConfig,
          headers: {
            ...axiosConfig.headers,
            Authorization: `Bearer ${token ?? ""}`,
          },
        });
        const result = await handleResponse<T>(response);
        setData(result as T);
      } catch (error: any) {
        const errorResult = await handleResponse<T>(error);
        setError(errorResult as IApiError);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [endpoint, shouldRefetch, ...Object.values(pathVariables)]);

  return { data, loading, error };
};

export const usePost = <T>(
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const postData = async <D>(postData?: D, isFormData = false) => {
    try {
      const token = localStorage.getItem("token");
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);

      const headers: AxiosRequestConfig["headers"] = {
        ...axiosConfig.headers,
        Authorization: `Bearer ${token ?? ""}`,
      };

      let requestBody: any = postData;

      if (postData instanceof FormData) {
        delete headers["Content-Type"];
      } else {
        requestBody = postData ? JSON.stringify(postData) : undefined;
        headers["Content-Type"] = "application/json";
      }

      const response = await axios.post(url, requestBody, {
        ...axiosConfig,
        headers,
      });

      const result = await handleResponse<T>(response);
      setData(result as T);
    } catch (error: any) {
      const errorResult = await handleResponse<T>(error);
      setError(errorResult as IApiError);
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, postData };
};

export const useEdit = <T>(
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const editData = async (postData: Record<string, any>) => {
    try {
      const token = localStorage.getItem("token");
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);
      const response = await axios.put(url, {
        ...axiosConfig,
        headers: {
          ...axiosConfig.headers,
          Authorization: `Bearer ${token ?? ""}`,
        },
      });
      const result = await handleResponse<T>(response);
      setData(result as T);
    } catch (error: any) {
      const errorResult = await handleResponse<T>(error);
      setError(errorResult as IApiError);
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, editData };
};

export const useDelete = <T>(
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const deleteData = async () => {
    try {
      const token = localStorage.getItem("token");
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);
      const response = await axios.delete(url, {
        ...axiosConfig,
        headers: {
          ...axiosConfig.headers,
          Authorization: `Bearer ${token ?? ""}`,
        },
      });
      const result = await handleResponse<T>(response);
      setData(result as T);
    } catch (error: any) {
      const errorResult = await handleResponse<T>(error);
      setError(errorResult as IApiError);
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, deleteData };
};