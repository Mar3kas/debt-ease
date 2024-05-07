import SockJS from "sockjs-client";
import Stomp from "stompjs";

class WebSocketService {
  private static instance: WebSocketService | null = null;

  static getInstance(url: string): WebSocketService {
    if (!WebSocketService.instance) {
      WebSocketService.instance = new WebSocketService(url);
    }
    return WebSocketService.instance;
  }

  private socketUrl: string;
  private stompClient: Stomp.Client | null;
  private subscriptions: Map<string, (message: any) => void>;
  private isConnected: boolean;

  private constructor(url: string) {
    this.socketUrl = url;
    this.stompClient = null;
    this.subscriptions = new Map();
    this.isConnected = false;
  }

  connect() {
    if (!this.stompClient || !this.isConnected) {
      const socket = new SockJS(this.socketUrl);
      this.stompClient = Stomp.over(socket);

      this.stompClient.connect(
        {},
        () => this.onConnect(),
        (error) => this.onError(error)
      );
    }
  }

  onConnect() {
    console.log("WebSocket connected");
    this.isConnected = true;
    this.subscriptions.forEach((callback, topic) =>
      this.subscribe(topic, callback, true)
    );
  }

  onError(error: any) {
    console.error("Connection error", error);
    this.isConnected = false;
  }

  subscribe(
    topic: string,
    callback: (message: any) => void,
    isResubscribe: boolean = false
  ) {
    const connectAndSubscribe = () => {
      if (!this.isConnected) {
        this.connect();
      } else {
        this.stompClient?.subscribe(topic, (message) =>
          this.onMessage(message, callback)
        );
      }
    };

    if (!this.stompClient || !this.isConnected) {
      connectAndSubscribe();
    } else {
      if (!isResubscribe) {
        this.subscriptions.set(topic, callback);
      }

      this.stompClient.subscribe(topic, (message) =>
        this.onMessage(message, callback)
      );
    }
  }

  onMessage(message: Stomp.Message, callback: (message: any) => void) {
    if (message.body) {
      const body = JSON.parse(message.body);
      callback(body);
    }
  }

  unsubscribeAll() {
    this.subscriptions.clear();
  }

  disconnect() {
    if (this.stompClient && this.isConnected) {
      this.unsubscribeAll();
      this.stompClient.disconnect(() => this.onDisconnect());
      this.stompClient = null;
      this.isConnected = false;
    }
  }

  onDisconnect() {
    console.log("WebSocket disconnected");
    this.isConnected = false;
  }
}

export default WebSocketService;
