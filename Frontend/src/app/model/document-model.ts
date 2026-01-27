export interface DocumentModel {
  id: string;
  name: string;
  size: string;
  uploadedAt: string;
  status: "processing" | "indexed" | "error";
}
