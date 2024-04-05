import { jwtDecode } from "jwt-decode";

interface DecodedToken {
  role: string;
  sub: string;
  exp: number;
}

class AuthService {
  private static instance: AuthService;
  private role: string | null = null;
  private username: string | null = null;
  private token: string | null = null;
  private refreshToken: string | null = null;
  private exp: number | null = null;

  private constructor() {}

  static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  public setToken(token: string, decodedToken: DecodedToken): void {
    this.token = token;
    if (decodedToken) {
      this.role = decodedToken.role;
      this.username = decodedToken.sub;
      this.exp = decodedToken.exp;
    }
  }

  public setRefreshToken(refreshToken: string): void {
    this.refreshToken = refreshToken;
  }

  public getToken(): string | null {
    return this.token;
  }

  public getRefreshToken(): string | null {
    return this.refreshToken;
  }

  public getRole(): string | null {
    return this.role;
  }

  public getUsername(): string | null {
    return this.username;
  }

  public isAuthenticated(): boolean {
    return !!this.token;
  }

  public decodeToken(token: string): DecodedToken | null {
    if (token) {
      try {
        const decoded = jwtDecode(token);
        this.setToken(token, decoded as DecodedToken);
        return decoded as DecodedToken;
      } catch (error) {
        console.error("Error decoding token:", error);
        return null;
      }
    }
    return null;
  }

  public isTokenExpired(): boolean {
    const now = Math.floor(Date.now() / 1000);
    return this.exp ? this.exp < now : true;
  }

  public clear(): void {
    this.token = null;
    this.refreshToken = null;
    this.role = null;
    this.username = null;
    this.exp = null;
  }
}

export default AuthService;
