import axiosInstance from './axiosInstance';

// Interface definitions for reminders
export interface ReminderRequest {
  title: string;
  description: string;
  reminderTime: string; // ISO date string
  emailEnabled: boolean;
}

export interface ReminderResponse {
  id: string;
  title: string;
  description: string;
  reminderTime: string; // ISO date string
  isProcessed: boolean;
  userId: string;
}

// Interface for API response
export interface ReminderApiResponse {
  data: ReminderResponse[] | ReminderResponse;
  message: string;
  code: number;
}

// Get all reminders for a user
export const getUserReminders = async (): Promise<ReminderApiResponse> => {
  try {
    const response = await axiosInstance.get(`/grab/reminders`);
    return response.data;
  } catch (error) {
    console.error('Error fetching reminders:', error);
    throw error;
  }
};

// Create a new reminder
export const createReminder = async (reminder: ReminderRequest): Promise<ReminderApiResponse> => {
  try {
    const response = await axiosInstance.post(`/grab/reminders`, reminder);
    return response.data;
  } catch (error) {
    console.error('Error creating reminder:', error);
    throw error;
  }
};

// Delete a reminder
export const deleteReminder = async (reminderId: string): Promise<ReminderApiResponse> => {
  try {
    const response = await axiosInstance.delete(`/grab/reminders/${reminderId}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting reminder:', error);
    throw error;
  }
};

// Delete all reminders for a user
export const deleteAllReminders = async (): Promise<ReminderApiResponse> => {
  try {
    const response = await axiosInstance.delete(`/grab/reminders`);
    return response.data;
  } catch (error) {
    console.error('Error deleting all reminders:', error);
    throw error;
  }
};

// // Update a reminder
// export const updateReminder = async (reminderId: string, reminder: ReminderRequest): Promise<ReminderApiResponse> => {
//   try {
//     const response = await axiosInstance.put(`/grab/reminders/${reminderId}`, reminder);
//     return response.data;
//   } catch (error) {
//     console.error('Error updating reminder:', error);
//     throw error;
//   }
// };
