package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.ModifyUserRequest;
import com.nexsol.tpa.core.api.controller.v1.request.SignUpRequest;
import com.nexsol.tpa.core.api.controller.v1.response.UserResponse;
import com.nexsol.tpa.core.api.support.response.ApiResponse;
import com.nexsol.tpa.core.domain.User;
import com.nexsol.tpa.core.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal Long userId) {
        User user = userService.findUser(userId);

        return ApiResponse.success(UserResponse.of(user));

    }

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@RequestBody @Valid SignUpRequest request) {

        User savedUser = userService.signUp(request.toNewUser());

        return ApiResponse.success(UserResponse.of(savedUser));
    }

    @PatchMapping("/update")
    public ApiResponse<UserResponse> update(@AuthenticationPrincipal Long userId, @RequestBody @Valid ModifyUserRequest request) {
        User updatedUser = userService.update(userId, ModifyUserRequest.toModifyUser(request));

        return ApiResponse.success(UserResponse.of(updatedUser));

    }

}