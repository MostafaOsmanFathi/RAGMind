export interface ChatRecordModel {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp: string;
}
