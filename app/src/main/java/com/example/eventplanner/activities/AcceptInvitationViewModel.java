package com.example.eventplanner.activities;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.InvitationRepository;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.JwtUtils;
import com.example.eventplanner.clients.utils.UserEmailUtils;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.dto.auth.LoginResponseDto;
import com.example.eventplanner.dto.auth.QuickLoginDto;
import com.example.eventplanner.dto.event.InvitationErrorDto;
import com.example.eventplanner.model.event.Invitation;
import com.example.eventplanner.utils.ObserverTracker;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;

public class AcceptInvitationViewModel extends ViewModel {

    private final InvitationRepository invitationRepository = new InvitationRepository();
    private final ObserverTracker tracker = new ObserverTracker();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    private final MutableLiveData<Boolean> invitationAccepted = new MutableLiveData<>();
    public LiveData<Boolean> getInvitationAccepted() { return invitationAccepted; }

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() { return errorMessage; }

    private final MutableLiveData<Long> navigateToEventId = new MutableLiveData<>();
    public LiveData<Long> getNavigateToEventId() { return navigateToEventId; }

    private final MutableLiveData<Boolean> navigateToHome = new MutableLiveData<>();
    public LiveData<Boolean> getNavigateToHome() { return navigateToHome; }

    private final MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    public LiveData<Boolean> getNavigateToLogin() { return navigateToLogin; }

    public void acceptInvitation(Context context, String token) {
        if (token == null || token.isEmpty()) {
            navigateToHome.setValue(true);
            return;
        }

        isLoading.setValue(true);
        
        tracker.observeOnce(invitationRepository.acceptInvitationWithErrorHandling(token), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    handleInvitationSuccess(context, token, result.getInvitation());
                } else {
                    handleInvitationError(context, result.getError(), token);
                }
            } else {
                handleInvitationError(null, null, token);
            }
        });
    }

    private void handleInvitationSuccess(Context context, String token, Invitation invitation) {
        if (invitation.isQuickRegistration()) {
            handleQuickRegistration(context, invitation, token);
        } else if (isUserLoggedIn(context)) {
            navigateToEvent(invitation.getEventDto().getId());
            errorMessage.setValue("Successfully accepted invitation");
        } else {
            navigateToHome.setValue(true);
        }
        isLoading.setValue(false);
    }

    private void handleQuickRegistration(Context context, Invitation invitation, String token) {
        if (isUserLoggedIn(context)) {
            navigateToEvent(invitation.getEventDto().getId());
            errorMessage.setValue("Successfully accepted invitation");
        } else {
            quickLogin(context, token, invitation.getEventDto().getId(), invitation.isJustRegistered(), false);
        }
    }

    private void handleInvitationError(Context context, InvitationErrorDto errorDto, String token) {
        Log.e("AcceptInvitation", "Failed to accept invitation");
        
        if (errorDto != null) {
            switch (errorDto.getType()) {
                case UNAUTHORIZED_QUICK_REGISTRATION:
                    quickLogin(context, token, errorDto.getEventId(), false, false);
                    break;
                case EVENT_FULL_QUICK_REGISTRATION:
                    quickLogin(context, token, errorDto.getEventId(), false, true);
                    break;
                case UNAUTHORIZED:
                    navigateToLogin.setValue(true);
                    errorMessage.setValue("Please sign in to accept invitation");
                    break;
                case EVENT_FULL:
                    if (errorDto.getEventId() != null)
                        navigateToEvent(errorDto.getEventId());
                    else
                        navigateToHome.setValue(true);

                    errorMessage.setValue("Failed to accept invitation, the event is full");
                    break;
                case FORBIDDEN:
                    navigateToHome.setValue(true);
                    errorMessage.setValue("This invitation is for another user");
                    break;
                case EVENT_NOT_FOUND:
                    navigateToHome.setValue(true);
                    errorMessage.setValue("Event could not be found");
                    break;
                case INVITATION_NOT_FOUND:
                default:
                    navigateToHome.setValue(true);
                    errorMessage.setValue("Invitation could not be found");
                    break;
            }
        } else {
            navigateToHome.setValue(true);
            errorMessage.setValue("Failed to accept invitation");
        }
        
        isLoading.setValue(false);
    }

    private void quickLogin(Context context, String token, Long eventId, boolean justRegistered, boolean isFull) {
        Call<LoginResponseDto> call =
            ClientUtils.authService.quickLogin(new QuickLoginDto(token));
        
        call.enqueue(new SimpleCallback<>(
            response -> {
                if (response.body() != null) {
                    JwtUtils.saveJwtToken(context, response.body().getJwt());
                    UserIdUtils.saveUserId(context, response.body().getId());
                    UserRoleUtils.saveUserRole(context, response.body().getRole());
                    UserEmailUtils.saveUserEmail(context, response.body().getEmail());
                    
                    if (eventId != null) {
                        navigateToEvent(eventId);
                    } else {
                        navigateToHome.setValue(true);
                    }
                    
                    if (justRegistered) {
                        errorMessage.setValue("Welcome! An account has been created for you, check your notifications for further instructions");
                    } else if (isFull) {
                        errorMessage.setValue("Failed to accept invitation, the event is full. You have been signed in");
                    } else {
                        String userRole = com.example.eventplanner.clients.utils.UserRoleUtils.getUserRole(context);
                        if ("AUTHENTICATED".equals(userRole)) {
                            errorMessage.setValue("Welcome! You can now upgrade your account to access more features.");
                        }
                    }
                } else {
                    navigateToHome.setValue(true);
                    errorMessage.setValue("Failed to accept invitation");
                }
                isLoading.setValue(false);
            },
            error -> {
                navigateToHome.setValue(true);
                if (error != null && error.first != null && error.first.body() != null) {
                    try { // Suspended account
                        String errorBody = error.first.errorBody().string();
                        JsonObject errorJson = new JsonParser().parse(errorBody).getAsJsonObject();
                        if (errorJson.has("suspendedAt")) {
                            errorMessage.setValue("Your account is temporarily suspended"); //TODO: Replace with dialog
                        } else {
                            errorMessage.setValue("Failed to accept invitation");
                        }
                    } catch (Exception e) {
                        errorMessage.setValue("Failed to accept invitation");
                    }
                } else {
                    errorMessage.setValue("Failed to accept invitation");
                }
                isLoading.setValue(false);
            }
        ));
    }

    private void navigateToEvent(Long eventId) {
        if (eventId != null) {
            navigateToEventId.setValue(eventId);
        } else {
            navigateToHome.setValue(true);
        }
    }

    private boolean isUserLoggedIn(Context context) {
        return JwtUtils.getJwtToken(context) != null && !JwtUtils.getJwtToken(context).isEmpty();
    }


    public void resetNavigation() {
        navigateToEventId.setValue(null);
        navigateToHome.setValue(null);
        navigateToLogin.setValue(null);
        errorMessage.setValue(null);
        invitationAccepted.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
