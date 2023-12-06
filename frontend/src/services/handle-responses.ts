import { useNavigate } from "react-router-dom";

const useErrorHandling = () => {
  const navigate = useNavigate();

  const handleErrorResponse = (statusCode: number) => {
    switch (statusCode) {
      case 401:
        localStorage.clear();
        navigate("/");
        break;
      case 403:
        navigate(-1);
        break;
      default:
        break;
    }
  };

  return { handleErrorResponse };
};

export default useErrorHandling;