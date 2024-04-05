import { useNavigate } from "react-router-dom";
import AuthService from "./jwt-service";

const useErrorHandling = () => {
  const navigate = useNavigate();

  const handleErrorResponse = (statusCode: number) => {
    const authService = AuthService.getInstance();

    switch (statusCode) {
      case 401:
        authService.clear();
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
