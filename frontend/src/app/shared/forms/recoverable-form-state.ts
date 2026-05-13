import { OperationFeedback } from '../models/operation-feedback.model';

export interface RecoverableFormState<T> {
  draft: Partial<T>;
  feedback: OperationFeedback | null;
}

export function createRecoverableFormState<T>(
  draft: Partial<T> = {},
  feedback: OperationFeedback | null = null
): RecoverableFormState<T> {
  return {
    draft,
    feedback
  };
}

export function preserveRecoverableDraft<T>(
  current: RecoverableFormState<T>,
  draft: Partial<T>,
  feedback: OperationFeedback
): RecoverableFormState<T> {
  return {
    draft: {
      ...current.draft,
      ...draft
    },
    feedback
  };
}
