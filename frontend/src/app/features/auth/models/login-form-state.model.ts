export type LoginSubmitStatus = 'idle' | 'loading' | 'success' | 'error';

export interface LoginFormState {
  email: string;
  password: string;
  submitStatus: LoginSubmitStatus;
  errorMessage: string | null;
}
