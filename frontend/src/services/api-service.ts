import React from "react";
import { IApiError } from "../shared/models/ApiError";

const API_BASE_URL = "http://localhost:8080/api";

const handleResponse = async <T>(response: Response): Promise<T> => {
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "Something went wrong");
  }
  return response.json();
};

const buildUrl = (
    endpoint: string,
    pathVariables: Record<string, string | number> = {}
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
  pathVariables: Record<string, string | number> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<IApiError | null>(null);

  React.useEffect(() => {
    const fetchData = async () => {
      try {
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
  }, [endpoint]);

  return { data, loading, error };
};

export const usePost = <T>(endpoint: string) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const postData = async (postData: Record<string, any>) => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
        method: "POST",
        body: JSON.stringify(postData),
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
  pathVariables: Record<string, string | number> = {}
) => {
  const [data, setData] = React.useState<T | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<IApiError | null>(null);

  const editData = async (postData: Record<string, any>) => {
    setLoading(true);
    try {
      const url = buildUrl(endpoint, pathVariables);
      const response = await fetch(url, {
        method: "PUT",
        body: JSON.stringify(postData),
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
    setLoading(true);
    try {
      const url = buildUrl(endpoint, pathVariables);
      const response = await fetch(url, {
        method: "DELETE",
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