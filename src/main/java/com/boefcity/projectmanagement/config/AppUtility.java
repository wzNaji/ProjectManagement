package com.boefcity.projectmanagement.config;
import com.boefcity.projectmanagement.model.Role;
import com.boefcity.projectmanagement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
public class AppUtility {

        public static boolean isNotAuthenticated(HttpSession session, RedirectAttributes redirectAttributes) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("message", "Venligst log ind.");
                return true;
            }
            return false;
        }
    public static boolean isAdminOrManager(User user) {
        Role role = user.getUserRole();
        return Role.ADMIN.equals(role) || Role.MANAGER.equals(role);
    }

}
