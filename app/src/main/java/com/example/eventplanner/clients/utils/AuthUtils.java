package com.example.eventplanner.clients.utils;

import android.content.Context;

public class AuthUtils {
    
    public static boolean isLoggedIn(Context context) {
        return JwtUtils.getJwtToken(context) != null && !JwtUtils.getJwtToken(context).isEmpty();
    }
    
    public static boolean hasRole(Context context, String role) {
        String userRole = UserRoleUtils.getUserRole(context);
        return role != null && role.equals(userRole);
    }
    
    public static boolean isAuthenticatedUser(Context context) {
        return hasRole(context, "AUTHENTICATED");
    }
    
    public static boolean isEventOrganizer(Context context) {
        return hasRole(context, "EVENT_ORGANIZER");
    }
    
    public static boolean isServiceProductProvider(Context context) {
        return hasRole(context, "SERVICE_PRODUCT_PROVIDER");
    }
    
    public static boolean isAdmin(Context context) {
        return hasRole(context, "ADMIN");
    }
    
    public static boolean canUpgradeAccount(Context context) {
        return isLoggedIn(context) && isAuthenticatedUser(context);
    }

    public static String getUserEmail(Context context) {
        return UserEmailUtils.getUserEmail(context);
    }
    
    public static void logout(Context context) {
        JwtUtils.clearJwtToken(context);
        UserIdUtils.clearUserId(context);
        UserRoleUtils.clearUserRole(context);
    }
}
