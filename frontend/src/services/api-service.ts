import React from "react";
import { IApiError } from "../shared/models/ApiError";

const API_BASE_URL = "http://localhost:8080/api";

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    if (response.status === 422) {
      const error = (await response.json()) as IApiError;
      const regex = /(\w+)\.(\w+): (.*?)(,|$)/g;
      let match;
      let messages = new Map<string, string>();

      while ((match = regex.exec(error.description)) !== null) {
        messages.set(match[2], match[3]);
      }

      error.description = JSON.stringify(Object.fromEntries(messages));

      throw error;
    } else if (response.status === 404 || response.status === 403) {
      const mappedError = {
        statusCode: response.status,
        message: response.status === 404 ? "Unauthorized" : "Forbidden",
      };

      throw mappedError;
    }

    const error = (await response.json()) as IApiError;
    throw error;
  }

  return response.json();
}

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
        setLoading(true);
        setError(null);
        const url = buildUrl(endpoint, pathVariables);
        const response = await fetch(url);
        const result = await handleResponse<T>(response);
        setData(result);
      } catch (error: any) {
        setError(error as IApiError);
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
  pathVariables: Record<string, string | number> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const postData = async (postData: Record<string, any>) => {
    try {
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);
      const response = await fetch(url, {
        method: "POST",
        body: JSON.stringify(postData),
        headers: {
          "Content-Type": "application/json",
        },
      });

      const result = await handleResponse<T>(response);
      setData(result);
    } catch (error: any) {
      setError(error as IApiError);
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
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);
      const response = await fetch(url, {
        method: "PUT",
        body: JSON.stringify(postData),
        headers: {
          "Content-Type": "application/json",
        },
      });

      const result = await handleResponse<T>(response);
      setData(result);
    } catch (error: any) {
      setError(error as IApiError);
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, editData };
};

export const useDelete = (
  endpoint: string,
  pathVariables: Record<string, string | number> = {}
) => {
  const [data, setData] = React.useState<boolean | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const deleteData = async () => {
    try {
      setLoading(true);
      setError(null);
      const url = buildUrl(endpoint, pathVariables);
      const response = await fetch(url, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const result = await handleResponse<boolean>(response);
      setData(result);
    } catch (error: any) {
      setError(error as IApiError);
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, deleteData };
};