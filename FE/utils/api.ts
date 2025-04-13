/**
 * Utility function for making authenticated API requests
 */
export async function fetchWithAuth(url: string, options: RequestInit = {}) {
  // Check if we're running on the client (not during server rendering)
  if (typeof window !== 'undefined') {
    // Get token from localStorage
    const token = localStorage.getItem('grabToken');
    
    // Create headers with Authorization if token exists
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
    
    // Return fetch with auth headers
    return fetch(url, {
      ...options,
      headers
    });
  } 
  
  // During server-side rendering, just make the request without token
  return fetch(url, options);
}