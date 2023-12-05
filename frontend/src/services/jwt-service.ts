import { jwtDecode } from "jwt-decode";

class AuthService {
  private static instance: AuthService;

  private constructor() {}

  static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  public getToken(): string | null {
    return localStorage.getItem("token");
  }

  public getRefreshToken(): string | null {
    return localStorage.getItem("refreshToken");
  }

  public isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token;
  }

  public decodeToken(): { role: string; sub: string; exp: number } | null {
    const token = this.getToken();

    if (token) {
      try {
        const decoded = jwtDecode(token);
        return decoded as { role: string; sub: string; exp: number };
      } catch (error) {
        console.error("Error decoding token:", error);
        return null;
      }
    }

    return null;
  }

  public isTokenExpired(): boolean {
    const decodedToken = this.decodeToken();
    const now = Math.floor(Date.now() / 1000);

    return decodedToken ? decodedToken.exp < now : true;
  }

  public clearLocalStorage(): void {
    localStorage.clear();
  }
}

export default AuthService;