import React from "react";
import { IApiError } from "../shared/models/api-error";
import axios, { AxiosRequestConfig, AxiosResponse } from "axios";
import AuthService from "./jwt-service";

const API_BASE_URL = "http://localhost:8080/api";

async function handleResponse<T>(
  response: AxiosResponse<T>
): Promise<T | IApiError | null> {
  if (!response.status) {
    if (response.request.status === 401 || response.request.status === 403) {
      const mappedError = {
        statusCode: response.request.status,
        time: new Date(),
        message: response.request.status === 401 ? "Unauthorized" : "Forbidden",
        description:
          response.request.status === 401 ? "Unauthorized" : "Forbidden",
      };

      return mappedError;
    }
    const responseJson: IApiError = JSON.parse(response.request.response);
    if (responseJson.statusCode === 422) {
      if (
        responseJson.description.includes("JSON") ||
        responseJson.description.includes("CSV") ||
        responseJson.description.includes("Refresh Token") ||
        responseJson.description.includes("file")
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
  shouldRefetch: boolean = true,
  shouldFetchInitial: boolean = false,
  responseType: "json" | "blob" = "json"
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<IApiError | null>(null);

  const fetchData = async (shouldRefetch: boolean = false) => {
    const authService = AuthService.getInstance();
    try {
      setLoading(true);
      setError(null);

      if (authService.isTokenExpired() && !endpoint.includes("files")) {
        const refreshToken = authService.getRefreshToken();
        try {
          const response = await axios.post(
            `${API_BASE_URL}/refresh`,
            { refreshToken },
            axiosConfig
          );
          authService.decodeToken(response.data.accessToken);
        } catch (refreshError: any) {
          const errorResult = await handleResponse<T>(refreshError);
          setError(errorResult as IApiError);
          authService.clear();
          return;
        }
      }

      const url = buildUrl(endpoint, pathVariables);
      const response = await axios.get(url, {
        ...axiosConfig,
        headers: {
          ...axiosConfig.headers,
          Authorization: `Bearer ${authService.getToken() ?? ""}`,
        },
        responseType: responseType,
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

  const getData = async (shouldRefetch: boolean = false) => {
    await fetchData(shouldRefetch);
  };

  React.useEffect(() => {
    if (shouldFetchInitial) {
      fetchData();
    }
  }, [endpoint, shouldRefetch, ...Object.values(pathVariables)]);

  return { data, loading, error, getData };
};

export const usePost = <T>(
  endpoint: string,
  pathVariables: Record<string, string | number | undefined> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const postData = async <D>(postData?: D, isFormData = false) => {
    const authService = AuthService.getInstance();

    try {
      setLoading(true);
      setError(null);

      if (authService.isTokenExpired() && !endpoint.includes("login")) {
        const refreshToken = authService.getRefreshToken();

        try {
          const response = await axios.post(
            `${API_BASE_URL}/refresh`,
            { refreshToken },
            axiosConfig
          );
          authService.decodeToken(response.data.accessToken);
        } catch (refreshError: any) {
          const errorResult = await handleResponse<T>(refreshError);
          setError(errorResult as IApiError);
          authService.clear();
          return;
        }
      }

      const url = buildUrl(endpoint, pathVariables);
      const headers: AxiosRequestConfig["headers"] = {
        ...axiosConfig.headers,
        Authorization: `Bearer ${authService.getToken() ?? ""}`,
      };

      let requestBody: any = postData;

      if (postData instanceof FormData && isFormData) {
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
    const authService = AuthService.getInstance();
    try {
      setLoading(true);
      setError(null);

      if (authService.isTokenExpired()) {
        const refreshToken = authService.getRefreshToken();

        try {
          const response = await axios.post(
            `${API_BASE_URL}/refresh`,
            { refreshToken },
            axiosConfig
          );
          authService.decodeToken(response.data.accessToken);
        } catch (refreshError: any) {
          const errorResult = await handleResponse<T>(refreshError);
          setError(errorResult as IApiError);
          authService.clear();
          return;
        }
      }
      const url = buildUrl(endpoint, pathVariables);
      const response = await axios.put(url, postData, {
        ...axiosConfig,
        headers: {
          ...axiosConfig.headers,
          Authorization: `Bearer ${authService.getToken() ?? ""}`,
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
    const authService = AuthService.getInstance();

    try {
      setLoading(true);
      setError(null);

      if (authService.isTokenExpired()) {
        const refreshToken = authService.getRefreshToken();

        try {
          const response = await axios.post(
            `${API_BASE_URL}/refresh`,
            { refreshToken },
            axiosConfig
          );
          authService.decodeToken(response.data.accessToken);
        } catch (refreshError: any) {
          const errorResult = await handleResponse<T>(refreshError);
          setError(errorResult as IApiError);
          authService.clear();
          return;
        }
      }

      const url = buildUrl(endpoint, pathVariables);
      const response = await axios.delete(url, {
        ...axiosConfig,
        headers: {
          ...axiosConfig.headers,
          Authorization: `Bearer ${authService.getToken() ?? ""}`,
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
