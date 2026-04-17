export type OperationFeedbackType = 'success' | 'info' | 'warning' | 'error';

export interface OperationFeedback {
  type: OperationFeedbackType;
  message: string;
  fieldErrors: Record<string, string> | null;
  retryable: boolean;
}
