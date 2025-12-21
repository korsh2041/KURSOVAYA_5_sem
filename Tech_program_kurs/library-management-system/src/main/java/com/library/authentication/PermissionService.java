package com.library.authentication;

import com.library.users.User;
import com.library.users.Student;
import com.library.users.Professor;
import com.library.users.Librarian;

public class PermissionService {
    public boolean hasPermission(User user, String permission) {
        if (user instanceof Librarian) {
            return true;
        } else if (user instanceof Professor) {
            return !permission.equals("MANAGE_USERS");
        } else if (user instanceof Student) {
            return permission.equals("BORROW_BOOKS") || permission.equals("VIEW_CATALOG");
        }
        return false;
    }
}