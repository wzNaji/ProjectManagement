/*package com.boefcity.projectmanagement.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred: " + exception.getMessage());
        return "redirect:/errorPage";
    }

}

 */
