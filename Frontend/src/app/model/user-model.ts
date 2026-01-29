export interface UserModel {
  id?: number;
  name: string;
  email: string;
  phoneNumber?: string;
  accessToken?: string;
  refreshToken?: string;
}
