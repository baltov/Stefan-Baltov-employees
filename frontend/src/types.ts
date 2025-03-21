export interface FileUploadDto {
  id: number; // Corresponds to Long
  fileName: string; // Corresponds to String
  created: string; // Use ISO 8601 string for LocalDateTime
  status: FileStatusEnum; // Enum type
  result: string | null; // Corresponds to String
}

export enum FileStatusEnum {
  UPLOADED = "UPLOADED",
  PROCESSING = "PROCESSING",
  PROCESSED = "PROCESSED"
}

export interface ErrorDto {
  message: string; // Corresponds to String
  fileId: number; // Corresponds to Long
  lineNumber: number; // Corresponds to Long
  created: string; // Use ISO 8601 string for LocalDateTime
}

export interface ReportDto {
  projectId: number; // To account for Long being nullable in Java
  oneUserId: number;
  secondUserId: number;
  days: number; // long in Java translates to number in TypeScript
}