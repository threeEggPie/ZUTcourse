package xyz.kingsword.course.service;

import lombok.NonNull;
import xyz.kingsword.course.pojo.User;

public interface UserService {
    User login(User user);

    int resetPassword(@NonNull String newPassword, @NonNull User user);

    Object getUserInfo(User user);

}
