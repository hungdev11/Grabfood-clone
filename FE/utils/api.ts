/**
 * Utility function for making authenticated API requests with timeout
 */
export async function fetchWithAuth(url: string, options: RequestInit = {}, timeoutMs: number = 10000) {
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
    
    // Create an AbortController to handle timeout
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeoutMs);
    
    try {
      // Return fetch with auth headers and signal
      const response = await fetch(url, {
        ...options,
        headers,
        signal: controller.signal
      });
      
      return response;
    } catch (error) {
      // If it's an abort error, convert to a more descriptive error
      if (error instanceof DOMException && error.name === 'AbortError') {
        throw new Error(`Request timeout after ${timeoutMs}ms`);
      }
      throw error;
    } finally {
      clearTimeout(timeoutId);
    }
  } 
  
  // During server-side rendering, just make the request without token
  return fetch(url, options);
}