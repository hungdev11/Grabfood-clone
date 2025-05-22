import { jwtDecode } from 'jwt-decode';
import axios from 'axios';

// Interface for decoded token data
interface DecodedToken {
  sub?: string;
  username?: string;
  roles?: string[] | string;
  authorities?: string[] | string;
  scope?: string;
  role?: string[] | string;
  exp?: number;
}

// Interface for restaurant info
interface RestaurantInfo {
  data: number;
  name: string;
}

/**
 * Checks if a JWT token is expired
 * 
 * @param exp Expiration timestamp from JWT
 * @returns Boolean indicating if token is expired
 */
const isTokenExpired = (exp?: number): boolean => {
  if (!exp) return true;
  // Convert exp to milliseconds (JWT exp is in seconds)
  return Date.now() >= exp * 1000;
};

/**
 * Extract user roles from JWT token
 * 
 * @returns Array of user roles or null if token is invalid
 */
export const getUserRolesFromToken = (): string[] | null => {
  try {
    const token = localStorage.getItem('grabToken');
    if (!token) return null;
    
    const decoded = jwtDecode<DecodedToken>(token);
    console.log('Decoded token:', decoded);
    
    // First check if roles are in the decoded.roles field
    if (decoded.roles) {
      if (Array.isArray(decoded.roles)) {
        return decoded.roles;
      } else if (typeof decoded.roles === 'string') {
        return decoded.roles.split(',').map(r => r.trim());
      }
    }
    
    // If not in roles, check other potential properties
    const potentialRoles = decoded.authorities || decoded.scope || decoded.role;
    
    if (potentialRoles) {
      // If it's a string, it might need splitting
      if (typeof potentialRoles === 'string') {
        return potentialRoles.split(',').map(r => r.trim());
      }
      
      // If it's an array, return it directly
      if (Array.isArray(potentialRoles)) {
        return potentialRoles;
      }
    }
    
    // Check if token is expired
    if (isTokenExpired(decoded.exp)) {
      localStorage.removeItem('grabToken');
      localStorage.removeItem('grabUserId');
      return null;
    }
    
    return [];
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};

/**
 * Get username from JWT token
 * 
 * @returns Username string or null if token is invalid
 */
export const getUsernameFromToken = (): string | null => {
  try {
    const token = localStorage.getItem('grabToken');
    if (!token) return null;

    const decoded = jwtDecode<DecodedToken>(token);
    // Try to find username in different fields
    return decoded.username || decoded.sub || null;
  } catch (error) {
    console.error('Error getting username from token:', error);
    return null;
  }
};

/**
 * Fetch restaurant information by username
 * 
 * @param username Username of restaurant owner
 * @returns Restaurant info object or null if not found
 */
export const getRestaurantByUsername = async (username: string): Promise<RestaurantInfo | null> => {
  try {
    console.log(`Fetching restaurant info for username: ${username}`);
    const response = await axios.get(`http://localhost:6969/grab/restaurants/username/${username}`);
    console.log('Restaurant API response:', response.data);
    
    // Handle different response formats
    if (response.data && response.data.data) {
      return response.data;
    }
    else {
      console.error('Restaurant info missing ID in response:', response.data);
      return null;
    }
  } catch (error) {
    console.error('Error fetching restaurant info:', error);
    return null;
  }
};

/**
 * Handle successful login and redirect based on user role
 * 
 * @param tokenFromServer Token string from server (userId#token format)
 */
export const handleLoginSuccess = async (tokenFromServer: string): Promise<void> => {
  try {
    // Step 1: Parse the token from server (in format userId#token)
    const [userIdPart, jwtPart] = tokenFromServer.split('#');
    
    // Validate token parts
    if (!userIdPart || !jwtPart) {
      throw new Error('Invalid token format received from server');
    }
    
    // Step 2: Store token parts in localStorage
    localStorage.setItem('grabUserId', userIdPart);
    localStorage.setItem('grabToken', jwtPart);
    
    // Step 3: Decode JWT to get user information
    const decoded = jwtDecode<DecodedToken>(jwtPart);
    console.log('Decoded token for login:', decoded);
    
    // Step 4: Check if token is expired
    if (isTokenExpired(decoded.exp)) {
      console.log('Token is expired, logging out');
      localStorage.removeItem('grabToken');
      localStorage.removeItem('grabUserId');
      window.location.href = '/login';
      return;
    }
    
    // Step 5: Extract user roles
    let roles: string[] = [];
    
    // Check for roles in different possible fields
    if (decoded.roles) {
      if (Array.isArray(decoded.roles)) {
        roles = decoded.roles;
      } else if (typeof decoded.roles === 'string') {
        roles = decoded.roles.split(',').map(r => r.trim());
      }
    } else if (decoded.authorities) {
      if (Array.isArray(decoded.authorities)) {
        roles = decoded.authorities;
      } else if (typeof decoded.authorities === 'string') {
        roles = decoded.authorities.split(',').map(r => r.trim());
      }
    } else if (decoded.role) {
      if (Array.isArray(decoded.role)) {
        roles = decoded.role;
      } else if (typeof decoded.role === 'string') {
        roles = [decoded.role];
      }
    }
    
    console.log('Extracted roles:', roles);
    
    // Step 6: Extract username
    const username = decoded.username || decoded.sub;
    
    if (!username) {
      console.warn('No username found in token');
    } else {
      console.log('Username from token:', username);
    }
    
    // Step 7: Redirect based on role
    if (roles.includes('ROLE_ADMIN')) {
      console.log('Redirecting admin to admin panel');
      window.location.href = '/admin';
      return;
    } 
    
    if (roles.includes('ROLE_RES')) {
      if (!username) {
        console.warn('No username for restaurant owner, redirecting to home');
        window.location.href = '/';
        return;
      }
      
      try {
        // Fetch restaurant info using username
        const restaurant = await getRestaurantByUsername(username);
        console.log('Fetched restaurant info:', restaurant);
        const restaurantIdNum = restaurant?.data;

        if (restaurantIdNum !== undefined && restaurantIdNum !== null) {
            const restaurantId = restaurantIdNum.toString();
          // Store restaurant ID in localStorage
          localStorage.setItem('restaurantId', restaurantId);
          console.log(`Redirecting to restaurant admin: /admin-restaurant/${restaurantId}`);
          window.location.href = `/admin-restaurant/${restaurantId}`;
          return;
        } else {
          console.error('No restaurant found for this user, redirecting to home');
          window.location.href = '/';
          return;
        }
      } catch (error) {
        console.error('Error during restaurant redirect:', error);
        window.location.href = '/';
        return;
      }
    }
    
    if (roles.includes('ROLE_USER') || roles.length === 0) {
      console.log('Redirecting regular user to home page');
      window.location.href = '/';
      return;
    }
    
    // Default fallback
    console.log('No specific role matched, defaulting to home page');
    window.location.href = '/';
    
  } catch (error) {
    console.error('Error in handleLoginSuccess:', error);
    alert('An error occurred during login. Please try again.');
    window.location.href = '/login';
  }
};

/**
 * Perform logout
 */
export const logout = (): void => {
  localStorage.removeItem('grabToken');
  localStorage.removeItem('grabUserId');
  localStorage.removeItem('restaurantId');
  window.location.href = '/login';
};
