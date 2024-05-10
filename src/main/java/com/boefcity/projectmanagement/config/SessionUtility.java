package com.boefcity.projectmanagement.config;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
public class SessionUtility {

        public static boolean isNotAuthenticated(HttpSession session, RedirectAttributes redirectAttributes) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("message", "Please login first.");
                return true;
            }
            return false;
        }

}
